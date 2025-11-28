import java.util.ArrayList;
import java.util.List;

public class Ant implements Runnable {
    private int x, y, steps;
    private GameGrid<Field> grid;
    private int[][] memory; // Hier merken wir uns die Schritte

    public Ant(int x, int y, int steps, GameGrid<Field> grid, int[][] memory) {
        this.x = x;
        this.y = y;
        this.steps = steps;
        this.grid = grid;
        this.memory = memory;
    }

    @Override
    public void run() {
        // 1. Nachbarn prüfen (Oben, Rechts, Unten, Links)
        // int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}}; // dx, dy
        // ACHTUNG: In deinem Grid ist y=Zeile(r), x=Spalte(c).
        // Wir probieren alle 4 Richtungen.
        int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};

        List<Thread> children = new ArrayList<>();
        boolean firstWayTaken = false;

        for (int[] dir : directions) {
            int nextX = x + dir[0];
            int nextY = y + dir[1];

            // Ist der Nachbar begehbar?
            if (grid.isWalkable(nextY, nextX)) { // Zeile (y), Spalte (x)

                // Synchronisierter Zugriff auf das Gedächtnis
                synchronized (memory) {
                    // Ist das Feld noch leer (0) oder haben wir einen kürzeren Weg gefunden?
                    int currentVal = memory[nextY][nextX]; // row, col
                    if (currentVal == 0 || currentVal > steps + 1) {

                        memory[nextY][nextX] = steps + 1; // Markieren!

                        // Wenn es der erste Weg ist, gehen wir ihn selbst (Rekursion optimiert)
                        if (!firstWayTaken) {
                            // Wir bewegen "uns selbst" auf das neue Feld
                            // Trick: Wir starten KEINEN Thread, sondern machen direkt weiter
                            // Aber um den Code simpel zu halten und die Aufgabe "neue Threads" zu erfüllen:
                            // Wir starten hier der Einfachheit halber immer Threads für Verzweigungen.

                            Thread t = new Thread(new Ant(nextX, nextY, steps + 1, grid, memory));
                            t.start();
                            children.add(t);
                            // firstWayTaken = true; // Falls man Optimierung will
                        }
                    }
                }
            }
        }

        // Auf alle Kinder warten ("join"), damit wir wissen, wann ALLES fertig ist
        for (Thread t : children) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
