package nn.neuron;

/**
 * @descript: 隐藏层，其实也是输出层
 * @author: baomengyang <baomengyang@sina.cn>
 * @create: 2023-02-08 10:35
 */
public class HideNeuron extends OutputNeuron {

    private double p;

    /**
     * @param preAllNum 上层所有节点数量
     */
    public HideNeuron(int preAllNum, double p) {
        super(preAllNum);
        this.p = p;
    }

    @Override
    public void addCacheResult(int index, long add) {
        super.addCacheResult(index, (long) (add * p));
    }
}
