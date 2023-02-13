import example.Choose;
import nn.NetManager;

/**
 * @descript:
 * @author: baomengyang <baomengyang@sina.cn>
 * @create: 2023-02-09 16:57
 */
public class GuessMain {
    public static void main(String[] args) {
        int chooseNum = 4;

        NetManager netManager = new NetManager(chooseNum, 1, chooseNum, chooseNum);

        Choose choose = new Choose();
        choose.init(netManager, chooseNum);
    }
}
