import java.awt.Graphics;
import java.awt.Color;
import java.util.Random;

public class Ghost extends GameObject implements Runnable {

    private Color color = null;
    private Thread myThread;
    // WICHTIG: Der Geist läuft auf einem Grid aus "Field"
    private final GameGrid<Field> grid;
    private boolean running = false;
    private final Random random;

    // Konstruktor anpassen: Er nimmt jetzt GameGrid<Field> entgegen
    public Ghost(int x, int y, GameGrid<Field> grid) {
        super(x, y);
        this.grid = grid;
        this.random = new Random();
        start(); // Thread direkt starten, damit er losläuft
    }


    public void start() {
        if (myThread == null || !myThread.isAlive()) {
            running = true;
            myThread = new Thread(this);
            myThread.start();
        }
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(500); // Geschwindigkeit
                makeRandomMove();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void makeRandomMove() {
        int direction = random.nextInt(4);
        int newX = this.x;
        int newY = this.y;

        switch (direction) {
            case 0: newY--; break; // Hoch (y wird kleiner)
            case 1: newX++; break; // Rechts
            case 2: newY++; break; // Runter
            case 3: newX--; break; // Links
        }

        // Prüfen ob begehbar
        if (grid.isWalkable(newY, newX)) { // Achtung: Grid ist oft grid[row][col] -> grid[y][x]
            // Bewegen
            this.setPosition(newX, newY);
            System.out.println("Geist auf: " + newX + "|" + newY);
        }
    }

    @Override
    public void update() { }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.CYAN); // Geist Farbe
        g.fillRect(x * 20, y * 20, 20, 20);
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        render(g); // Weiterleitung, damit LevelLoader/GamePanel es zeichnen kann
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}