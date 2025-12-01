package logic;

import entities.GameObject;
import entities.Ghost;
import entities.Player;

import java.util.*;

public class EntityManager {
    private final List<GameObject> entities = new ArrayList<>();
    private Player player;
    private final List<Ghost> ghosts = new ArrayList<>();

    public void add(GameObject obj) { entities.add(obj); }
    public List<GameObject> getAll() { return entities; }

    public void setPlayer(Player p) { player = p; }
    public Player getPlayer() { return player; }

    public void addGhost(Ghost g) { ghosts.add(g); }
    public List<Ghost> getGhosts() { return ghosts; }
}
