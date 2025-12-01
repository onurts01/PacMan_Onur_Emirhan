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
 * Die Hauptklasse der Anwendung (Controller).
 * Hier laufen alle Fäden zusammen: GUI-Setup, Thread-Start und der Game-Loop.
 */
public class App {
    public static void main(String[] args) throws IOException {

        // --- 1. INITIALISIERUNG (Datenmodell & View) ---
        EntityManager em = new EntityManager();
        // LevelLoader nutzt Generics: Grid<Field> (Anforderung 3: Generics)
        Grid<Field> grid = LevelLoader.load("src/level1.txt", em);
        GamePanel gamePanel = new GamePanel(grid, em);

        // --- 2. SERIALISIERUNG (Anforderung 4) ---
        // Lädt den gespeicherten Highscore beim Start von der Festplatte.
        int currentHighscore = loadHighscore();

        // --- 3. GUI SETUP (Anforderung 7: Swing) ---
        JPanel hudPanel = new JPanel();
        hudPanel.setBackground(Color.BLACK);
        hudPanel.setLayout(new GridLayout(1, 4)); // Grid-Layout für saubere Anordnung

        JLabel scoreLabel = new JLabel("Score: 0");
        JLabel timeLabel = new JLabel("Time: 0s");
        JLabel livesLabel = new JLabel("Lives: 3");
        JLabel highscoreLabel = new JLabel("Highscore: " + currentHighscore);

        // Styling der Labels
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

        frame.pack(); // Passt Fenstergröße an Inhalt an
        frame.setLocationRelativeTo(null); // Zentriert das Fenster
        frame.setVisible(true);

        // Fokus auf das Panel setzen, damit KeyBindings funktionieren
        gamePanel.requestFocusInWindow();
        setupInput(gamePanel, em);

        // --- 4. MULTITHREADING START (Anforderung 5.2) ---
        // Startet für jeden Geist einen eigenen, parallelen Thread.
        for (Ghost g : em.getGhosts()) {
            g.start();
        }

        long startTime = System.currentTimeMillis();

        // --- 5. GAME LOOP (Anforderung 5.1) ---
        // Der Timer sorgt für periodische Updates (alle 40ms = 25 FPS).
        // Dies trennt die Spielgeschwindigkeit von der Rechenleistung.
        Timer timer = new Timer(40, e -> {
            Player p = em.getPlayer();

            if (p != null) {
                // A) Update: Bewegungslogik des Spielers
                p.update();

                // B) Kollisionsabfrage: Spieler vs. Geister
                for (Ghost g : em.getGhosts()) {
                    // Einfache AABB-Kollision (Axis-Aligned Bounding Box) auf Rasterebene
                    if (g.getX() == p.getX() && g.getY() == p.getY()) {
                        if (p.isInvincible()) {
                            // Szenario: PowerUp aktiv -> Geist wird gefressen
                            p.addScore(100);
                            g.resetPosition();
                        } else {
                            // Szenario: Normal -> Spieler verliert Leben
                            p.loseLife();
                            p.resetPosition();
                        }
                    }
                }

                // C) UI-Updates
                scoreLabel.setText("Score: " + p.getScore());
                livesLabel.setText("Lives: " + p.getLives());
                long playedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                timeLabel.setText("Time: " + playedSeconds + "s");

                // D) Game Over Logik
                if (p.getLives() <= 0) {
                    ((Timer)e.getSource()).stop(); // Loop stoppen

                    // Highscore speichern, falls neuer Rekord (Serialisierung)
                    if (p.getScore() > loadHighscore()) {
                        saveHighscore(p.getScore());
                    }

                    JOptionPane.showMessageDialog(frame, "GAME OVER! Score: " + p.getScore());
                    System.exit(0);
                }
            }
            // Zeichnet das Spielfeld neu (ruft paintComponent auf)
            gamePanel.repaint();
        });

        timer.start();
    }

    // --- HILFSMETHODEN ---

    private static void styleLabel(JLabel label, Font font) {
        label.setForeground(Color.WHITE);
        label.setFont(font);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Richtet die Tastatursteuerung mittels KeyBindings ein.
     * KeyBindings sind robuster als KeyListener (Swing Standard).
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

    /**
     * Speichert den Highscore mittels ObjectOutputStream (Serialisierung).
     */
    private static void saveHighscore(int score) {
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream("highscore.dat"))) {
            out.writeInt(score);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lädt den Highscore mittels ObjectInputStream (Deserialisierung).
     */
    private static int loadHighscore() {
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream("highscore.dat"))) {
            return in.readInt();
        } catch (java.io.IOException e) {
            return 0; // Fallback, falls noch kein Savegame existiert
        }
    }
}