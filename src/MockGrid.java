// Eine "dumme" Simulation des Spielfelds, damit du Logik testen kannst.
public class MockGrid implements GameGrid<GameObject> {

    @Override
    public GameObject get(int x, int y) {
        return null; // Einfach nichts zurückgeben
    }

    @Override
    public void set(int x, int y, GameObject obj) {
        System.out.println("DEBUG: Setze Objekt auf " + x + "|" + y);
    }

    @Override
    public boolean isWalkable(int x, int y) {
        // Wir tun so, als wäre das Spielfeld unendlich groß und leer
        // Nur zum Testen: Alles größer als 20 ist eine "Wand"
        if (x > 20 || x < 0 || y > 20 || y < 0) return false;
        return true;
    }

    @Override
    public int getWidth() { return 20; }

    @Override
    public int getHeight() { return 20; }
}
