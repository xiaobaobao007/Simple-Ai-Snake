package nn.neuron;

/**
 * @descript: 输入层
 * @author: baomengyang <baomengyang@sina.cn>
 * @create: 2023-02-08 10:35
 */
public class InputNeuron extends AbstractNeuron{

    //输入层数据
    private long value;

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public long getNextValue() {
        return getValue();
    }
}
