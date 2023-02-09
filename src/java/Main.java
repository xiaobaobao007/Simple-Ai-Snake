import nn.NetManager;
import snake.GameFrame;
import snake.GamePanel;

/**
 * @descript:
 * @author: baomengyang <baomengyang@sina.cn>
 * @create: 2023-02-08 10:18
 */
public class Main {

    public static void main(String[] args) {
        NetManager netManager = new NetManager(GamePanel.MAP_HEIGHT * GamePanel.MAP_WIDTH + 3, 40, 4);

        GamePanel gamePanel = new GamePanel();
        gamePanel.init(netManager);

        GameFrame gameFrame = new GameFrame();
        gameFrame.init(gamePanel);

        gameFrame.setVisible(true);
    }
}
