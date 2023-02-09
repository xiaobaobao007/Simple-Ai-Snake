package snake;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JPanel;
import nn.NetManager;
import nn.neuron.OutputNeuron;

/**
 * @descript:
 * @author: baomengyang <baomengyang@sina.cn>
 * @create: 2023-02-08 13:04
 */
public class GamePanel extends JPanel {

    private NetManager netManager;

    private final ScheduledExecutorService poll = Executors.newScheduledThreadPool(1);

    public static final int MAP_WIDTH = 10;
    public static final int MAP_HEIGHT = 10;

    private final int Panel_WIDTH = 500;
    private final int Panel_HEIGHT = 500;

    private final int BLOCK_WIDTH = Panel_WIDTH / MAP_WIDTH;
    private final int BLOCK_HEIGHT = Panel_HEIGHT / MAP_HEIGHT;

    private int[] trainInputData = new int[MAP_WIDTH * MAP_HEIGHT + 3];

    private Point food;
    private final LinkedList<Point> snake = new LinkedList<>();
    private final LinkedList<Point> empty = new LinkedList<>();

    private int nowDirection;
    private int switchDirection;
    private final int[][] directionArray = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    private final int[][] initSnake = {{2, 5}, {3, 5}};//从尾开始

    public static final Random random = new Random();

    public void init(NetManager netManager) {
        this.netManager = netManager;
        super.setBounds(45, 40, Panel_WIDTH, Panel_HEIGHT);

        resetAll(null);

        for (int i = 0; i < 100; i++) {
            train(netManager);
            move();
        }

        poll.scheduleWithFixedDelay(() -> {
            try {
                trick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 50, 50, TimeUnit.MILLISECONDS);
    }


    public void train(NetManager netManager) {
        Arrays.fill(trainInputData, 0);

        for (Point point : snake) {
            trainInputData[point.getY() * MAP_HEIGHT + point.getX()] = 1;
        }

        trainInputData[trainInputData.length - 3] = snake.getFirst().getX();
        trainInputData[trainInputData.length - 2] = snake.getFirst().getY();
        trainInputData[trainInputData.length - 1] = food.getY() * MAP_HEIGHT + food.getX();

        netManager.inputData(trainInputData);
        OutputNeuron[] outputData = netManager.calculate();

        if (nowDirection - 2 >= 0) {
            outputData[nowDirection - 2].setCacheResult(Long.MIN_VALUE);
        } else {
            outputData[nowDirection + 2].setCacheResult(Long.MIN_VALUE);
        }

        int i;
        for (i = 1; i < outputData.length; i++) {
            if (outputData[i - 1].getResult() != outputData[i].getResult()) {
                break;
            }
        }

        int maxIndex = 0;
        long maxResult = Long.MIN_VALUE;

        for (i = 0; i < outputData.length; i++) {
            if (outputData[i].getResult() > maxResult) {
                maxIndex = i;
            } else if (outputData[i].getResult() == maxResult && random.nextBoolean()) {
                maxIndex = i;
            } else {
                continue;
            }

            maxResult = outputData[i].getResult();
        }

        switchDirection = maxIndex;
    }

    public void initSnake() {
        nowDirection = switchDirection = 0;

        snake.clear();
        for (int[] data : initSnake) {
            snake.addFirst(new Point().init(data[0], data[1]));
        }

        empty.clear();
        for (int h = 0; h < MAP_HEIGHT; h++) {
            SEARCH:
            for (int w = 0; w < MAP_WIDTH; w++) {
                for (int[] data : initSnake) {
                    if (w == data[0] && h == data[1]) {
                        continue SEARCH;
                    }
                }
                empty.add(new Point().init(w, h));
            }
        }
    }

    private void resetAll(NetManager netManager) {
        initSnake();
        randomFood();

        if (netManager != null) {
            netManager.fail(switchDirection);
        }
    }

    private void trick() {
        train(netManager);
        move();
        repaint();
    }

    private void move() {
        if (switchDirection - 2 != nowDirection && switchDirection + 2 != nowDirection) {
            nowDirection = switchDirection;
        }

        Point first = snake.getFirst();
        int newX = first.getX() + directionArray[nowDirection][0];
        int newY = first.getY() + directionArray[nowDirection][1];

        if (newX < 0 || newY < 0 || newX >= MAP_WIDTH || newY >= MAP_HEIGHT) {
            resetAll(netManager);
            return;
        }

        for (Point point : snake) {
            if (point.getX() == newX && point.getY() == newY) {
                resetAll(netManager);
                return;
            }
        }

        if (newX == food.getX() && newY == food.getY()) {
            snake.addFirst(food);
            empty.remove(food);

            randomFood();

            if (netManager != null) {
                netManager.success(switchDirection);
            }
        } else {
            Point newPoint = snake.removeLast();
            empty.add(newPoint.cloneNew());

            newPoint.init(newX, newY);
            snake.addFirst(newPoint);

            empty.remove(newPoint);
        }
    }

    public void right() {
        switchDirection = 0;
    }

    public void down() {
        switchDirection = 1;
    }

    public void left() {
        switchDirection = 2;
    }

    public void up() {
        switchDirection = 3;
    }

    public void randomFood() {
        int index = random.nextInt(empty.size());
        food = empty.remove(index);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Color.GRAY);
        g.fillRect(0, 0, Panel_WIDTH, Panel_HEIGHT);

        boolean head = true;
        g.setColor(new Color(16, 92, 7));
        for (Point point : snake) {
            g.fillRect(BLOCK_WIDTH * point.getX(), BLOCK_HEIGHT * point.getY(), BLOCK_WIDTH, BLOCK_HEIGHT);
            if (head) {
                head = false;
                g.setColor(Color.GREEN);
            }
        }

        g.setColor(Color.YELLOW);
        g.fillRect(BLOCK_WIDTH * food.getX(), BLOCK_HEIGHT * food.getY(), BLOCK_WIDTH, BLOCK_HEIGHT);
    }

    public static int getRandomInt(int bounds) {
        return random.nextInt(bounds);
    }
}
