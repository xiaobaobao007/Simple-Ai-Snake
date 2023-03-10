package snake;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 键盘监听
 */
public class KeyListen implements KeyListener {

	private final GamePanel gamePanel;

	KeyListen(GamePanel gamePanel) {
		super();
		this.gamePanel = gamePanel;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_W:
				gamePanel.up();
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_A:
				gamePanel.left();
				break;
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_S:
				gamePanel.down();
				break;
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_D:
				gamePanel.right();
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
