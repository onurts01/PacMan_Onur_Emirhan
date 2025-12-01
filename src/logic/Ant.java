package logic;

import world.Field;
import world.GameGrid;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementierung des "Ameisen"-Algorithmus.
 * Jede Ameise ist ein eigener Thread, der rekursiv neue Ameisen für neue Pfade startet.
 * Dient der Berechnung der Distanzkarte (Flood Fill).
 */
public class Ant implements Runnable {
    private int x, y, steps;
    private GameGrid<Field> grid;
    // Geteilter Speicher für das Schritt-Gedächtnis (Shared Memory)
    private int[][] memory;

    public Ant(int x, int y, int steps, GameGrid<Field> grid, int[][] memory) {
        this.x = x;
        this.y = y;
        this.steps = steps;
        this.grid = grid;
        this.memory = memory;
    }

    @Override
    public void run() {
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        List<Thread> children = new ArrayList<>();
        boolean firstWayTaken = false;

        for (int[] dir : directions) {
            int nextX = x + dir[0];
            int nextY = y + dir[1];

            if (grid.isWalkable(nextY, nextX)) {
                // Zugriff auf shared memory synchronisieren
                synchronized (memory) {
                    int currentVal = memory[nextY][nextX];
                    // Prüfen: War hier noch niemand oder haben wir einen kürzeren Weg gefunden?
                    if (currentVal == 0 || currentVal > steps + 1) {

                        memory[nextY][nextX] = steps + 1;

                        // Optimierung: Ersten Weg im aktuellen Thread gehen,
                        // für weitere Abzweigungen neue Threads starten.
                        if (!firstWayTaken) {
                            Thread t = new Thread(new Ant(nextX, nextY, steps + 1, grid, memory));
                            t.start();
                            children.add(t);
                        }
                    }
                }
            }
        }

        // Warten auf Beendigung aller Kind-Threads (Join),
        // damit der Algorithmus erst endet, wenn alles erforscht ist.
        for (Thread t : children) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
