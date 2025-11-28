import java.awt.*;

public class Player extends GameObject {

    // Referenz auf das Grid speichern, damit wir Wände sehen
    private GameGrid<Field> grid;

    public Player(int row, int col, GameGrid<Field> grid) {
        super(row, col);
        this.grid = grid;
    }

    // Neue Methode zum Bewegen
    public void tryMove(int dx, int dy) {
        int newX = this.x + dx; // dx ist Änderung in Spalte (x)
        int newY = this.y + dy; // dy ist Änderung in Zeile (y)

        // Achtung: In deinem Grid ist grid.get(row, col), also get(y, x)!
        // Prüfen, ob der Weg frei ist
        if (grid.isWalkable(newY, newX)) {
            this.x = newX;
            this.y = newY;
            System.out.println("Player moved to: " + x + "|" + y);
        }

        // ... Rest bleibt gleich (render, draw etc.)

        }
    @Override
    public void update () {

    }

    @Override
    public void render (Graphics g){

    }

    @Override
    public void draw(Graphics g, int x, int y, int size) {
        // Wir nutzen NICHT this.x, sondern die x/y, die uns das GamePanel gibt.
        // Das garantiert, dass Pacman genau in seinem Kästchen landet.
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, size, size);
    }
}
