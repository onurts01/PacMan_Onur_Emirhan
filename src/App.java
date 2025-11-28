import javax.swing.*;
import java.awt.*;      // <-- für BorderLayout, Color, etc.
import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {

        EntityManager em = new EntityManager();
        Grid<Field> grid = LevelLoader.load("src/level1.txt", em);

        GamePanel gamePanel = new GamePanel(grid, em);

        // ===== HUD erstellen (Score / Highscore / Lives + Checkboxes) =====
        JPanel hud = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hud.setBackground(Color.BLACK);

        JLabel scoreLabel = new JLabel("Score: 0");
        JLabel highscoreLabel = new JLabel("Highscore: 0");
        JLabel livesLabel = new JLabel("Lives: 3");

        // Schriftfarbe weiß, damit man es auf Schwarz sieht
        scoreLabel.setForeground(Color.WHITE);
        highscoreLabel.setForeground(Color.WHITE);
        livesLabel.setForeground(Color.WHITE);

        JCheckBox invincibleBox = new JCheckBox("Invincible");
        JCheckBox debugBox = new JCheckBox("Debug");

        invincibleBox.setForeground(Color.WHITE);
        debugBox.setForeground(Color.WHITE);
        invincibleBox.setBackground(Color.BLACK);
        debugBox.setBackground(Color.BLACK);

        hud.add(scoreLabel);
        hud.add(Box.createHorizontalStrut(20));
        hud.add(highscoreLabel);
        hud.add(Box.createHorizontalStrut(20));
        hud.add(livesLabel);
        hud.add(Box.createHorizontalStrut(40));
        hud.add(invincibleBox);
        hud.add(debugBox);

        // ===== Frame aufbauen =====
        JFrame frame = new JFrame("PacMan - simplified");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());      // wichtig fürs HUD!

        frame.add(hud, BorderLayout.NORTH);       // HUD oben
        frame.add(gamePanel, BorderLayout.CENTER); // Spielfeld darunter

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // später GameLoop starten

        Thread meinGeist = new Thread();
        meinGeist.start();
    }
}