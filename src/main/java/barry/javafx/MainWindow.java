package barry.javafx;

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
 * Controller for the main application window.
 * <p>
 * The {@code MainWindow} wires the JavaFX view to the Barry backend:
 * it renders user and bot messages as dialog bubbles, keeps the scroll
 * position pinned to the latest message, and handles user input events.
 * </p>
 * <p>
 * Responsibilities:
 * </p>
 * <ul>
 *   <li>Auto-scroll the conversation view as new messages arrive.</li>
 *   <li>Display Barry's greeting upon initialization via {@link #setBarry(Barry)}.</li>
 *   <li>Handle user input (pressing the send button / Enter) and render the
 *       corresponding user/bot dialog boxes.</li>
 *   <li>Show errors in a dedicated error bubble.</li>
 *   <li>Schedule application exit after a short delay when the user types {@code bye}.</li>
 * </ul>
 */
public class MainWindow extends AnchorPane {
    /**
     * Avatar for the user bubbles.
     */
    private final Image userImage =
            new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    /**
     * Avatar for Barry's bubbles.
     */
    private final Image barryImage =
            new Image(this.getClass().getResourceAsStream("/images/DaBarry.png"));
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    /**
     * Barry backend that processes user commands and produces replies.
     */
    private Barry barry;

    /**
     * Initializes view behavior after the FXML has been loaded.
     * <p>
     * This method is called automatically by the {@code FXMLLoader}.
     * It binds the scroll position to the conversation container height
     * so that the latest message is always visible, and enables width-based
     * reflow for a responsive layout.
     * </p>
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        // Ensure the window is resizable-friendly
        scrollPane.setFitToWidth(true);
        dialogContainer.setFillWidth(true);
    }

    /**
     * Injects the backend instance and shows Barry's greeting.
     * <p>
     * This method must be called by the application bootstrap after
     * constructing {@code MainWindow}. It stores the provided {@link Barry}
     * instance for subsequent requests and renders the initial greeting
     * message in the dialog container.
     * </p>
     *
     * @param barry the Barry backend to use for generating replies
     */
    public void setBarry(Barry barry) {
        this.barry = barry;
        // show initial message from Barry/Gui
        dialogContainer.getChildren().add(
                DialogBox.getBarryDialog(barry.getGreeting(), barryImage)
        );
    }

    /**
     * Handles the send action from the UI (button press / Enter key).
     * <p>
     * Behavior:
     * </p>
     * <ol>
     *   <li>Reads and clears the text from the input field; returns early if blank.</li>
     *   <li>If the user typed {@code "bye"}, schedules an application exit after 1 second.</li>
     *   <li>Asks {@link Barry} for a response and appends both the user bubble and
     *       Barry's reply to the dialog container.</li>
     *   <li>If a {@link BarryException} is thrown, formats the error message using
     *       {@link Gui} and renders it as an error bubble.</li>
     * </ol>
     * <p>
     * All UI updates occur on the JavaFX Application Thread.
     * </p>
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }

        userInput.clear();

        if (input.equals("bye")) {
            PauseTransition delay = new PauseTransition(Duration.millis(1000));
            delay.setOnFinished(e -> Platform.exit());
            delay.play();
        }

        try {
            String response = barry.getResponse(input);
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(input, userImage),
                    DialogBox.getBarryDialog(response, barryImage)
            );
        } catch (BarryException e) {
            ArrayList<String> s = new ArrayList<>();
            s.add("OOPS!!! " + e.getMessage());
            Gui gui = new Gui();
            dialogContainer.getChildren().addAll(
                    DialogBox.getUserDialog(input, userImage),
                    DialogBox.getErrorDialog(gui.print(s), barryImage)
            );
        }
    }
}
