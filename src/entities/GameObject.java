package entities;

import java.awt.Graphics;

/**
 * Abstrakte Basisklasse für alle Spielobjekte (Anforderung 1: Vererbung).
 * Definiert gemeinsame Eigenschaften (Position) und erzwingt die Implementierung
 * von update() und draw() in allen Unterklassen (Polymorphie).
 */
public abstract class GameObject {

    // Protected erlaubt den direkten Zugriff für erbende Klassen (Player, Ghost)
    protected int x;
    protected int y;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Jedes Objekt muss seine eigene Logik definieren (z.B. Bewegung).
     */
    public abstract void update();

    /**
     * Veraltete Methode, wird durch draw() ersetzt, bleibt aber für Kompatibilität.
     */
    public abstract void render(Graphics g);

    /**
     * Thread-sichere Getter für die Position.
     * Wichtig, da der GUI-Thread (Zeichnen) und Logik-Threads (Geister)
     * gleichzeitig auf diese Werte zugreifen könnten.
     */
    public synchronized int getX() {
        return x;
    }

    public synchronized int getY() {
        return y;
    }

    /**
     * Setzt die Position atomar (synchronized), um inkonsistente Zustände zu vermeiden.
     */
    public synchronized void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Zeichnet das Objekt an einer spezifischen Bildschirmposition.
     * Wird vom GamePanel aufgerufen (Polymorphie).
     */
    public abstract void draw(Graphics g, int x, int y, int size);
}