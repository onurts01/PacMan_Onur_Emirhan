import java.awt.*;

public class Player extends GameObject {

    // Referenz auf das Grid speichern, damit wir Wände sehen und uns umsetzen können
    private GameGrid<Field> grid;

    public Player(int x, int y, GameGrid<Field> grid) {
        // WICHTIG: x = Spalte (column), y = Zeile (row)
        super(x, y);
        this.grid = grid;
    }

    // Die Methode zum Bewegen
    public void tryMove(int dx, int dy) {
        int newX = this.x + dx;
        int newY = this.y + dy;

        // 1. Prüfen: Ist der Weg frei? (grid.isWalkable erwartet Zeile, Spalte -> y, x)
        if (grid.isWalkable(newY, newX)) {

            // --- HIER WAR DER FEHLER ---

            // 2. Dem ALTEN Feld sagen: "Ich bin weg" (Löschen)
            // Wir greifen auf das Feld zu, wo wir GERADE NOCH stehen
            grid.get(this.y, this.x).setContent(null);

            // 3. Meine internen Koordinaten aktualisieren
            this.x = newX;
            this.y = newY;

            // 4. Dem NEUEN Feld sagen: "Hier bin ich jetzt" (Setzen)
            // Wir greifen auf das Feld zu, wo wir JETZT stehen
            grid.get(this.y, this.x).setContent(this);

            // ---------------------------

            System.out.println("Player moved to: " + x + "|" + y);
        }
    }

    @Override
    public void update() {
        // Leer, da wir uns per Tastendruck bewegen
    }

    @Override
    public void render(Graphics g) {
        // Wird nicht genutzt, da draw() aufgerufen wird
    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        // Wir ignorieren hier this.x/this.y und zeichnen dort,
        // wo das GamePanel uns sagt (x, y).
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, size, size);
    }
}