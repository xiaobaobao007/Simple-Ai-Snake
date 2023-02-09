package snake;

import javax.swing.JFrame;

/**
 * @descript:
 * @author: baomengyang <baomengyang@sina.cn>
 * @create: 2023-02-08 13:04
 */
public class GameFrame extends JFrame {
    public void init(GamePanel gamePanel) {
        setLayout(null);
        setBounds(-800, -100, 600, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(new KeyListen(gamePanel));
        setFocusable(true);
        add(gamePanel);
    }
}
