package example;

import constant.AllConstant;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import nn.NetManager;
import nn.neuron.OutputNeuron;

/**
 * @descript: 最简单的训练，出n个选择，训练AI选择一样的
 * @author: baomengyang <baomengyang@sina.cn>
 * @create: 2023-02-09 16:45
 */
public class Choose {

    private int[] data;
    private boolean ai = true;

    private final Random random = new Random();
    private NetManager netManager;

    public void init(NetManager netManager, int chooseNum) {
        this.netManager = netManager;
        data = new int[chooseNum];

        for (int i = 0; i < 10; i++) {
            calculate();
        }

        ai = false;

        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            calculate();
        }
    }

    int winTimes = 0;
    int failTimes = 0;
    int randomWeight = 30;

    private void calculate() {
        Arrays.fill(data, 0);
        int index = random.nextInt(data.length);

        data[index] = 1;

        netManager.inputData(data);

        OutputNeuron[] outputData = netManager.calculate();

        int maxIndex = -1;
        long maxResult = 0;

        for (int i = 0; i < outputData.length; i++) {
            if (outputData[i].getResult() > 0 && outputData[i].getResult() > maxResult) {
                maxIndex = i;
                maxResult = outputData[i].getResult();
            }
        }

        if (maxIndex < 0) {
            maxIndex = random.nextInt(outputData.length);
        }

        if (ai) {
            if (data[maxIndex] == 1) {
                netManager.encourage(maxIndex, AllConstant.FeedbackScore.GUESS_SUCCESS);
            } else {
                netManager.encourage(maxIndex, AllConstant.FeedbackScore.GUESS_FAIL);
            }
        } else {
            if (data[maxIndex] == 1) {
                winTimes++;
            } else {
                failTimes++;
            }
            System.out.printf("题目 %3s 答案 %3s win%4d fail%4d percent %6.2f%%\n", index, maxIndex, winTimes, failTimes, winTimes * 100.0 / (winTimes + failTimes));
        }
    }
}
