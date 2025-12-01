package entities;
import java.awt.*;

/**
 * Ein normaler Punkt, der gesammelt werden kann (+10 Score).
 */
public class Dot extends GameObject {
    public Dot(int x, int y) {
        super(x, y);
    }

    @Override
    public void update() {

    }

    @Override
    public void render(Graphics g) {

    }

    // ... (wie Wall, nur Farbe RED und fillOval)
    @Override
    public void draw(Graphics g, int x, int y, int size) {
        g.setColor(Color.RED);
        int d = size / 3;
        g.fillOval(x + d, y + d, d, d);
    }
    // ...
}
