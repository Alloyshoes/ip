package eve.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * A single chat bubble: a message label next to a round, colored avatar
 * with an initial letter -- either something the user typed, or Eve's
 * response to it. Each speaker keeps the same randomly-chosen avatar color
 * for the whole session (see {@link MainWindow}), since real avatar images
 * aren't available. Built from DialogBox.fxml; use {@link #getUserDialog}
 * or {@link #getEveDialog} rather than the constructor directly, since they
 * also set the alignment/avatar position that distinguishes the two.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_DIAMETER = 36;

    @FXML
    private Label dialog;
    @FXML
    private StackPane avatarPane;

    private DialogBox(String text, Color avatarColor, String avatarLetter) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load DialogBox.fxml", e);
        }
        dialog.setText(text);
        avatarPane.getChildren().add(createAvatar(avatarColor, avatarLetter));
    }

    /**
     * Returns a dialog box for something the user typed, with the avatar on
     * the outer (right) edge of the conversation.
     *
     * @param text the user's input.
     * @param avatarColor this session's color for the user's avatar.
     */
    public static DialogBox getUserDialog(String text, Color avatarColor) {
        DialogBox box = new DialogBox(text, avatarColor, "Y");
        box.setAlignment(Pos.CENTER_RIGHT);
        box.dialog.getStyleClass().add("user-dialog");
        box.flip();
        return box;
    }

    /**
     * Returns a dialog box for Eve's response, with the avatar on the outer
     * (left) edge of the conversation.
     *
     * @param text Eve's response.
     * @param avatarColor this session's color for Eve's avatar.
     */
    public static DialogBox getEveDialog(String text, Color avatarColor) {
        DialogBox box = new DialogBox(text, avatarColor, "E");
        box.setAlignment(Pos.CENTER_LEFT);
        box.dialog.getStyleClass().add("eve-dialog");
        return box;
    }

    /** Swaps the avatar and message order, so the avatar ends up on the outer edge of a right-aligned dialog. */
    private void flip() {
        getChildren().setAll(dialog, avatarPane);
    }

    /** Builds a round, colored avatar with a single letter centered in it. */
    private static StackPane createAvatar(Color color, String letter) {
        Circle circle = new Circle(AVATAR_DIAMETER / 2, color);
        Text initial = new Text(letter);
        initial.setFill(Color.WHITE);
        initial.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        StackPane pane = new StackPane(circle, initial);
        pane.setPrefSize(AVATAR_DIAMETER, AVATAR_DIAMETER);
        pane.setMinSize(AVATAR_DIAMETER, AVATAR_DIAMETER);
        return pane;
    }
}
