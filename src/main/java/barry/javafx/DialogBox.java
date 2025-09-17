package barry.javafx;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents a single chat bubble rendered in the conversation view.
 * <p>
 * Each {@code DialogBox} consists of an avatar ({@link ImageView}) and a text
 * label. The FXML markup is loaded from {@code /view/DialogBox.fxml}, after
 * which the instance is styled using CSS classes:
 * </p>
 * <ul>
 *   <li><b>{@code user-label}</b> – the user's message bubble (right-aligned)</li>
 *   <li><b>{@code reply-label}</b> – Barry's message bubble (left-aligned)</li>
 *   <li><b>{@code error-label}</b> – error bubble to highlight problems (left-aligned)</li>
 * </ul>
 * <p>
 * Use the static factory methods to obtain correctly styled/positioned dialog boxes:
 * {@link #getUserDialog(String, Image)}, {@link #getBarryDialog(String, Image)},
 * and {@link #getErrorDialog(String, Image)}.
 * </p>
 */
public class DialogBox extends HBox {

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    /**
     * Creates a {@code DialogBox} by loading {@code /view/DialogBox.fxml} and
     * populating the text and avatar.
     *
     * @param text the message to display
     * @param img  the avatar image to show beside the message
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            // In this context, failing fast is acceptable; log to stderr for debugging.
            e.printStackTrace();
        }
        dialog.setText(text);
        displayPicture.setImage(img);
    }

    /**
     * Flips the child order so that the avatar is placed on the <em>left</em> and
     * the text bubble on the <em>right</em>, and left-aligns the row.
     * <p>
     * This is used for Barry/error messages to achieve the asymmetric chat layout
     * (bot on the left, user on the right).
     * </p>
     */
    private void flip() {
        ObservableList<Node> nodes = FXCollections.observableArrayList(getChildren());
        Collections.reverse(nodes);
        getChildren().setAll(nodes);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a right-aligned dialog bubble representing the user's message.
     *
     * @param text the message to display
     * @param img  the user's avatar image
     * @return a {@code DialogBox} styled with {@code user-label}
     */
    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("user-label");
        db.setAlignment(Pos.TOP_RIGHT);
        return db;
    }

    /**
     * Creates a left-aligned dialog bubble representing Barry's message.
     *
     * @param text the message to display
     * @param img  Barry's avatar image
     * @return a {@code DialogBox} styled with {@code reply-label}
     */
    public static DialogBox getBarryDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("reply-label");
        db.flip(); // put avatar on the left, text on the right, left-align row
        return db;
    }

    /**
     * Creates a left-aligned dialog bubble styled for error messages to draw attention.
     *
     * @param text the error message to display
     * @param img  Barry's (or a warning) avatar image
     * @return a {@code DialogBox} styled with {@code error-label}
     */
    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.dialog.getStyleClass().add("error-label");
        db.flip(); // same left-side layout as Barry replies
        return db;
    }
}
