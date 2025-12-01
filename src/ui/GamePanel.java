package ui;

import logic.EntityManager;
import world.Field;
import world.Grid;

import javax.swing.*;
import java.awt.*;

/**
 * Die View-Komponente des Spiels (Anforderung 7: GUI mit Swing).
 * Erbt von JPanel und überschreibt paintComponent für benutzerdefiniertes Zeichnen.
 */
public class GamePanel extends JPanel {
    private final Grid<Field> grid;
    private final int tileSize = 20; // Pixelgröße eines Quadrats
    private boolean debug = false;

    public GamePanel(Grid<Field> grid, EntityManager em) {
        this.grid = grid;
        // Größe des Panels basierend auf Grid-Größe berechnen
        setPreferredSize(new Dimension(grid.getCols() * tileSize, grid.getRows() * tileSize));
        setBackground(Color.DARK_GRAY);
        setFocusable(true); // Wichtig für KeyListener/Bindings
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Zeichnet den aktuellen Spielzustand.
     * Wird durch repaint() im Game-Loop ausgelöst.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Iteration über das gesamte Grid zum Zeichnen aller Felder
        for (int r = 0; r < grid.getRows(); r++) {
            for (int c = 0; c < grid.getCols(); c++) {
                Field f = grid.get(r, c);

                int x = c * tileSize;
                int y = r * tileSize;

                // 1. Hintergrund (Boden)
                g.setColor(Color.BLACK);
                g.fillRect(x, y, tileSize, tileSize);

                // 2. Inhalt (Polymorpher Aufruf von draw() für Wall, Player, Ghost etc.)
                if (f.getContent() != null) {
                    f.getContent().draw(g, x, y, tileSize);
                }

                // 3. Grid-Linien (für bessere Übersicht)
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, tileSize, tileSize);

                // 4. Optional: Debug-Info (für Ameisen-Algorithmus)
                if (debug && f.getSteps() > 0) {
                    g.setColor(Color.CYAN);
                    g.drawString("" + f.getSteps(), x + 5, y + 14);
                }
            }
        }
    }
}