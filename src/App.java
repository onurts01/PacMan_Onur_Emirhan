import entities.Ghost;
import entities.Player;
import logic.EntityManager;
import ui.GamePanel;
import world.Field;
import world.Grid;
import world.LevelLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Haupteinstiegspunkt der Anwendung.
 * Initialisiert das Model (Entities, Grid), die View (GamePanel) und startet den Game-Controller (Timer).
 */
public class App {
    public static void main(String[] args) throws IOException {

        // --- Setup & Initialisierung ---
        EntityManager em = new EntityManager();
        // Laden des Levels aus der Textdatei. Wir nutzen hier ein generisches Grid für Flexibilität.
        Grid<Field> grid = LevelLoader.load("src/level1.txt", em);
        GamePanel gamePanel = new GamePanel(grid, em);

        // Highscore laden (Serialisierungstest)
        int currentHighscore = loadHighscore();

        // --- GUI Aufbau (Swing) ---
        JPanel hudPanel = new JPanel();
        hudPanel.setBackground(Color.BLACK);
        hudPanel.setLayout(new GridLayout(1, 4));

        JLabel scoreLabel = new JLabel("Score: 0");
        JLabel timeLabel = new JLabel("Time: 0s");
        JLabel livesLabel = new JLabel("Lives: 3");
        JLabel highscoreLabel = new JLabel("Highscore: " + currentHighscore);

        // Einheitliches Styling für das HUD
        Font hudFont = new Font("Arial", Font.BOLD, 18);
        styleLabel(scoreLabel, hudFont);
        styleLabel(timeLabel, hudFont);
        styleLabel(livesLabel, hudFont);
        styleLabel(highscoreLabel, hudFont);

        hudPanel.add(scoreLabel);
        hudPanel.add(timeLabel);
        hudPanel.add(livesLabel);
        hudPanel.add(highscoreLabel);

        JFrame frame = new JFrame("PacMan - Student Edition");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(hudPanel, BorderLayout.NORTH);
        frame.add(gamePanel, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null); // Fenster zentrieren
        frame.setVisible(true);

        // Fokus auf das Panel zwingen, damit KeyBindings sofort greifen
        gamePanel.requestFocusInWindow();
        setupInput(gamePanel, em);

        // --- Threading Start ---
        // Geister laufen autonom in eigenen Threads
        for (Ghost g : em.getGhosts()) {
            g.start();
        }

        long startTime = System.currentTimeMillis();

        // --- Main Game Loop ---
        // Wir nutzen einen Swing-Timer statt einer while-Schleife im Main-Thread,
        // damit die GUI nicht einfriert und wir eine konstante Tick-Rate (ca. 25 FPS) haben.
        Timer timer = new Timer(40, e -> {
            Player p = em.getPlayer();

            if (p != null) {
                // 1. Logik-Update (Bewegung, PowerUps prüfen)
                p.update();

                // 2. Kollisionserkennung
                for (Ghost g : em.getGhosts()) {
                    // Simple Raster-Kollision
                    if (g.getX() == p.getX() && g.getY() == p.getY()) {
                        if (p.isInvincible()) {
                            // Spieler frisst Geist
                            p.addScore(100);
                            g.resetPosition();
                        } else {
                            // Geist frisst Spieler
                            p.loseLife();
                            p.resetPosition();
                        }
                    }
                }

                // 3. UI Sync
                scoreLabel.setText("Score: " + p.getScore());
                livesLabel.setText("Lives: " + p.getLives());
                long playedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                timeLabel.setText("Time: " + playedSeconds + "s");

                // 4. Game Over Condition
                if (p.getLives() <= 0) {
                    ((Timer)e.getSource()).stop(); // Loop anhalten

                    // Highscore persistieren, falls neuer Rekord
                    if (p.getScore() > loadHighscore()) {
                        saveHighscore(p.getScore());
                    }

                    JOptionPane.showMessageDialog(frame, "GAME OVER! Score: " + p.getScore());
                    System.exit(0);
                }
            }
            // Repaint stößt paintComponent im GamePanel an
            gamePanel.repaint();
        });

        timer.start();
    }

    private static void styleLabel(JLabel label, Font font) {
        label.setForeground(Color.WHITE);
        label.setFont(font);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Setzt die Steuerung mittels KeyBindings auf.
     * KeyBindings sind robuster als KeyListener, da sie keine Fokus-Probleme haben.
     */
    private static void setupInput(JComponent comp, EntityManager em) {
        InputMap im = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = comp.getActionMap();

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
        am.put("left", new AbstractAction() { public void actionPerformed(ActionEvent e) { if(em.getPlayer()!=null) em.getPlayer().setDirection(-1, 0); }});

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
        am.put("right", new AbstractAction() { public void actionPerformed(ActionEvent e) { if(em.getPlayer()!=null) em.getPlayer().setDirection(1, 0); }});

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
        am.put("up", new AbstractAction() { public void actionPerformed(ActionEvent e) { if(em.getPlayer()!=null) em.getPlayer().setDirection(0, -1); }});

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
        am.put("down", new AbstractAction() { public void actionPerformed(ActionEvent e) { if(em.getPlayer()!=null) em.getPlayer().setDirection(0, 1); }});
    }

    // Speichert einfachen Integer-Wert via Serialisierung
    private static void saveHighscore(int score) {
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream("highscore.dat"))) {
            out.writeInt(score);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private static int loadHighscore() {
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream("highscore.dat"))) {
            return in.readInt();
        } catch (java.io.IOException e) {
            return 0; // Standardwert, falls keine Datei existiert
        }
    }
}