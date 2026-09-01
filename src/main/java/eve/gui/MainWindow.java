package eve.gui;

import eve.Eve;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Controller for the main GUI window: shows the conversation and forwards user input to {@link Eve}. */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Eve eve;

    /** Keeps the conversation scrolled to the newest message. */
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the Eve instance this window sends user input to.
     *
     * @param eve the chatbot instance to use.
     */
    public void setEve(Eve eve) {
        this.eve = eve;
    }

    /**
     * Sends the text field's content to Eve, shows both it and Eve's
     * response as new dialog boxes, and closes the window shortly after a
     * "bye" response. Called when the user presses Enter in the text field
     * or clicks "Send".
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = eve.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getEveDialog(response));
        userInput.clear();

        if (eve.isExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
