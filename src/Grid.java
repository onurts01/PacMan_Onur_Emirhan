// Auch hier: "extends GameObject" wegnehmen!
public class Grid<T> implements GameGrid<T> {
    private final int rows, cols;
    private final Object[][] cells; // Hier speichern wir die Objekte

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Object[rows][cols];
    }

    @Override
    public void set(int x, int y, T value) {
        if (inBounds(x, y)) {
            cells[x][y] = value;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int x, int y) {
        if (!inBounds(x, y)) return null;
        return (T) cells[x][y];
    }

    @Override
    public int getWidth() { return cols; }

    @Override
    public int getHeight() { return rows; }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    @Override
    public boolean isWalkable(int x, int y) {
        if (!inBounds(x, y)) return false;

        Object obj = cells[x][y];

        // Wenn das Grid "Fields" enthält (was dein LevelLoader macht):
        if (obj instanceof Field) {
            Field f = (Field) obj;
            // Ein Feld ist begehbar, wenn es 'passable' ist UND keine Wand enthält
            boolean isWall = (f.getContent() instanceof Wall);
            return f.isPassable() && !isWall;
        }

        // Fallback, falls wir doch mal direkt GameObjects speichern
        if (obj instanceof Wall) return false;

        return true;
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < rows && y < cols;
    }
}
