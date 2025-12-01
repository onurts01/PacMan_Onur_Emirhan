import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {

        EntityManager em = new EntityManager();
        Grid<Field> grid = LevelLoader.load("src/level1.txt", em);
        GamePanel gamePanel = new GamePanel(grid, em);

        // Vor dem Timer
        int currentHighscore = loadHighscore();
        JLabel highscoreLabel = new JLabel("Highscore: " + currentHighscore);
// ... Rest vom HUD ...

        if (p.getLives() <= 0) {
            ((Timer)e.getSource()).stop();

            // NEU: Highscore speichern, wenn er besser ist
            if (p.getScore() > loadHighscore()) {
                saveHighscore(p.getScore());
            }

            JOptionPane.showMessageDialog(frame, "GAME OVER! Score: " + p.getScore());
            System.exit(0);
        }

        // 1. HUD (Head-Up Display) erstellen
        JPanel hudPanel = new JPanel();
        hudPanel.setBackground(Color.BLACK);
        hudPanel.setLayout(new GridLayout(1, 3)); // 3 Bereiche nebeneinander

        JLabel scoreLabel = new JLabel("Score: 0");
        JLabel timeLabel = new JLabel("Time: 0s");
        JLabel livesLabel = new JLabel("Lives: 3");

        // Styling (Weißer Text, fett, zentriert)
        Font hudFont = new Font("Arial", Font.BOLD, 18);
        styleLabel(scoreLabel, hudFont);
        styleLabel(timeLabel, hudFont);
        styleLabel(livesLabel, hudFont);

        hudPanel.add(scoreLabel);
        hudPanel.add(timeLabel);
        hudPanel.add(livesLabel);

        // 2. Fenster zusammenbauen
        JFrame frame = new JFrame("PacMan - Student Edition");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.add(hudPanel, BorderLayout.NORTH); // HUD ganz oben
        frame.add(gamePanel, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gamePanel.requestFocusInWindow(); // Wichtig für Tastatur!

        // 3. Steuerung (Key Bindings)
        setupInput(gamePanel, em);

        // 4. Geister starten
        for (Ghost g : em.getGhosts()) {
            g.start();
        }

        // 5. Spielstart-Zeit merken
        long startTime = System.currentTimeMillis();


        // 6. GAME LOOP (Schiedsrichter & Zeichner)
        Timer timer = new Timer(40, e -> {
            Player p = em.getPlayer();

            if (p != null) {
                // A) Spieler bewegen & Logik updaten
                p.update();

                // B) Kollision mit Geistern prüfen
                for (Ghost g : em.getGhosts()) {
                    if (g.getX() == p.getX() && g.getY() == p.getY()) {

                        // --- NEUE LOGIK ---
                        if (p.isInvincible()) {
                            // Szenario: Pacman frisst Geist
                            p.addScore(100);
                            g.resetPosition(); // Geist zurück in den Käfig
                            System.out.println("Geist gegessen! +100 Punkte");
                        } else {
                            // Szenario: Geist frisst Pacman
                            p.loseLife();
                            p.resetPosition();
                            System.out.println("OUCH! Leben verloren.");
                        }
                        // ------------------
                    }
                }

                // C) HUD aktualisieren
                scoreLabel.setText("Score: " + p.getScore());
                livesLabel.setText("Lives: " + p.getLives());

                long playedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                timeLabel.setText("Time: " + playedSeconds + "s");

                // Game Over Check
                if (p.getLives() <= 0) {
                    ((Timer)e.getSource()).stop();
                    JOptionPane.showMessageDialog(frame, "GAME OVER! Score: " + p.getScore());
                    System.exit(0);
                }
            }
            gamePanel.repaint();
        });

        timer.start();
    }



    // Hilfsmethode für schönes Design
    private static void styleLabel(JLabel label, Font font) {
        label.setForeground(Color.WHITE);
        label.setFont(font);
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // Hilfsmethode für die Tasten (der Übersichtlichkeit halber ausgelagert)
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
    // Speichern
    private static void saveHighscore(int score) {
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream("highscore.dat"))) {
            out.writeInt(score); // Wir speichern einfach die Zahl
            System.out.println("Highscore gespeichert: " + score);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    // Laden
    private static int loadHighscore() {
        try (java.io.ObjectInputStream in = new java.io.ObjectInputStream(new java.io.FileInputStream("highscore.dat"))) {
            return in.readInt();
        } catch (java.io.IOException e) {
            return 0; // Kein Highscore vorhanden
        }
    }
}