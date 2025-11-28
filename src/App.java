import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {

        EntityManager em = new EntityManager();
        Grid<Field> grid = LevelLoader.load("src/level1.txt", em);

        GamePanel gamePanel = new GamePanel(grid, em);

        // Frame Setup
        JFrame frame = new JFrame("PacMan - simplified");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // HUD (Optional, hier vereinfacht weggelassen oder aus deinem Code kopieren)
        frame.add(gamePanel, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // KEY BINDINGS statt KeyListener (funktioniert auf macOS!)
        InputMap inputMap = gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = gamePanel.getActionMap();

        // Links
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
        actionMap.put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (em.getPlayer() != null) em.getPlayer().tryMove(-1, 0);
            }
        });

        // Rechts
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
        actionMap.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (em.getPlayer() != null) em.getPlayer().tryMove(1, 0);
            }
        });

        // Hoch
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
        actionMap.put("up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (em.getPlayer() != null) em.getPlayer().tryMove(0, -1);
            }
        });

        // Runter
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
        actionMap.put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (em.getPlayer() != null) em.getPlayer().tryMove(0, 1);
            }
        });

        // 2. GEISTER STARTEN
        for (Ghost g : em.getGhosts()) {
            g.start();
        }

        // 3. GAME LOOP (Zeichnen)
        Timer timer = new Timer(40, e -> gamePanel.repaint());
        timer.start();
    }
}