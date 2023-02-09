package nn.neuron;

import snake.GamePanel;

/**
 * @descript: 输出层
 * @author: baomengyang <baomengyang@sina.cn>
 * @create: 2023-02-08 10:36
 */
public class OutputNeuron extends AbstractNeuron {

    //所有权重
    private int[] weight;

    //上层所有节点的计算结果缓存，用来作为下一层的输入，如果为输出层，则直接为计算结果
    private long cacheResult;

    /**
     * @param preAllNum 上层所有节点数量
     */
    public OutputNeuron(int preAllNum) {
        weight = new int[preAllNum];
    }

    public void clearCache() {
        cacheResult = 0;
    }

    public void addCacheResult(int index, long add) {
        cacheResult += add * weight[index];
    }

    public final long getResult() {
        return cacheResult;
    }

    public void setCacheResult(long cacheResult) {
        this.cacheResult = cacheResult;
    }

    @Override
    public long getNextValue() {
        return cacheResult;
    }

    public int[] topWeightChange(int topNum, int change) {
        int[] winIndex = new int[topNum];
        for (int i = 0; i < topNum; i++) {
            winIndex[i] = -1;
        }

        int lastIndex = topNum - 1;
        int startIndex = GamePanel.getRandomInt(weight.length - 10) + 10;

        iii(startIndex, weight.length - 1, winIndex, lastIndex);
        iii(0, startIndex - 1, winIndex, lastIndex);

        for (int index : winIndex) {
            weight[index] += change;
        }

        return winIndex;
    }

    public void iii(int start, int end, int[] winIndex, int lastIndex) {
        for (; start <= end; start++) {
            if (winIndex[lastIndex] == -1 || weight[start] > weight[winIndex[lastIndex]]) {
                winIndex[lastIndex] = start;

                for (int j = lastIndex; j > 0; j--) {
                    if (winIndex[j - 1] == -1 || weight[winIndex[j]] > weight[winIndex[j - 1]]) {
                        int topNum = winIndex[j];
                        winIndex[j] = winIndex[j - 1];
                        winIndex[j - 1] = topNum;
                    }
                }
            }
        }
    }

    public int[] getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return cacheResult + "";
    }
}
