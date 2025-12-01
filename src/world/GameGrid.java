package world;

/**
 * Generisches Interface für das Spielfeld (Anforderung 3: Generics).
 * Definiert die grundlegenden Operationen, unabhängig vom gespeicherten Datentyp T.
 * T ist in unserer Implementierung meistens 'Field'.
 */
public interface GameGrid<T> {

    T get(int x, int y);
    void set(int x, int y, T obj);

    boolean isWalkable(int x, int y);

    int getWidth();
    int getHeight();
}