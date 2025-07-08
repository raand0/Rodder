package src;

/**
 * Controller class for handling user authentication and license validation.
 * This controller manages the login screen, license key verification, and transition
 * to the main application upon successful authentication.
 */
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.net.http.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Desktop;
import java.nio.file.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class loginController {
    // FXML UI components
    @FXML private TextField licenseKey;        // Text field for entering license key
    @FXML private Button Activate;             // Button to activate the license
    @FXML private Hyperlink link;              // Link to purchase page
    @FXML private Label ErrorLabel;            // Label for displaying error messages

    // Constants for license validation
    private final String productId = "krZ7QS2yvIspHczXhAO35w==";  // Product ID for Gumroad API
    private final Path licensePath = Paths.get(System.getProperty("user.home"), "Rodder", ".rodder_license");  // Path to saved license file

    // Secret key for AES encryption (16 chars = 128 bit key)
    private final String secretKey = "MySecretKey12345";  // Key used for encrypting/decrypting the saved license

    /**
     * Initializes the controller and sets up event handlers.
     * This method is automatically called by JavaFX after FXML loading.
     * Also attempts to auto-login using saved license information.
     */
    @FXML
    public void initialize() {
        // Set up event handlers for UI components
        link.setOnAction(this::handleLinkClick);
        Activate.setOnAction(this::handleActivateClick);

        // Attempt auto-login if a saved license exists
        try {
            if (Files.exists(licensePath)) {
                String encryptedKey = Files.readString(licensePath).trim();
                if (!encryptedKey.isEmpty()) {
                    // Decrypt and use the saved license key
                    String savedKey = decrypt(encryptedKey);
                    licenseKey.setText(savedKey);
                    handleActivateClick(null);  // Trigger license verification automatically
                }
            }
        } catch (Exception e) {
            ErrorLabel.setText("Auto-login failed.");
        }
    }

    /**
     * Handles clicks on the purchase link.
     * Opens the default browser to the product's purchase page.
     * 
     * @param event The action event triggered by clicking the link
     */
    private void handleLinkClick(ActionEvent event) {
        try {
            // Open the product purchase page in the default browser
            Desktop.getDesktop().browse(new URI("https://gumroad.com/l/Rodder"));
        } catch (Exception e) {
            ErrorLabel.setText("Failed to open link.");
        }
    }

    /**
     * Handles license activation when the Activate button is clicked.
     * Validates the license key with Gumroad's API and proceeds to the main application if valid.
     * 
     * @param event The action event triggered by clicking the button (can be null for auto-login)
     */
    private void handleActivateClick(ActionEvent event) {
        // Get the license key from the text field
        final String key = licenseKey.getText();
        
        // Validate that a key was entered
        if (key == null || key.trim().isEmpty()) {
            ErrorLabel.setText("Please enter your license key.");
            return;
        }

        // Update UI to show verification is in progress
        ErrorLabel.setText("Verifying license...");

        // Create a new thread for the network request to avoid blocking the UI
        Thread thread = new Thread(() -> {
            try {
                // Prepare request parameters
                String data = "product_id=" + URLEncoder.encode(productId, StandardCharsets.UTF_8) +
                              "&license_key=" + URLEncoder.encode(key, StandardCharsets.UTF_8);

                // Build HTTP request for Gumroad API
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.gumroad.com/v2/licenses/verify"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(data))
                        .build();

                // Send the request and get response
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse the JSON response
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.body());

                // Check if the license is valid
                boolean success = root.path("success").asBoolean(false);

                if (success) {
                    // License is valid, encrypt and save it for future auto-login
                    String encryptedKey = encrypt(key);
                    Files.createDirectories(licensePath.getParent());
                    Files.writeString(licensePath, encryptedKey);

                    // Update UI and load main application on the JavaFX thread
                    javafx.application.Platform.runLater(() -> {
                        ErrorLabel.setText("License valid! Logging in...");
                        try {
                            // Load the main application FXML
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/src/GUI/GUI.fxml"));
                            Scene scene = new Scene(loader.load());
                            Controller mainController = loader.getController();
                            
                            // Get the current stage and set the new scene
                            Stage stage = (Stage) Activate.getScene().getWindow();
                            stage.setScene(scene);
                            stage.setTitle("Rodder");
                            
                            // Set up application close handler for proper cleanup
                            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                                @Override
                                public void handle(WindowEvent event) {
                                    // Save settings before closing
                                    if (mainController.macroController != null) {
                                        mainController.macroController.saveSettings();
                                    }
                                    // Clean up resources
                                    if (mainController != null) {
                                        mainController.cleanup();
                                    }
                                    // Exit the application
                                    Platform.exit();
                                    System.exit(0);
                                }
                            });                            
                            // Show the main application window
                            stage.show();
                        } catch (Exception e) {
                            // Log and display any errors that occur during loading
                            e.printStackTrace();
                            System.out.println("Failed to load main page: " + e.getMessage());
                            ErrorLabel.setText("Failed to load main page.");
                        }
                    });
                } else {
                    // License is invalid, show error message
                    final String message = root.path("message").asText("Invalid license key.");
                    javafx.application.Platform.runLater(() -> ErrorLabel.setText("Invalid: " + message));
                }
            } catch (Exception e) {
                // Show any network or other errors
                javafx.application.Platform.runLater(() -> ErrorLabel.setText("Connection failed: " + e.getMessage()));
            }
        });
        thread.start();
    }

    /**
     * Encrypts a string using AES encryption.
     * Used to securely store the license key on disk.
     * 
     * @param strToEncrypt The string to encrypt
     * @return Base64-encoded encrypted string
     * @throws Exception If encryption fails
     */
    private String encrypt(String strToEncrypt) throws Exception {
        // Create encryption key from the secret
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        
        // Initialize cipher in encryption mode
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        
        // Encrypt the string and encode with Base64
        byte[] encryptedBytes = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decrypts a previously encrypted string using AES decryption.
     * Used to retrieve the stored license key.
     * 
     * @param strToDecrypt The Base64-encoded encrypted string to decrypt
     * @return The decrypted string
     * @throws Exception If decryption fails
     */
    private String decrypt(String strToDecrypt) throws Exception {
        // Create decryption key from the secret
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        
        // Initialize cipher in decryption mode
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        
        // Decode Base64 and decrypt
        byte[] decodedBytes = Base64.getDecoder().decode(strToDecrypt);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        
        // Convert bytes back to string
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}