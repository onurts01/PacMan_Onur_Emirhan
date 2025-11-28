import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class LevelLoader {

    public static Grid<Field> load(String path, EntityManager em) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(path));

        int rows = lines.size();

        // Nimm die MAXIMALE Zeilenlänge, nicht nur die erste
        int cols = lines.stream().mapToInt(String::length).max().orElse(0);

        Grid<Field> grid = new Grid<>(rows, cols);

        for (int r = 0; r < rows; r++) {
            String line = lines.get(r);

            for (int c = 0; c < cols; c++) {

                // wenn Zeile zu kurz -> so behandeln als 'leer'
                char ch = (c < line.length()) ? line.charAt(c) : ' ';

                Field f = new Field(r, c);

                switch (ch) {
                    case '#':
                        Wall w = new Wall(r, c);
                        f.setPassable(false);
                        f.setContent(w);
                        em.add(w);
                        break;

                    case '.':
                        Dot d = new Dot(r, c);
                        f.setContent(d);
                        em.add(d);
                        break;

                    case 'o':
                        PowerUp p = new PowerUp(r, c);
                        f.setContent(p);
                        em.add(p);
                        break;

                    case 'P':
                        Player pl = new Player(r, c);
                        f.setContent(pl);
                        em.setPlayer(pl);
                        em.add(pl);
                        break;

                    case 'G':
                        // Das Grid ist ja schon ein Grid<Field>, genau das braucht der Geist jetzt!
                        // Kein (GameGrid<GameObject>) Cast nötiger.
                        Ghost g = new Ghost(r, c, grid);

                        f.setContent(g);
                        em.add(g);
                        em.addGhost(g);
                        break;


                    case 'X':
                        Teleporter tp = new Teleporter(r, c);
                        f.setPassable(true);      // Teleporter sind begehbar
                        f.setContent(tp);
                        em.add(tp);
                        break;

                    default:
                        // leer
                        break;
                }

                grid.set(r, c, f);
            }
        }

        return grid;
    }
}