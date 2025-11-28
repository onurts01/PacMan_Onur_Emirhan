import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class LevelLoader {

    public static Grid<Field> load(String path, EntityManager em) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(path));

        int rows = lines.size();
        int cols = lines.stream().mapToInt(String::length).max().orElse(0);

        Grid<Field> grid = new Grid<>(rows, cols);

        for (int r = 0; r < rows; r++) {
            String line = lines.get(r);

            for (int c = 0; c < cols; c++) {
                char ch = (c < line.length()) ? line.charAt(c) : ' ';
                Field f = new Field(r, c);

                // WICHTIG: Bei GameObject ist x=Spalte (c) und y=Zeile (r)
                // Wir übergeben also (c, r) statt (r, c)!

                switch (ch) {
                    case '#':
                        Wall w = new Wall(c, r); // x=c, y=r
                        f.setPassable(false);
                        f.setContent(w);
                        em.add(w);
                        break;

                    case '.':
                        Dot d = new Dot(c, r);
                        f.setContent(d);
                        em.add(d);
                        break;

                    case 'o':
                        PowerUp p = new PowerUp(c, r);
                        f.setContent(p);
                        em.add(p);
                        break;

                    case 'P':
                        // Player braucht das Grid für Kollisionen!
                        Player pl = new Player(c, r, grid);
                        f.setContent(pl);
                        em.setPlayer(pl);
                        em.add(pl);
                        break;

                    case 'G':
                        Ghost g = new Ghost(c, r, grid);
                        f.setContent(g);
                        em.add(g);
                        em.addGhost(g);
                        break;

                    case 'X':
                        Teleporter tp = new Teleporter(c, r);
                        f.setPassable(true);
                        f.setContent(tp);
                        em.add(tp);
                        break;
                }
                grid.set(r, c, f);
            }
        }
        return grid;
    }
}