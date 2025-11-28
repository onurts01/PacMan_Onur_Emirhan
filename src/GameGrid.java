// <T extends GameObject> garantiert, dass wir nur Spielobjekte ins Gitter packen
// und keine Strings oder Integers. Das gibt Punkte für Generics!

// Wir entfernen "extends GameObject", damit auch "Field" erlaubt ist.
public interface GameGrid<T> {

    T get(int x, int y);
    void set(int x, int y, T obj);

    boolean isWalkable(int x, int y);

    int getWidth();
    int getHeight();
}