public interface GameGrid<T extends GameObject> {
    T get(int x, int y);
    void set(int x, int y, T obj);
    boolean isWalkable(int x, int y);
    int getCols();
    int getRows();
}
//interface