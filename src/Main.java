public class Main {
    public static void main(String[] args) {
        // In deinem Main oder im Test-Code:
        GameGrid<GameObject> testGrid = new MockGrid(); // Das Dummy-Grid nutzen
        Ghost meinGeist = new Ghost(1, 1, testGrid);    // Dem Geist das Dummy-Grid geben
        meinGeist.start();                              // Thread starten!
    }
}

