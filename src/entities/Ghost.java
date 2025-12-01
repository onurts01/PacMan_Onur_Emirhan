package entities;

import logic.Ant;
import world.Field;
import world.GameGrid;
import world.Grid;

import java.awt.Graphics;
import java.awt.Color;
import java.util.Random;

/**
 * Repräsentiert einen Gegner.
 * Implementiert Runnable, um als eigenständiger Thread neben dem Main-Loop zu laufen.
 */
public class Ghost extends GameObject implements Runnable {

    private Thread myThread;
    private GameGrid<Field> grid;
    private boolean running = false;
    private Random random = new Random();

    private int startX, startY;

    public Ghost(int x, int y, Grid<Field> grid) {
        super(x, y);
        this.grid = grid;
        this.startX = x;
        this.startY = y;
    }

    /**
     * Setzt den Geist thread-sicher auf Start zurück.
     * Nutzt synchronized, um Race Conditions mit dem Movement-Thread zu verhindern.
     */
    public void resetPosition() {
        synchronized(grid) {
            grid.get(this.y, this.x).setContent(null);
            this.x = startX;
            this.y = startY;
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

    /**
     * Der Lebenszyklus des Geistes.
     * Hier wird die KI ausgeführt und der Thread schlafen gelegt, um Geschwindigkeit zu simulieren.
     */
    @Override
    public void run() {
        while (running) {
            try {
                makeSmartMove();
                // Simuliert "Nachdenkzeit" und reguliert die Geschwindigkeit
                Thread.sleep(600);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    /**
     * KI-Logik basierend auf dem "Ameisen-Algorithmus" (Breitensuche).
     * Startet temporäre Threads (Ameisen) zur Wegfindung.
     */
    private void makeSmartMove() {
        int targetX = -1, targetY = -1;

        // 1. Spieler suchen (ineffizient, aber sicher, da Grid synchronisiert ist)
        for(int r=0; r<grid.getHeight(); r++) {
            for(int c=0; c<grid.getWidth(); c++) {
                if (grid.get(r,c).getContent() instanceof Player) {
                    targetY = r;
                    targetX = c;
                }
            }
        }

        if (targetX == -1) {
            makeRandomMove(); // Fallback, falls Spieler tot
            return;
        }

        // 2. Ameisen zur Pfadsuche aussenden
        int[][] memory = new int[grid.getHeight()][grid.getWidth()];

        // Wir starten beim Spieler und fluten das Grid, um Distanzen zu ermitteln
        Ant queen = new Ant(targetX, targetY, 1, grid, memory);
        Thread t = new Thread(queen);
        t.start();
        try {
            t.join(); // Warten auf Abschluss der Pfadberechnung (Thread-Koordination)
        } catch(InterruptedException e) {}

        // 3. Besten Nachbarn (kürzeste Distanz) wählen
        int bestX = -1, bestY = -1;
        int minSteps = Integer.MAX_VALUE;
        int[][] dirs = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

        for (int[] dir : dirs) {
            int nx = this.x + dir[0];
            int ny = this.y + dir[1];

            if (grid.isWalkable(ny, nx)) {
                int val = memory[ny][nx];
                if (val > 0 && val < minSteps) {
                    minSteps = val;
                    bestX = nx;
                    bestY = ny;
                }
            }
        }

        if (bestX != -1) {
            moveTo(bestX, bestY);
        } else {
            makeRandomMove();
        }
    }

    private void makeRandomMove() {
        int dir = random.nextInt(4);
        int dx=0, dy=0;
        if(dir==0) dy=-1; else if(dir==1) dx=1; else if(dir==2) dy=1; else dx=-1;
        moveTo(this.x+dx, this.y+dy);
    }

    /**
     * Bewegt den Geist physisch im Grid.
     * Kritischer Abschnitt -> Muss synchronisiert sein!
     */
    private void moveTo(int targetX, int targetY) {
        synchronized (grid) {
            // Doppelte Prüfung, da sich der Zustand seit der KI-Entscheidung geändert haben könnte
            if (!grid.isWalkable(targetY, targetX)) return;

            Field targetField = grid.get(targetY, targetX);
            // Verhindert "Verschmelzen" von Geistern
            if (targetField.getContent() instanceof Ghost) return;

            // Atomares Update der Position
            grid.get(this.y, this.x).setContent(null);
            this.setPosition(targetX, targetY);
            targetField.setContent(this);
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