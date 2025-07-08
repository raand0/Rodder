package src;
/**
 * Main controller class for the application's UI.
 * Manages the main view, page navigation, and toggle functionality.
 */
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class Controller {
    // Controller for the macro settings page
    public Mcontroller macroController;
    
    // FXML UI components
    @FXML
    private ToggleButton Enable;          // Toggle button to enable/disable the macro
    @FXML
    private Button MacroPage;             // Button to navigate to the macro settings page
    @FXML
    private Button ContactPage;           // Button to navigate to the contact/tutorial page
    @FXML
    private StackPane contentArea;        // Container for the current page content
    @FXML
    private CheckBox backToSword;         // Checkbox for the "back to sword" option
    
    // Macro instance to handle the actual macro functionality
    private Macro macro;

    /**
     * Initializes the controller and UI components.
     * This method is automatically called by JavaFX after FXML loading.
     */
    @FXML
    private void initialize() {
        // Initialize macro with reference to the toggle button
        macro = new Macro(Enable);

        // Set initial style for the enable/disable toggle button
        updateToggleButtonStyle();

        // Highlight the default page (Macro page) and load it
        MacroPage.setStyle("-fx-background-color: rgb(51, 51, 51);");
        loadPage("/src/GUI/MacroScene.fxml");

        // Configure the toggle button functionality using an anonymous inner class
        Enable.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Update button style when clicked
                updateToggleButtonStyle();
                
                // Enable or disable the macro based on the toggle button state
                boolean isEnabled = Enable.isSelected();
                macro.setEnabled(isEnabled);
            }
        });
    }

    /**
     * Updates the toggle button style and text based on its selected state.
     * Sets text to "ON" with green styling when selected, "OFF" with red styling when not selected.
     */
    private void updateToggleButtonStyle() {
        if (Enable.isSelected()) {
            // Style for ON state (green)
            Enable.setText("ON");
            Enable.setStyle("-fx-background-color: transparent; " +
                           "-fx-text-fill: rgb(0, 125, 46); " +
                           "-fx-border-width: 2px; " +
                           "-fx-border-color: rgb(0, 125, 46); " +
                           "-fx-pref-width: 70px; " +
                           "-fx-pref-height: 50px;");
        } else {
            // Style for OFF state (red)
            Enable.setText("OFF");
            Enable.setStyle("-fx-background-color: transparent; " +
                           "-fx-text-fill: rgb(171, 0, 0); " +
                           "-fx-border-width: 2px; " +
                           "-fx-border-color: rgb(171,0,0); " +
                           "-fx-pref-width: 70px; " +
                           "-fx-pref-height: 50px;");
        }
    }

    /**
     * Handles navigation to the Macro page.
     * Highlights the Macro button and loads the MacroScene.fxml.
     * 
     * @param event The action event triggered by clicking the button
     */
    @FXML
    private void pageMacro(ActionEvent event) {
        // Reset all page button styles
        resetPageStyles();
        // Highlight the Macro page button
        MacroPage.setStyle("-fx-background-color: rgb(51, 51, 51);");
        // Load the Macro page content
        loadPage("/src/GUI/MacroScene.fxml");
    }

    /**
     * Handles navigation to the Tutorial/Contact page.
     * Highlights the Contact button and loads the howToUse.fxml.
     * 
     * @param event The action event triggered by clicking the button
     */
    @FXML
    private void pageTutorial(ActionEvent event) {
        // Reset all page button styles
        resetPageStyles();
        // Highlight the Contact page button
        ContactPage.setStyle("-fx-background-color: rgb(51, 51, 51);");
        // Load the tutorial/contact page content
        loadPage("/src/GUI/howToUse.fxml");
    }

    /**
     * Resets the highlighting styles of all navigation buttons to default.
     * Called before setting a new active page to ensure only one button is highlighted.
     */
    private void resetPageStyles() {
        // Reset Macro page button to default style
        MacroPage.setStyle("-fx-background-color: rgb(31,31,31);");
        // Reset Contact page button to default style
        ContactPage.setStyle("-fx-background-color: rgb(31,31,31)");
    }

    /**
     * Loads a specified FXML page into the content area.
     * If the loaded page is MacroScene.fxml, stores a reference to its controller.
     * 
     * @param fxml The path to the FXML file to load
     */
    @FXML
    private void loadPage(String fxml) {
        try {
            // Create a new FXMLLoader for the specified FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            // Load the FXML content into an AnchorPane
            AnchorPane page = loader.load();
            // Replace all children in the content area with the new page
            contentArea.getChildren().setAll(page);
    
            // Check if the loaded page is MacroScene.fxml
            if (fxml.equals("/src/GUI/MacroScene.fxml")) {
                // If it is, store a reference to its controller for later use
                macroController = loader.getController();
            } else {
                // If it's not, clear the controller reference
                macroController = null;
            }
    
        } catch (IOException e) {
            // Print stack trace if there's an error loading the page
            e.printStackTrace();
        }
    }
    
    /**
     * Cleans up resources when the application is closed.
     * This should be called when the application is shutting down.
     */
    public void cleanup() {
        // If the macro exists, call its cleanup method
        if (macro != null) {
            macro.cleanup();
        }
    }
}