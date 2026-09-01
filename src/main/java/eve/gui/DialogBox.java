package eve.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * A single chat bubble: either something the user typed, or Eve's response
 * to it. Built from DialogBox.fxml; use {@link #getUserDialog} or
 * {@link #getEveDialog} rather than the constructor directly, since they
 * also set the alignment/styling that distinguishes the two.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load DialogBox.fxml", e);
        }
        dialog.setText(text);
    }

    /**
     * Returns a dialog box for something the user typed, aligned to the
     * right of the conversation.
     *
     * @param text the user's input.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox box = new DialogBox(text);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.getStyleClass().add("user-dialog");
        return box;
    }

    /**
     * Returns a dialog box for Eve's response, aligned to the left of the
     * conversation.
     *
     * @param text Eve's response.
     */
    public static DialogBox getEveDialog(String text) {
        DialogBox box = new DialogBox(text);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("eve-dialog");
        return box;
    }
}
