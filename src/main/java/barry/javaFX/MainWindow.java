package barry.javaFX;

import java.util.ArrayList;

import barry.Barry;
import barry.data.exceptions.BarryException;
import barry.ui.Gui;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI.
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

    private Barry barry;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        // Ensure the window is resizable-friendly
        scrollPane.setFitToWidth(true);
        dialogContainer.setFillWidth(true);
    }

    /** Injects the Source.Duke instance */
    public void setBarry(Barry barry) {
        this.barry = barry;
        // show initial message from Barry/Gui
        dialogContainer.getChildren().add(
                DialogBox.getDukeDialog(barry.getGreeting(), dukeImage)
        );
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }

        userInput.clear();

        if (input.equals("bye")) {
            PauseTransition delay = new PauseTransition(Duration.millis(1000));
            delay.setOnFinished(e -> {
                Platform.exit();
            });
            delay.play();
        }

        try {
            String response = barry.getResponse(input); // or your parser/execute path
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(input, userImage),
                    DialogBox.getDukeDialog(response, dukeImage)
            );
        } catch (BarryException e) {
            ArrayList<String> s = new ArrayList<>();
            s.add("OOPS!!! " + e.getMessage());
            Gui gui = new Gui();
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(input, userImage),
                    DialogBox.getDukeDialog(gui.print(s), dukeImage)
            );
        }
    }
}
