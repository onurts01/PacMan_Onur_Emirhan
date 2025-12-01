package entities;
import java.awt.*;

/**
 * Repräsentiert ein Hindernis.
 * Wird vom LevelLoader erstellt, wenn ein '#' im Textfile steht.
 */
public class Wall extends GameObject {
    public Wall(int x, int y) {
        super(x, y);
    }
    // ... Konstruktor ...

    @Override
    public void update() {
        // Wände bewegen sich nicht
    }

    @Override
    public void render(Graphics g) {}

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, size, size);
    }
}