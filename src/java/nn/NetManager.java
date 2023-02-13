package nn;

import constant.AllConstant;
import nn.neuron.HideNeuron;
import nn.neuron.InputNeuron;
import nn.neuron.OutputNeuron;

/**
 * @descript:
 * @author: baomengyang <baomengyang@sina.cn>
 * @create: 2023-02-08 10:57
 */
public class NetManager implements AllConstant {

    private final InputNeuron[] inputNeurons;

    private final HideNeuron[][] hideNeuronTwo;
    private final int hideLastIndex;
    private final OutputNeuron[] outputNeurons;

    public NetManager(int inputNum, int hideLevel, int hideNum, int outNum) {
        inputNeurons = new InputNeuron[inputNum];
        for (int i = 0; i < inputNum; i++) {
            inputNeurons[i] = new InputNeuron();
        }

        hideNeuronTwo = new HideNeuron[hideLevel][];
        for (int i = 0; i < hideLevel; i++) {
            hideNeuronTwo[i] = new HideNeuron[hideNum];
            for (int j = 0; j < hideNum; j++) {
                hideNeuronTwo[i][j] = new HideNeuron(i == 0 ? inputNum : hideNum, 1 * Math.pow(0.1, i));
            }
        }
        hideLastIndex = hideLevel - 1;

        outputNeurons = new OutputNeuron[outNum];
        for (int i = 0; i < outNum; i++) {
            outputNeurons[i] = new OutputNeuron(hideNum);
        }
    }

    public void inputData(int[] data) {
        if (data == null || data.length == 0) {
            throw new RuntimeException("input data is null");
        }

        if (data.length > inputNeurons.length) {
            throw new RuntimeException("input data is too lang");
        }

        for (int i = 0; i < data.length; i++) {
            inputNeurons[i].setValue(data[i]);
        }

        for (int i = data.length; i < inputNeurons.length; i++) {
            inputNeurons[i].setValue(0);
        }
    }

    public OutputNeuron[] calculate() {
        for (int i = 0; i < hideNeuronTwo.length; i++) {
            HideNeuron[] hideNeuronOne = hideNeuronTwo[i];
            for (HideNeuron hideNeuron : hideNeuronOne) {
                hideNeuron.clearCache();

                if (i == 0) {
                    for (int j = 0; j < inputNeurons.length; j++) {
                        hideNeuron.addCacheResult(j, inputNeurons[j].getNextValue());
                    }
                } else {
                    for (int j = 0; j < hideNeuronOne.length; j++) {
                        hideNeuron.addCacheResult(j, hideNeuronTwo[i - 1][j].getNextValue());
                    }
                }
            }
        }

        for (OutputNeuron outputNeuron : outputNeurons) {
            outputNeuron.clearCache();
            HideNeuron[] hideNeurons = hideNeuronTwo[hideLastIndex];
            for (int i = 0; i < hideNeurons.length; i++) {
                outputNeuron.addCacheResult(i, hideNeurons[i].getNextValue());
            }
        }

        return outputNeurons;
    }

    public void success(int winIndex) {
        encourage(winIndex, FeedbackScore.GET_FOOD);
    }

    public void fail(int failIndex) {
        encourage(failIndex, FeedbackScore.FAIL);
    }

    public void encourage(int winIndex, FeedbackScore feedbackScore) {
        encourage(winIndex, feedbackScore, feedbackScore.addScore);
    }

    public void encourage(int winIndex, FeedbackScore feedbackScore, int score) {
        OutputNeuron outputNeuron = outputNeurons[winIndex];
        int hideLevel = hideNeuronTwo.length - 1;
        for (int hideToIndex : outputNeuron.topWeightChange(feedbackScore.num1, score)) {
            encourageHideToHide(hideLevel, hideToIndex, feedbackScore, score);
        }
    }

    public void encourageHideToHide(int hideLevel, int hideIndex, FeedbackScore feedbackScore, int score) {
        int toHideLevel = hideLevel - 1;
        int[] data = hideNeuronTwo[hideLevel][hideIndex].topWeightChange(hideLevel == 0 || toHideLevel == 0 ? feedbackScore.num3 : feedbackScore.num2, score);
        if (toHideLevel >= 0) {
            for (int hideToIndex : data) {
                encourageHideToHide(toHideLevel, hideToIndex, feedbackScore, score);
            }
        }
    }

    public void sout() {
        System.out.println("隐藏层参数");
        for (int i = 0; i < hideNeuronTwo.length; i++) {
            soutHideResult("第" + (i + 1) + "层", hideNeuronTwo[i]);
        }

        System.out.println("输出层参数");
        for (int i = 0; i < outputNeurons.length; i++) {
            soutOutWeight("第" + (i + 1) + "层", outputNeurons[i].getWeight());
        }
    }

    public void soutHideResult(String name, OutputNeuron[] array) {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        int less_Num = 0;
        int more_Num = 0;

        for (OutputNeuron data : array) {
            long result = data.getResult();
            min = Math.min(min, result);
            max = Math.max(max, result);

            if (result > 0) {
                more_Num++;
            } else if (result < 0) {
                less_Num++;
            }
        }

        System.out.printf("%-6s  最小值%10d 最大值%10d 负数量%3d 正数量%3d\n", name, min, max, less_Num, more_Num);
    }

    public void soutOutWeight(String name, int[] array) {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        int less_Num = 0;
        int more_Num = 0;

        for (int result : array) {
            min = Math.min(min, result);
            max = Math.max(max, result);

            if (result > 0) {
                more_Num++;
            } else if (result < 0) {
                less_Num++;
            }
        }

        System.out.printf("%-6s  最小值%10d 最大值%10d 负数量%3d 正数量%3d\n", name, min, max, less_Num, more_Num);
    }

}
