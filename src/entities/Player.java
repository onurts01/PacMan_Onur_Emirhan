package entities;

import logic.InvalidMoveException;
import world.Field;
import world.GameGrid;

import java.awt.*;

public class Player extends GameObject {

    private GameGrid<Field> grid;

    private int currentDx = 0;
    private int currentDy = 0;
    private long lastMoveTime = 0;
    private int moveDelay = 200;

    private int score = 0;
    private int lives = 3;
    private int startX, startY;

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
        if (isPoweredUp && System.currentTimeMillis() > powerUpEndTime) {
            isPoweredUp = false;
            System.out.println("entities.PowerUp abgelaufen!");
        }

        if (System.currentTimeMillis() - lastMoveTime > moveDelay) {
            if (currentDx != 0 || currentDy != 0) {
                tryMove(currentDx, currentDy);
                lastMoveTime = System.currentTimeMillis();
            }
        }
    }

    private void tryMove(int dx, int dy) {
        int newX = this.x + dx;
        int newY = this.y + dy;

        try {
            // 1. Exception-Check: Ist das eine Wand?
            if (!grid.isWalkable(newY, newX)) {
                // Wenn wir hier werfen, springt er sofort zum catch-Block unten
                // und führt KEINE Bewegung aus.
                throw new InvalidMoveException("Wand!");
            }

            Field nextField = grid.get(newY, newX);
            GameObject content = nextField.getContent();

            // 2. entities.Teleporter Logik
            if (content instanceof Teleporter) {
                if (newX == 0) {
                    newX = grid.getWidth() - 2;
                } else {
                    newX = 1;
                }
                // Nach Teleport müssen wir das neue Zielfeld holen
                nextField = grid.get(newY, newX);
                content = nextField.getContent();
            }

            // 3. Punkte essen
            if (content instanceof Dot) {
                score += 10;
            } else if (content instanceof PowerUp) {
                score += 50;
                activatePowerUp();
            }

            // 4. Bewegung ausführen
            grid.get(this.y, this.x).setContent(null); // Alten Platz leeren
            this.x = newX;
            this.y = newY;
            nextField.setContent(this);   // Neuen Platz besetzen

        } catch (InvalidMoveException e) {
            // Wir fangen den Fehler ab und tun nichts (Pacman bleibt einfach stehen)
            // System.out.println(e.getMessage()); // Optional: Konsolenausgabe
        }
    }

    private void activatePowerUp() {
        isPoweredUp = true;
        powerUpEndTime = System.currentTimeMillis() + 10000;
        System.out.println("POWERUP! Geister essbar!");
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
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.YELLOW);
        }
        g.fillOval(x, y, size, size);
    }
}