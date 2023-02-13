package snake;

import constant.AllConstant;
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
public class GamePanel extends JPanel implements AllConstant {

    private NetManager netManager;

    private final ScheduledExecutorService poll = Executors.newScheduledThreadPool(1);

    private final int Panel_WIDTH = 500;
    private final int Panel_HEIGHT = 500;

    private final int[] trainInputData = new int[MAP_WIDTH * MAP_HEIGHT + 3];

    private Point food;
    private final LinkedList<Point> snake = new LinkedList<>();
    private final LinkedList<Point> empty = new LinkedList<>();

    private int nowDirection;
    private int switchDirection;
    private final int[][] directionArray = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

    public static final Random random = new Random();

    private boolean doAi = true;
    private int moveTimes;

    public void init(NetManager netManager) {
        this.netManager = netManager;
        super.setBounds(45, 40, Panel_WIDTH, Panel_HEIGHT);

        resetAll(null);

        int last = 30;
        int max = TRAIN_TIMES;
        int out = max / last;

        for (int i = 0; i < max; i++) {
            calculate();
            move();
            if (i % out == 0) {
                System.out.printf("%d ", (last--));
            }
        }
        System.out.println();

        netManager.sout();
        doAi = false;

        poll.scheduleWithFixedDelay(() -> {
            try {
                trick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 300, 300, TimeUnit.MILLISECONDS);
    }

    public void calculate() {
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

        boolean allSame = true;
        for (int i = 1; i < outputData.length; i++) {
            if (outputData[i].getResult() != outputData[i - 1].getResult()) {
                allSame = false;
                break;
            }
        }

        if (allSame) {
            while (outputData[nowDirection = random.nextInt(outputData.length)].getResult() == Long.MIN_VALUE) {
            }
        } else {
            switchDirection = 0;
            long maxResult = Long.MIN_VALUE;

            for (int i = 0; i < outputData.length; i++) {
                if (outputData[i].getResult() > maxResult) {
                    switchDirection = i;
                    maxResult = outputData[i].getResult();
                }
            }
        }
    }

    public void initSnake() {
        moveTimes = 0;
        nowDirection = switchDirection = random.nextInt(3);

        snake.clear();
        int startX = random.nextInt(MAP_WIDTH - 2) + 1;
        int startY = random.nextInt(MAP_HEIGHT - 2) + 1;
        snake.addFirst(new Point().init(startX, startY));

        empty.clear();
        for (int h = 0; h < MAP_HEIGHT; h++) {
            for (int w = 0; w < MAP_WIDTH; w++) {
                if (w == startX && h == startY) {
                    continue;
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
        calculate();
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

        if (moveTimes > 50) {
            netManager.encourage(nowDirection, FeedbackScore.LOOP);
        }

        if (newX == food.getX() && newY == food.getY()) {
            moveTimes = 0;
            snake.addFirst(food);
            empty.remove(food);

            randomFood();

            if (doAi && netManager != null) {
                netManager.success(switchDirection);
            }
        } else {
            if (doAi && netManager != null) {
                int oldDistance = Math.abs(food.getX() - first.getX()) + Math.abs(food.getY() - first.getY());
                int newDistance = Math.abs(food.getX() - newX) + Math.abs(food.getY() - newY);
                if (newDistance < oldDistance) {
                    netManager.encourage(nowDirection, FeedbackScore.MOVE, newDistance << 1);
                } else if (newDistance > oldDistance) {
                    netManager.encourage(nowDirection, FeedbackScore.MOVE, -newDistance >> 1);
                }
            }

            Point newPoint = snake.removeLast();
            empty.add(newPoint.cloneNew());

            newPoint.init(newX, newY);
            snake.addFirst(newPoint);

            empty.remove(newPoint);
        }

        moveTimes++;
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
        int BLOCK_WIDTH = Panel_WIDTH / MAP_WIDTH;
        int BLOCK_HEIGHT = Panel_HEIGHT / MAP_HEIGHT;
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
