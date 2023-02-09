package nn;

import nn.neuron.HideNeuron;
import nn.neuron.InputNeuron;
import nn.neuron.OutputNeuron;

/**
 * @descript:
 * @author: baomengyang <baomengyang@sina.cn>
 * @create: 2023-02-08 10:57
 */
public class NetManager {

    private final InputNeuron[] inputNeurons;

    private final HideNeuron[] hideNeurons1;
    private final HideNeuron[] hideNeurons2;

    private final OutputNeuron[] outputNeurons;

    public NetManager(int inputNum, int hideNum, int outNum) {
        outputNeurons = new OutputNeuron[outNum];
        for (int i = 0; i < outNum; i++) {
            outputNeurons[i] = new OutputNeuron(hideNum);
        }

        hideNeurons2 = new HideNeuron[hideNum];
        for (int i = 0; i < hideNum; i++) {
            hideNeurons2[i] = new HideNeuron(hideNum, 0.01);
        }

        hideNeurons1 = new HideNeuron[hideNum];
        for (int i = 0; i < hideNum; i++) {
            hideNeurons1[i] = new HideNeuron(inputNum, 1);
        }

        inputNeurons = new InputNeuron[inputNum];
        for (int i = 0; i < inputNum; i++) {
            inputNeurons[i] = new InputNeuron();
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
        for (HideNeuron hideNeuron : hideNeurons1) {
            hideNeuron.clearCache();
            for (int i = 0; i < inputNeurons.length; i++) {
                hideNeuron.addCacheResult(i, inputNeurons[i].getNextValue());
            }
        }

        for (HideNeuron hideNeuron : hideNeurons2) {
            hideNeuron.clearCache();
            for (int i = 0; i < hideNeurons1.length; i++) {
                hideNeuron.addCacheResult(i, hideNeurons1[i].getNextValue());
            }
        }

        for (OutputNeuron outputNeuron : outputNeurons) {
            outputNeuron.clearCache();
            for (int i = 0; i < hideNeurons2.length; i++) {
                outputNeuron.addCacheResult(i, hideNeurons2[i].getNextValue());
            }
        }

        return outputNeurons;
    }

    private static final int WIN_ADD_SCORE = 5;
    private static final int FAIL_ADD_SCORE = -1;

    public void success(int winIndex) {
        for (int i = 0; i < outputNeurons.length; i++) {
            OutputNeuron outputNeuron = outputNeurons[i];
            if (i == winIndex) {
                for (int hide2Index : outputNeuron.topWeightChange(15, WIN_ADD_SCORE)) { //40
                    for (int hide1Index : hideNeurons2[hide2Index].topWeightChange(15, WIN_ADD_SCORE)) {
                        hideNeurons1[hide1Index].topWeightChange(35, WIN_ADD_SCORE);
                    }
                }
            }
        }
    }

    public void fail(int failIndex) {
        for (int hide2Index : outputNeurons[failIndex].topWeightChange(15, FAIL_ADD_SCORE)) {
            for (int hide1Index : hideNeurons2[hide2Index].topWeightChange(15, FAIL_ADD_SCORE)) {
                hideNeurons1[hide1Index].topWeightChange(35, FAIL_ADD_SCORE);
            }
        }
    }

//    public void sout() {
//        for (HideNeuron hideNeuron : hideNeurons) {
//            System.out.println(Arrays.toString(hideNeuron.getWeight()));
//        }
//        System.out.println("---------------");
//        for (OutputNeuron outputNeuron : outputNeurons) {
//            System.out.println(Arrays.toString(outputNeuron.getWeight()));
//        }
//    }

}
