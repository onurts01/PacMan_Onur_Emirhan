import java.awt.Graphics;

public abstract class GameObject {
    protected int x, y;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void update();
    public abstract void render(Graphics g);

    // Synchronized ist gut für Threads!
    public synchronized int getX() { return x; }
    public synchronized int getY() { return y; }
    public synchronized void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}