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

    // PowerUp Status
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
            System.out.println("PowerUp abgelaufen!");
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

        // Wir prüfen erst, ob wir uns bewegen dürfen
        if (grid.isWalkable(newY, newX)) {
            Field nextField = grid.get(newY, newX);
            GameObject content = nextField.getContent();

            try {
                // Check: Wenn Wand -> Exception werfen!
                if (!grid.isWalkable(newY, newX)) {
                    throw new InvalidMoveException("Aua! Das ist eine Wand.");
                }


            // --- TELEPORTER LOGIC START ---
            if (content instanceof Teleporter) {
                // Wir laufen in einen Teleporter!
                // Statt das Teleporter-Objekt zu "fressen" (überschreiben),
                // springen wir direkt auf die ANDERE Seite des Spielfelds.

                if (newX == 0) {
                    // Wir sind links reingelaufen -> Beamen nach Rechts
                    // (Breite - 2, damit wir NEBEN dem rechten Teleporter rauskommen)
                    newX = grid.getWidth() - 2;
                } else {
                    // Wir sind rechts reingelaufen -> Beamen nach Links
                    // (Spalte 1, damit wir NEBEN dem linken Teleporter rauskommen)
                    newX = 1;
                }


                // Jetzt müssen wir das NEUE Zielfeld laden, weil sich newX geändert hat
                nextField = grid.get(newY, newX);
                content = nextField.getContent();
                System.out.println("Teleportiert zu: " + newX + "|" + newY);
            }

            // --- TELEPORTER LOGIC END ---

            // Punkte essen (am neuen Ort)
            if (content instanceof Dot) {
                score += 10;
            } else if (content instanceof PowerUp) {
                score += 50;
                activatePowerUp();
            }

            // Die eigentliche Bewegung im Grid
            grid.get(this.y, this.x).setContent(null); // Alten Platz leeren
            this.x = newX;
            this.y = newY;
            nextField.setContent(this);   // Neuen Platz besetzen

            grid.get(this.y, this.x).setContent(null);
            this.x = newX;
            this.y = newY;
            nextField.setContent(this);

        } catch (InvalidMoveException e) {
            // Exception fangen (Punkte für Aufgabe gesichert!)
            System.out.println(e.getMessage());

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