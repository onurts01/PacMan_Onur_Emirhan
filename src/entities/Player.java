package entities;

import logic.InvalidMoveException;
import world.Field;
import world.GameGrid;

import java.awt.*;

public class Player extends GameObject {

    private GameGrid<Field> grid;

    // Movement Timing
    private int currentDx = 0;
    private int currentDy = 0;
    private long lastMoveTime = 0;
    private int moveDelay = 200; // 5 Schritte pro Sekunde

    // Stats
    private int score = 0;
    private int lives = 3;
    private int startX, startY;

    // State
    private boolean isPoweredUp = false;
    private long powerUpEndTime = 0;

    public Player(int x, int y, GameGrid<Field> grid) {
        super(x, y);
        this.grid = grid;
        this.startX = x;
        this.startY = y;
    }

    public void setDirection(int dx, int dy) {
        this.currentDx = dx;
        this.currentDy = dy;
    }

    @Override
    public void update() {
        // Status-Check: PowerUp abgelaufen?
        if (isPoweredUp && System.currentTimeMillis() > powerUpEndTime) {
            isPoweredUp = false;
            System.out.println("PowerUp Mode ended.");
        }

        // Bewegung basierend auf Zeit (nicht Frame-Rate)
        if (System.currentTimeMillis() - lastMoveTime > moveDelay) {
            if (currentDx != 0 || currentDy != 0) {
                tryMove(currentDx, currentDy);
                lastMoveTime = System.currentTimeMillis();
            }
        }
    }

    /**
     * Führt die Bewegung aus und behandelt Interaktionen (Items, Wände).
     * Wirft intern InvalidMoveException bei Wand-Kollision.
     */
    private void tryMove(int dx, int dy) {
        int newX = this.x + dx;
        int newY = this.y + dy;

        try {
            // Prüfung auf Wand -> Exception werfen (Anforderung)
            if (!grid.isWalkable(newY, newX)) {
                throw new InvalidMoveException("Blocked path at " + newX + "|" + newY);
            }

            Field nextField = grid.get(newY, newX);
            GameObject content = nextField.getContent();

            // Teleporter-Logik: Wrap-around movement
            if (content instanceof Teleporter) {
                if (newX == 0) {
                    newX = grid.getWidth() - 2;
                } else {
                    newX = 1;
                }
                // Zielfeld nach Teleport aktualisieren
                nextField = grid.get(newY, newX);
                content = nextField.getContent();
            }

            // Item-Interaktionen
            if (content instanceof Dot) {
                score += 10;
            } else if (content instanceof PowerUp) {
                score += 50;
                activatePowerUp();
            }

            // Grid-Update
            grid.get(this.y, this.x).setContent(null); // Alten Platz leeren
            this.x = newX;
            this.y = newY;
            nextField.setContent(this); // Neuen Platz belegen

        } catch (InvalidMoveException e) {
            // Exception wird gefangen und ignoriert -> Spieler bleibt einfach stehen
            // System.out.println(e.getMessage());
        }
    }

    private void activatePowerUp() {
        isPoweredUp = true;
        powerUpEndTime = System.currentTimeMillis() + 10000; // 10 Sek. Unverwundbarkeit
        System.out.println("POWERUP ACTIVE!");
    }

    public boolean isInvincible() { return isPoweredUp; }
    public void addScore(int points) { this.score += points; }

    public void resetPosition() {
        grid.get(this.y, this.x).setContent(null);
        this.x = startX;
        this.y = startY;
        grid.get(this.y, this.x).setContent(this);
        this.currentDx = 0;
        this.currentDy = 0;
    }

    public int getScore() { return score; }
    public int getLives() { return lives; }
    public void loseLife() { lives--; }

    @Override
    public void render(Graphics g) {}

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        if (isPoweredUp) {
            g.setColor(Color.RED); // Visuelles Feedback für PowerUp
        } else {
            g.setColor(Color.YELLOW);
        }
        g.fillOval(x, y, size, size);
    }
}