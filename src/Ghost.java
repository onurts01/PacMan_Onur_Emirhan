import java.awt.Graphics;
import java.awt.Color;
import java.util.Random;

// Erbt von GameObject (Basis) und implementiert Runnable (für den Thread)
public class Ghost extends GameObject implements Runnable {

    private Thread myThread;
    private GameGrid<GameObject> grid; // Referenz auf das Spielfeld
    private boolean running = false;
    private int speed = 500; // Wartezeit in Millisekunden (je kleiner, desto schneller)
    private Random random;

    // Konstruktor
    public Ghost(int x, int y, GameGrid<GameObject> grid) {
        super(x, y);
        this.grid = grid;
        this.random = new Random();
    }

    // Startet den Thread sicher
    public void start() {
        if (myThread == null || !myThread.isAlive()) {
            running = true;
            myThread = new Thread(this); // "this" ist dieser Geist (Runnable)
            myThread.start(); // Ruft automatisch die run()-Methode auf
        }
    }

    // Stoppt den Thread sauber (wichtig beim Beenden des Spiels)
    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        System.out.println("Geist-Thread gestartet!");

        while (running) {
            try {
                // 1. Bewegung berechnen
                makeRandomMove();

                // 2. Warten (Geschwindigkeit simulieren)
                Thread.sleep(speed);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Geist-Thread beendet.");
    }

    // Eine einfache KI: Zufällige Bewegung
    private void makeRandomMove() {
        int direction = random.nextInt(4); // 0=Hoch, 1=Rechts, 2=Runter, 3=Links
        int newX = this.x;
        int newY = this.y;

        switch (direction) {
            case 0:
                newY--;
                break; // Hoch
            case 1:
                newX++;
                break; // Rechts
            case 2:
                newY++;
                break; // Runter
            case 3:
                newX--;
                break; // Links
        }

        // --- WICHTIG: Kollisionsprüfung ---
        // Wir fragen das Grid: "Ist das Feld (newX, newY) begehbar?"
        if (grid.isWalkable(newX, newY)) {
            // Wenn ja: Position aktualisieren (Thread-safe durch synchronized in GameObject)
            this.setPosition(newX, newY);

            // Nur für dich zum Testen in der Konsole:
            System.out.println("Geist bewegt sich nach: [" + x + "|" + y + "]");
        } else {
            System.out.println("Geist ist gegen eine Wand gelaufen bei: [" + newX + "|" + newY + "]");
        }
    }

    // Logik update (wird hier eigentlich nicht gebraucht, da der Thread alles macht,
    // aber muss wegen GameObject implementiert werden)
    @Override
    public void update() {
        // Leer lassen oder für Animationen nutzen
    }

    @Override
    public void render(Graphics g) {
        // Einfacher roter Kasten als Platzhalter für die GUI später
        g.setColor(Color.RED);
        g.fillRect(x * 20, y * 20, 20, 20);
    }

    // --- MAIN METHODE ZUM TESTEN (NUR FÜR DICH) ---
    public static void main(String[] args) {
        // 1. Dummy Grid erstellen (Das hast du vorhin gespeichert)
        GameGrid<GameObject> testGrid = new MockGrid();

        // 2. Geist erstellen auf Position 5,5
        Ghost g = new Ghost(5, 5, testGrid);

        // 3. Thread starten
    }
}