package world;

import entities.GameObject;

/**
 * Repräsentiert ein einzelnes Feld auf dem Schachbrett (Grid).
 * Speichert, was sich aktuell auf diesem Feld befindet (Content).
 */
public class Field {
    public final int row, col;
    private boolean passable = true; // Ist das Feld generell betretbar?

    // Speichert den Abstand zum Ziel (für Ameisen-Algorithmus)
    private int steps = 0;

    // Das Objekt, das gerade hier steht (z.B. Player oder Null)
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