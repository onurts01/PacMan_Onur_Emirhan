import java.awt.Graphics;

public abstract class GameObject {

    // direkter Zugriff durch protected
    protected int x;
    protected int y;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Logik
    public abstract void update();

    // Grafik
    public abstract void render(Graphics g);

    // Positionsabfragen
    public synchronized int getX() {
        return x;
    }

    public synchronized int getY() {
        return y;
    }

    public synchronized void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}