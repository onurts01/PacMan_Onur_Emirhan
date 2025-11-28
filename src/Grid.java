
public class Grid<T extends GameObject> implements GameGrid<T> {
    private final int rows, cols;
    private final Object[][] cells;

    public Grid(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Object[rows][cols];
    }

    @Override
    public void set(int x, int y, T value) {
        cells[x][y] = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int x, int y) {
        return (T) cells[x][y];
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    @Override
    public int getWidth() { return cols; }

    @Override
    public int getHeight() { return rows; }

    @Override
    public boolean isWalkable(int x, int y) {
        if (!inBounds(x, y)) {
            return false;
        }
        T obj = get(x, y);
        if (obj == null) {
            return true;
        }
        // If the object is a Field, check if it's passable
        if (obj instanceof Field) {
            return ((Field) obj).isPassable();
        }
        // If it's a Wall, it's not walkable
        if (obj instanceof Wall) {
            return false;
        }
        return true;
    }

    public boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < rows && y < cols;
    }
}
