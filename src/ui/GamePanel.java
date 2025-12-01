package ui;

import logic.EntityManager;
import world.Field;
import world.Grid;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final Grid<Field> grid;
    private final int tileSize = 20;
    private boolean debug = false;

    public GamePanel(Grid<Field> grid, EntityManager em) {
        this.grid = grid;
        setPreferredSize(new Dimension(grid.getCols() * tileSize, grid.getRows() * tileSize));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                Field f = grid.get(r, c);

                int x = c * tileSize;
                int y = r * tileSize;


                // Boden
                g.setColor(Color.BLACK);
                g.fillRect(x, y, tileSize, tileSize);

                // Inhalt
                if (f.getContent() != null) {
                    f.getContent().draw(g, x, y, tileSize);
                }

                // Rasterlinie
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, tileSize, tileSize);

                // Debug-Schrittzahlen
                if (debug && f.getSteps() > 0) {
                    g.setColor(Color.CYAN);
                    g.drawString("" + f.getSteps(), x + 5, y + 14);
                }
            }
        }
    }
}