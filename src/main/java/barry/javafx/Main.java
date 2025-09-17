package barry.javafx;

import java.io.IOException;

import barry.Barry;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Source.Barry using FXML.
 */
public class Main extends Application {

    private Barry barry = new Barry();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setBarry(barry); // inject the Source.Barry instance
            stage.setTitle("Barry — Personal Task Assistant"); // <- shows on the OS title bar
            stage.getIcons().add(new Image(
                    getClass().getResource("/images/DaBarry.png").toExternalForm() // optional app icon
            ));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
