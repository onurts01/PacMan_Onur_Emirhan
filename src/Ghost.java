import java.awt.Graphics;
import java.awt.Color;
import java.util.Random;

public class Ghost extends GameObject implements Runnable {

    private Thread myThread;
    private GameGrid<Field> grid;
    private boolean running = false;
    private Random random = new Random();

    // Startposition merken für Reset
    private int startX, startY;

    public Ghost(int x, int y, GameGrid<Field> grid) {
        super(x, y);
        this.grid = grid;
        this.startX = x;
        this.startY = y;
    }

    // --- NEU: Geist nach Hause schicken (wenn gefressen) ---
    public void resetPosition() {
        synchronized(grid) {
            // Alten Platz leeren
            grid.get(this.y, this.x).setContent(null);

            // Koordinaten zurücksetzen
            this.x = startX;
            this.y = startY;

            // Auf Startplatz setzen
            grid.get(this.y, this.x).setContent(this);
        }
    }

    public void start() {
        if (myThread == null || !myThread.isAlive()) {
            running = true;
            myThread = new Thread(this);
            myThread.start();
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Hier rufen wir die SCHLAUE Bewegung auf
                makeSmartMove();

                // Geschwindigkeit
                Thread.sleep(600);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    // --- Die wiederhergestellte KI-Logik (Ameisen) ---
    private void makeSmartMove() {
        // 1. Ziel finden (Den Spieler)
        int targetX = -1, targetY = -1;

        // Wir suchen den Spieler im Grid
        for(int r=0; r<grid.getHeight(); r++) {
            for(int c=0; c<grid.getWidth(); c++) {
                if (grid.get(r,c).getContent() instanceof Player) {
                    targetY = r;
                    targetX = c;
                }
            }
        }

        // Falls kein Spieler da ist (z.B. gerade tot), zufällig bewegen
        if (targetX == -1) {
            makeRandomMove();
            return;
        }

        // 2. Ameisen-Gedächtnis bauen
        int[][] memory = new int[grid.getHeight()][grid.getWidth()];

        // Wir starten die "Flutung" beim SPIELER.
        // So wissen wir bei jedem Feld, wie weit es zum Spieler ist.
        Ant queen = new Ant(targetX, targetY, 1, grid, memory);
        Thread t = new Thread(queen);
        t.start();
        try {
            t.join(); // Warten bis die Ameisen fertig sind
        } catch(InterruptedException e) {}

        // 3. Den besten Nachbarn suchen (Der mit der kleinsten Zahl > 0)
        int bestX = -1, bestY = -1;
        int minSteps = Integer.MAX_VALUE;

        int[][] dirs = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

        // Wir prüfen alle 4 Nachbarn vom GEIST
        for (int[] dir : dirs) {
            int nx = this.x + dir[0];
            int ny = this.y + dir[1];

            if (grid.isWalkable(ny, nx)) {
                int val = memory[ny][nx]; // Wie weit ist es von hier zum Spieler?

                // Wir suchen die KLEINSTE Distanz (val > 0)
                if (val > 0 && val < minSteps) {
                    minSteps = val;
                    bestX = nx;
                    bestY = ny;
                }
            }
        }

        // 4. Bewegen
        if (bestX != -1) {
            moveTo(bestX, bestY);
        } else {
            makeRandomMove(); // Fallback
        }
    }

    // Fallback, falls der Weg blockiert ist
    private void makeRandomMove() {
        int dir = random.nextInt(4);
        int dx=0, dy=0;
        if(dir==0) dy=-1; else if(dir==1) dx=1; else if(dir==2) dy=1; else dx=-1;
        moveTo(this.x+dx, this.y+dy);
    }

    // Sicheres Bewegen mit Kollisionsschutz
    private void moveTo(int targetX, int targetY) {
        synchronized (grid) {
            // Check 1: Ist das Ziel begehbar?
            if (!grid.isWalkable(targetY, targetX)) return;

            // Check 2: Steht da schon ein anderer Geist?
            Field targetField = grid.get(targetY, targetX);
            if (targetField.getContent() instanceof Ghost) return;

            // Bewegen
            grid.get(this.y, this.x).setContent(null); // Alten Platz leeren
            this.setPosition(targetX, targetY);        // Koordinaten ändern
            targetField.setContent(this);              // Neuen Platz füllen
        }
    }

    @Override
    public void update() {}

    @Override
    public void render(Graphics g) {}

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        g.setColor(Color.CYAN);
        g.fillRect(x, y, size, size);
    }
}