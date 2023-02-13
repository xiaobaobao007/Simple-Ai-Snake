package constant;

/**
 * @descript:
 * @author: baomengyang <baomengyang@sina.cn>
 * @create: 2023-02-09 14:30
 */
public interface AllConstant {
    /*
     * 全局通用
     */
    int TRAIN_TIMES = 1000000;

    int INIT_MIN_WEIGHT = 0;
    int INIT_MAX_WEIGHT = 0;

    //隐藏层 层数
    int HIDE_LEVEL = 1;
    //隐藏层 神经元个数
    int HIDE_NUM = 4;

    enum FeedbackScore {
        GET_FOOD(3, 3, 8, 10),//吃到食物
        FAIL(2, 4, 8, -2),//失败
        LOOP(2, 4, 8, -2),//死循环
        MOVE(4, 4, 8),//移动

        GUESS_SUCCESS(2, 0, 2, 1),
        GUESS_FAIL(2, 0, 2, -100),

        ;

        //激励输出层相连的隐藏层单层的个数
        public final int num1;
        //激励隐藏层单层的个数
        public final int num2;
        //激励输入层的个数
        public final int num3;
        //获得的分数
        public final int addScore;

        FeedbackScore(int num1, int num2, int num3) {
            this(num1, num2, num3, 0);
        }

        FeedbackScore(int num1, int num2, int num3, int addScore) {
            this.num1 = num1;
            this.num2 = num2;
            this.num3 = num3;
            this.addScore = addScore;
        }
    }

    /*
     * 🐍 贪吃蛇
     */
   int MAP_WIDTH = 6;
   int MAP_HEIGHT = 6;
}
