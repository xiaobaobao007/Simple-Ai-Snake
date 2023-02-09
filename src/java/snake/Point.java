package snake;

/**
 * @descript:
 * @author: baomengyang <baomengyang@sina.cn>
 * @create: 2023-02-08 13:38
 */
public class Point {
    private int x;
    private int y;

    public Point init(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            Point p = (Point) obj;
            return p.x == x && p.y == y;
        }
        return super.equals(obj);
    }

    public Point cloneNew() {
        return new Point().init(x, y);
    }
}
