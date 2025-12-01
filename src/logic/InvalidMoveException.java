package logic;
/**
 * Benutzerdefinierte Exception (Anforderung 2).
 * Wird geworfen, wenn eine Figur versucht, in eine Wand zu laufen.
 */
public class InvalidMoveException extends Exception {
    public InvalidMoveException(String message) {
        super(message);
    }
}
