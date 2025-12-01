package world;

import entities.Wall;

/**
 * Konkrete Implementierung des generischen Grids.
 * Speichert Objekte vom Typ T in einem 2D-Array.
 */
public class Grid<T> implements GameGrid<T> {
    private final int rows, cols;
    private final Object[][] cells; // Interne Speicherung als Object-Array (Java Generics Limitation)

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Object[rows][cols];
    }

    @Override
    public void set(int row, int col, T value) {
        if (inBounds(row, col)) {
            cells[row][col] = value;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int row, int col) {
        if (!inBounds(row, col)) return null;
        // Sicherer Cast, da wir über set() nur T zulassen
        return (T) cells[row][col];
    }

    @Override
    public int getWidth() { return cols; }

    @Override
    public int getHeight() { return rows; }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    /**
     * Prüft, ob ein Feld begehbar ist.
     * Nutzt 'instanceof', um Wände zu identifizieren.
     */
    @Override
    public boolean isWalkable(int row, int col) {
        if (!inBounds(row, col)) return false;

        Object obj = cells[row][col];

        // Spezialbehandlung für Field-Objekte
        if (obj instanceof Field) {
            Field f = (Field) obj;
            // Ein Feld ist begehbar, wenn es 'passable' ist UND keine Wand enthält
            boolean isWall = (f.getContent() instanceof Wall);
            return f.isPassable() && !isWall;
        }

        // Fallback für direkte Objekt-Speicherung
        if (obj instanceof Wall) return false;

        return true;
    }

    private boolean inBounds(int row, int col) {
        return row >= 0 && col >= 0 && row < rows && col < cols;
    }
}
