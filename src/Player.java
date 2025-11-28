import java.awt.*;

public class Player extends GameObject {
    public Player(int row, int col) {
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
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, size, size);
    }
}
