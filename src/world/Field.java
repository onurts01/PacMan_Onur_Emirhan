package world;

import entities.GameObject;

public class Field {
    public final int row, col;
    private boolean passable = true;
    private int steps = 0; // für Debug/Ameisen später
    private GameObject content;

    public Field(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean isPassable() { return passable; }
    public void setPassable(boolean passable) { this.passable = passable; }

    public GameObject getContent() { return content; }
    public void setContent(GameObject content) { this.content = content; }

    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }
}