// <T extends GameObject> garantiert, dass wir nur Spielobjekte ins Gitter packen
// und keine Strings oder Integers. Das gibt Punkte für Generics!

public interface GameGrid<T extends GameObject> {

    // Gibt das Objekt an Position x,y zurück
    T get(int x, int y);

    // Setzt ein Objekt an eine Position
    void set(int x, int y, T obj);

    // Prüft, ob ein Feld betretbar ist (keine Wand)
    boolean isWalkable(int x, int y);

    // Breite und Höhe des Spielfelds (für Grenzen-Checks)
    int getWidth();
    int getHeight();
}