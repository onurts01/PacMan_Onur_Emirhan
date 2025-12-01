package entities;

import java.awt.*;

public class Dot extends GameObject {
    public Dot(int row, int col) {
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
        g.setColor(Color.RED);
        int d = size / 3;
        g.fillOval(x + d, y + d, d, d);
    }
}
