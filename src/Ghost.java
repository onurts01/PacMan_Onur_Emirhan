import java.awt.Graphics;
import java.awt.Color;
import java.util.Random;

public class Ghost extends GameObject implements Runnable {

    private Thread myThread;
    private GameGrid<Field> grid;
    private boolean running = false;
    private Random random = new Random();

    public Ghost(int x, int y, GameGrid<Field> grid) {
        super(x, y);
        this.grid = grid;
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
                // 1. AMEISEN LOSSCHICKEN
                makeSmartMove();

                // 2. Warten
                Thread.sleep(600); // Etwas langsamer, damit man es sieht
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void makeSmartMove() {
        // 1. Ziel finden (Den Spieler)
        // Wir suchen einfach im Grid nach dem Player objekt.
        // (Effizienter wäre, wenn EntityManager uns das sagt, aber so geht's auch)
        int targetX = -1, targetY = -1;

        // Simpel: Wir suchen den Spieler im Grid
        // Achtung: Teure Operation, aber bei 20x20 ok.
        for(int r=0; r<grid.getHeight(); r++) {
            for(int c=0; c<grid.getWidth(); c++) {
                if (grid.get(r,c).getContent() instanceof Player) {
                    targetY = r;
                    targetX = c;
                }
            }
        }

        if (targetX == -1) {
            makeRandomMove(); // Kein Spieler gefunden -> Zufall
            return;
        }

        // 2. Ameisen Gedächtnis bauen
        int[][] memory = new int[grid.getHeight()][grid.getWidth()];

        // Player Position als Ziel markieren (oder Start? Aufgabe sagt:
        // "kürzesten Weg zum Spieler... Startfeld des Geistes erzeugt Ameise")
        // Wir starten beim GEIST und fluten das Grid.
        // Das Feld mit dem Spieler hat am Ende eine Zahl (z.B. 15).
        // Um den Player zu fangen, müssen wir aber wissen, welcher NACHBAR die kleinste Distanz ZUM PLAYER hat.
        // TRICK: Wir starten die Ameisen BEIM SPIELER!
        // Dann steht beim Geist z.B. eine 10. Sein Nachbar hat eine 9. Er geht zur 9.

        Ant queen = new Ant(targetX, targetY, 1, grid, memory);
        Thread t = new Thread(queen);
        t.start();
        try {
            t.join(); // Warten bis fertig geflutet
        } catch(InterruptedException e) {}

        // 3. Den besten Nachbarn suchen (Der mit der kleinsten Zahl > 0)
        int bestX = -1, bestY = -1;
        int minSteps = Integer.MAX_VALUE;

        int[][] dirs = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        for (int[] dir : dirs) {
            int nx = this.x + dir[0];
            int ny = this.y + dir[1];

            if (grid.isWalkable(ny, nx)) {
                int val = memory[ny][nx]; // Wie weit ist es von hier zum Spieler?
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
            makeRandomMove(); // Sackgasse oder Fehler
        }
    }

    private void moveTo(int newX, int newY) {
        grid.get(this.y, this.x).setContent(null);
        this.setPosition(newX, newY);
        grid.get(this.y, this.x).setContent(this);
    }

    private void makeRandomMove() {
        // ... (Deine alte Zufallslogik als Fallback) ...
        // Kopiere hier einfach die Logik von vorhin rein oder lass ihn stehenbleiben
        // Fallback: Zufall
        int dir = random.nextInt(4);
        int dx=0, dy=0;
        if(dir==0) dy=-1; else if(dir==1) dx=1; else if(dir==2) dy=1; else dx=-1;
        if (grid.isWalkable(this.y+dy, this.x+dx)) moveTo(this.x+dx, this.y+dy);
    }

    @Override
    public void update() {}

    @Override
    public void render(Graphics g) {} // Wird nicht genutzt, draw wird genutzt

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        g.setColor(Color.CYAN);
        g.fillRect(x, y, size, size);
    }
}