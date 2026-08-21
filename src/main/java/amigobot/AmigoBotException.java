package amigobot;

/**
 * Represents an exception specific to AmigoBot.
 * Thrown when the user provides invalid input or an unrecognized command.
 */
public class AmigoBotException extends Exception {
    public AmigoBotException(String message) {
        super(message);
    }
}
