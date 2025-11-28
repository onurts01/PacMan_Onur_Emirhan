import javax.swing.*;
import java.awt.*;
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

        // 1. INPUT (Steuerung)
        frame.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                Player p = em.getPlayer();
                if (p != null) {
                    int key = e.getKeyCode();
                    if (key == 37) p.tryMove(-1, 0); // Links
                    if (key == 38) p.tryMove(0, -1); // Hoch
                    if (key == 39) p.tryMove(1, 0);  // Rechts
                    if (key == 40) p.tryMove(0, 1);  // Runter
                }
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