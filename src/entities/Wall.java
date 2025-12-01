package entities;

import java.awt.*;

public class Wall extends GameObject {
    public Wall(int row, int col) {
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
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, size, size);
    }
}