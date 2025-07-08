package src;
/**
 * Controller class for the application's UI.
 * Manages keyboard shortcuts and settings for the rod macro functionality.
 */
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

public class Mcontroller {
    /**
     * Settings class to store user preferences.
     * Used for JSON serialization/deserialization of the settings file.
     */
    public static class Settings {
        // Key bindings for various actions
        public String swordKey;
        public String rodKey;
        public String macKey;
        public String toggleKey;
        // Option to switch back to sword after action
        public boolean backToSwordSelected;
    
        /**
         * Default constructor required for Jackson JSON deserialization
         */
        public Settings() {} 
    
        /**
         * Constructor with all settings parameters
         * @param swordKey The key for selecting sword
         * @param rodKey The key for selecting fishing rod
         * @param macKey The key for executing the macro
         * @param toggleKey The key for toggling macro functionality
         * @param backToSwordSelected Whether to switch back to sword after using rod
         */
        public Settings(String swordKey, String rodKey, String macKey, String toggleKey, boolean backToSwordSelected) {
            this.swordKey = swordKey;
            this.rodKey = rodKey;
            this.macKey = macKey;
            this.toggleKey = toggleKey;
            this.backToSwordSelected = backToSwordSelected;
        }
    }    
    
    // FXML UI components
    @FXML
    private ComboBox<String> macKey;       // ComboBox for selecting macro key
    @FXML
    private ComboBox<String> swordKey;     // ComboBox for selecting sword key
    @FXML
    private ComboBox<String> rodKey;       // ComboBox for selecting rod key
    @FXML
    private ComboBox<String> togKey;       // ComboBox for selecting toggle key
    @FXML
    private Label changeToSword;           // Label for the "change to sword" option
    @FXML
    private Label swordLabel;              // Label for the sword key selection
    @FXML
    private Label toggleLabel;             // Label for the toggle key selection
    @FXML
    private Label macLabel;                // Label for the macro key selection
    @FXML
    private Label rodLabel;                // Label for the rod key selection
    @FXML
    private CheckBox backToSword;          // Checkbox for "change back to sword" option
    
    // Static variables for application state
    @FXML
    public static boolean isSelected;      // Flag indicating if "change back to sword" is selected
    public static int skeyCode = -1;       // Key code for sword key
    public static int rkeyCode = -1;       // Key code for rod key
    public static int mkeyCode = -1;       // Key code for macro key
    public static int toggleKeyCode = -1;  // Key code for toggle key
    
    // Maps to convert key names to Java key codes and native key codes
    private static final Map<String, Integer> keyMap = new HashMap<>();
    private static final Map<String, Integer> keyMapNative = new HashMap<>();
    
    /**
     * Static initializer block that populates the key mapping tables
     * Maps string representations of keys to their corresponding KeyEvent codes
     * and NativeKeyEvent codes
     */
    static {
        // Initialize mapping for Java AWT KeyEvent codes
        keyMap.put("A", KeyEvent.VK_A); 
        keyMap.put("B", KeyEvent.VK_B);
        keyMap.put("C", KeyEvent.VK_C);
        keyMap.put("D", KeyEvent.VK_D);
        keyMap.put("E", KeyEvent.VK_E);
        keyMap.put("F", KeyEvent.VK_F);
        keyMap.put("G", KeyEvent.VK_G);
        keyMap.put("H", KeyEvent.VK_H);
        keyMap.put("I", KeyEvent.VK_I);
        keyMap.put("J", KeyEvent.VK_J);
        keyMap.put("K", KeyEvent.VK_K);
        keyMap.put("L", KeyEvent.VK_L);
        keyMap.put("M", KeyEvent.VK_M);
        keyMap.put("N", KeyEvent.VK_N);
        keyMap.put("O", KeyEvent.VK_O);
        keyMap.put("P", KeyEvent.VK_P);
        keyMap.put("Q", KeyEvent.VK_Q);
        keyMap.put("R", KeyEvent.VK_R);
        keyMap.put("S", KeyEvent.VK_S);
        keyMap.put("T", KeyEvent.VK_T);
        keyMap.put("U", KeyEvent.VK_U);
        keyMap.put("V", KeyEvent.VK_V);
        keyMap.put("W", KeyEvent.VK_W);
        keyMap.put("X", KeyEvent.VK_X);
        keyMap.put("Y", KeyEvent.VK_Y);
        keyMap.put("Z", KeyEvent.VK_Z);
        keyMap.put("LShift", KeyEvent.VK_SHIFT);
        keyMap.put("LCtrl", KeyEvent.VK_CONTROL);
        keyMap.put("LAlt", KeyEvent.VK_ALT);
        keyMap.put("Space", KeyEvent.VK_SPACE);
        keyMap.put("TAB", KeyEvent.VK_TAB);
        keyMap.put("1", KeyEvent.VK_1);
        keyMap.put("2", KeyEvent.VK_2);
        keyMap.put("3", KeyEvent.VK_3);
        keyMap.put("4", KeyEvent.VK_4);
        keyMap.put("5", KeyEvent.VK_5);
        keyMap.put("6", KeyEvent.VK_6);
        keyMap.put("7", KeyEvent.VK_7);
        keyMap.put("8", KeyEvent.VK_8);
        keyMap.put("9", KeyEvent.VK_9);
        keyMap.put("0", KeyEvent.VK_0);

        // Initialize mapping for JNativeHook NativeKeyEvent codes
        keyMapNative.put("A", NativeKeyEvent.VC_A);
        keyMapNative.put("B", NativeKeyEvent.VC_B);
        keyMapNative.put("C", NativeKeyEvent.VC_C);
        keyMapNative.put("D", NativeKeyEvent.VC_D);
        keyMapNative.put("E", NativeKeyEvent.VC_E);
        keyMapNative.put("F", NativeKeyEvent.VC_F);
        keyMapNative.put("G", NativeKeyEvent.VC_G);
        keyMapNative.put("H", NativeKeyEvent.VC_H);
        keyMapNative.put("I", NativeKeyEvent.VC_I);
        keyMapNative.put("J", NativeKeyEvent.VC_J);
        keyMapNative.put("K", NativeKeyEvent.VC_K);
        keyMapNative.put("L", NativeKeyEvent.VC_L);
        keyMapNative.put("M", NativeKeyEvent.VC_M);
        keyMapNative.put("N", NativeKeyEvent.VC_N);
        keyMapNative.put("O", NativeKeyEvent.VC_O);
        keyMapNative.put("P", NativeKeyEvent.VC_P);
        keyMapNative.put("Q", NativeKeyEvent.VC_Q);
        keyMapNative.put("R", NativeKeyEvent.VC_R);
        keyMapNative.put("S", NativeKeyEvent.VC_S);
        keyMapNative.put("T", NativeKeyEvent.VC_T);
        keyMapNative.put("U", NativeKeyEvent.VC_U);
        keyMapNative.put("V", NativeKeyEvent.VC_V);
        keyMapNative.put("W", NativeKeyEvent.VC_W);
        keyMapNative.put("X", NativeKeyEvent.VC_X);
        keyMapNative.put("Y", NativeKeyEvent.VC_Y);
        keyMapNative.put("Z", NativeKeyEvent.VC_Z);
        keyMapNative.put("LShift", NativeKeyEvent.VC_SHIFT);
        keyMapNative.put("LCtrl", NativeKeyEvent.VC_CONTROL);
        keyMapNative.put("LAlt", NativeKeyEvent.VC_ALT);
        keyMapNative.put("Space", NativeKeyEvent.VC_SPACE);
        keyMapNative.put("TAB", NativeKeyEvent.VC_TAB);
        keyMapNative.put("1", NativeKeyEvent.VC_1);
        keyMapNative.put("2", NativeKeyEvent.VC_2);
        keyMapNative.put("3", NativeKeyEvent.VC_3);
        keyMapNative.put("4", NativeKeyEvent.VC_4);
        keyMapNative.put("5", NativeKeyEvent.VC_5);
        keyMapNative.put("6", NativeKeyEvent.VC_6);
        keyMapNative.put("7", NativeKeyEvent.VC_7);
        keyMapNative.put("8", NativeKeyEvent.VC_8);
        keyMapNative.put("9", NativeKeyEvent.VC_9);
        keyMapNative.put("0", NativeKeyEvent.VC_0);
        keyMapNative.put("`(GRAVE)", NativeKeyEvent.VC_BACKQUOTE);
    }

    /**
     * Initializes the UI components and loads saved settings.
     * This method is automatically called by JavaFX after FXML loading.
     */
    @FXML
    private void initialize() {
        // Load saved settings from file
        ObjectMapper mapper = new ObjectMapper();
        File settingsFile = new File(System.getProperty("user.home") + "/Rodder/settings.json");
        
        // Check if settings file exists and load it
        if (settingsFile.exists()) {
            try {
                // Deserialize JSON to Settings object
                Settings loaded = mapper.readValue(settingsFile, Settings.class);
                
                // Apply loaded settings to UI components
                if (loaded.swordKey != null) swordKey.getSelectionModel().select(loaded.swordKey);
                if (loaded.rodKey != null) rodKey.getSelectionModel().select(loaded.rodKey);
                if (loaded.macKey != null) macKey.getSelectionModel().select(loaded.macKey);
                if (loaded.toggleKey != null) togKey.getSelectionModel().select(loaded.toggleKey);
                backToSword.setSelected(loaded.backToSwordSelected);
                isSelected = loaded.backToSwordSelected;
                
                // Update static keyCodes based on loaded keys
                skeyCode = keyMap.getOrDefault(loaded.swordKey, -1);
                rkeyCode = keyMap.getOrDefault(loaded.rodKey, -1);
                mkeyCode = keyMapNative.getOrDefault(loaded.macKey, -1);
                toggleKeyCode = keyMapNative.getOrDefault(loaded.toggleKey, -1);

            } catch (IOException e) {
                // Print stack trace if there's an error loading settings
                e.printStackTrace();
            }
        }

        // Setup tooltips for labels to provide additional information
        changeToSword.setTooltip(new Tooltip("ON: Changes back to sword.\nOFF: Doesn't change back to sword."));
        swordLabel.setTooltip(new Tooltip("Your sword hotkey."));
        rodLabel.setTooltip(new Tooltip("Your rod hotkey."));
        macLabel.setTooltip(new Tooltip("The key to execute macro."));
        toggleLabel.setTooltip(new Tooltip("Shortcut key to enable and disable macro."));

        // Populate dropdown menu items for key selection
        // Add "None" option first, followed by alphabet keys
        swordKey.getItems().addAll(
            "None",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "LShift", "LCtrl", "LAlt", "Space", "TAB",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"
        );
        rodKey.getItems().addAll(
            "None",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "LShift", "LCtrl", "LAlt", "Space", "TAB",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"
        );
        macKey.getItems().addAll(
            "None",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
            "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
            "LShift", "LCtrl", "LAlt", "Space", "TAB",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"
        );
        // Toggle key has limited options (only modifier keys)
        togKey.getItems().addAll(
            "None",
            "LShift", "LCtrl", "LAlt", "TAB", "`(GRAVE)"
        );

        // Set action handlers for ComboBox selection changes
        // Sword key selection handler
        swordKey.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Get selected key and update the global key code
                String selected = swordKey.getValue();
                if (selected == null || selected.equals("None")) {
                    skeyCode = -1;
                } else {
                    skeyCode = keyMap.getOrDefault(selected, -1);
                }
            }
        });
        
        // Rod key selection handler
        rodKey.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Get selected key and update the global key code
                String selected = rodKey.getValue();
                if (selected == null || selected.equals("None")) {
                    rkeyCode = -1;
                } else {
                    rkeyCode = keyMap.getOrDefault(selected, -1);
                }
            }
        });
        
        // Macro key selection handler
        macKey.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Get selected key and update the global key code
                String selected = macKey.getValue();
                if (selected == null || selected.equals("None")) {
                    mkeyCode = -1;
                } else {
                    mkeyCode = keyMapNative.getOrDefault(selected, -1);
                }
            }
        });
        
        // Toggle key selection handler
        togKey.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Get selected key and update the global key code
                String selected = togKey.getValue();
                if (selected == null || selected.equals("None")) {
                    toggleKeyCode = -1;
                } else {
                    toggleKeyCode = keyMapNative.getOrDefault(selected, -1);
                }
            }
        });

        // Checkbox handler for "back to sword" option
        backToSword.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Update global isSelected flag based on checkbox state
                isSelected = backToSword.isSelected();
            }
        });
    }

    /**
     * Saves the current settings to a JSON file.
     * Creates a Settings object with current values and serializes it to disk.
     */
    public void saveSettings() {
        // Create ObjectMapper for JSON serialization
        ObjectMapper mapper = new ObjectMapper();
        
        // Create a new Settings object with current values
        // Handle "None" selection by setting the value to null
        String swordKeyValue = swordKey.getValue();
        String rodKeyValue = rodKey.getValue();
        String macKeyValue = macKey.getValue();
        String togKeyValue = togKey.getValue();
        
        // Convert "None" selections to null
        if (swordKeyValue != null && swordKeyValue.equals("None")) swordKeyValue = null;
        if (rodKeyValue != null && rodKeyValue.equals("None")) rodKeyValue = null;
        if (macKeyValue != null && macKeyValue.equals("None")) macKeyValue = null;
        if (togKeyValue != null && togKeyValue.equals("None")) togKeyValue = null;
        
        Settings settings = new Settings(
            swordKeyValue,
            rodKeyValue,
            macKeyValue,
            togKeyValue,
            backToSword.isSelected()
        );
        
        try {
            // Write the settings to a JSON file in the user's home directory
            mapper.writerWithDefaultPrettyPrinter().writeValue(
                new File(System.getProperty("user.home") + "/Rodder/settings.json"), 
                settings
            );
        } catch (IOException e) {
            // Print stack trace if there's an error saving settings
            e.printStackTrace();
        }
    }    
}