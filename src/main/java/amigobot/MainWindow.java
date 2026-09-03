package amigobot;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI window.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private AmigoBot amigoBot;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image botImage = new Image(this.getClass().getResourceAsStream("/images/DaBot.png"));

    /** Binds the scroll pane to auto-scroll to the bottom. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.getChildren().add(
                DialogBox.getBotDialog("Hola amigo! I'm AmigoBot.\nWhat can I do for you, compadre?", botImage));
    }

    /** Injects the AmigoBot instance. */
    public void setAmigoBot(AmigoBot bot) {
        amigoBot = bot;
    }

    /**
     * Creates two dialog boxes — one for the user input and one for AmigoBot's reply —
     * then appends them to the dialog container and clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = amigoBot.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBotDialog(response, botImage)
        );
        userInput.clear();

        if (input.equalsIgnoreCase("bye")) {
            sendButton.setDisable(true);
            userInput.setDisable(true);
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    // ignored
                }
                Platform.exit();
            }).start();
        }
    }
}
