import java.awt.*;

public class PowerUp extends GameObject {
    public PowerUp(int row, int col) {
        super(row, col);
    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {

    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        g.setColor(Color.CYAN);
        g.fillOval(x + size/4, y + size/4, size/2, size/2);
    }
}
