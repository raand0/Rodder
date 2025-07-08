package src;

import javafx.application.Application; // JavaFX base application class
import javafx.fxml.FXMLLoader;         // Loads FXML files
import javafx.scene.Scene;            // JavaFX scene object
import javafx.scene.image.Image;      // Used to set application icon
import javafx.stage.Stage;            // Represents the main window

public class Main extends Application {

    // Entry point for JavaFX application
    @Override
    public void start(Stage stage) throws Exception {
        // Load the login scene from FXML file
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/src/GUI/loginScene.fxml"));
        
        // Create a new scene using the loaded FXML layout
        Scene scene = new Scene(fxmlLoader.load());

        // Set title for the main window
        stage.setTitle("Rodder");

        // Apply the created scene to the stage
        stage.setScene(scene);

        // Disable window resizing
        stage.setResizable(false);

        // Set the application icon
        stage.getIcons().add(new Image(getClass().getResource("/src/icon/fishing-rod.ico").toString()));

        // Display the stage (main window)
        stage.show();
    }

    // Main method: launches the JavaFX application
    public static void main(String[] args) {
        launch(); // Calls the start() method internally
    }
}
