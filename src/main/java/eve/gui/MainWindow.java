package eve.gui;

import java.util.Random;

import eve.Eve;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/** Controller for the main GUI window: shows the conversation and forwards user input to {@link Eve}. */
public class MainWindow {
    private static final Color[] AVATAR_COLORS = {
        Color.web("#e57373"),
        Color.web("#64b5f6"),
        Color.web("#81c784"),
        Color.web("#ffd54f"),
        Color.web("#ba68c8"),
        Color.web("#4db6ac"),
        Color.web("#ff8a65"),
        Color.web("#a1887f"),
    };

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Eve eve;
    private Color userAvatarColor;
    private Color eveAvatarColor;

    /**
     * Keeps the conversation scrolled to the newest message, and picks a
     * random, distinct avatar color for the user and for Eve, kept for the
     * rest of this session.
     */
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        Random random = new Random();
        userAvatarColor = AVATAR_COLORS[random.nextInt(AVATAR_COLORS.length)];
        do {
            eveAvatarColor = AVATAR_COLORS[random.nextInt(AVATAR_COLORS.length)];
        } while (eveAvatarColor.equals(userAvatarColor));
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
                DialogBox.getUserDialog(input, userAvatarColor),
                DialogBox.getEveDialog(response, eveAvatarColor));
        userInput.clear();

        if (eve.isExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
