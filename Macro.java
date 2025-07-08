package src;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.control.ToggleButton;

public class Macro implements NativeKeyListener {
    private Robot robot; // Used for simulating keyboard and mouse actions
    private boolean MacroPressed = false; // Tracks if macro key is currently pressed
    private boolean isEnabled = false; // Tracks whether macro is enabled or not
    private ToggleButton toggleButton; // UI button to show toggle state

    // Constructor
    public Macro(ToggleButton toggleButton) {
        this.toggleButton = toggleButton;

        try {
            // Initialize the Robot class for input simulation
            robot = new Robot();

            // Disable unnecessary logging from JNativeHook
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);

            // Register the native key hook and this class as a listener
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Called when a key is pressed
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        // If the pressed key is the toggle key
        if (e.getKeyCode() == Mcontroller.toggleKeyCode) {
            final boolean newState = !isEnabled(); // Flip enabled state
            setEnabled(newState); // Apply new state

            Toolkit.getDefaultToolkit().beep(); // Beep to indicate toggle

            // Update the UI button on the JavaFX thread
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    toggleButton.setSelected(newState);

                    if (newState) {
                        toggleButton.setText("ON");
                        toggleButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgb(0, 125, 46); -fx-border-width: 2px; -fx-border-color: rgb(0, 125, 46); -fx-pref-width: 70px; -fx-pref-height: 50px;");
                    } else {
                        toggleButton.setText("OFF");
                        toggleButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgb(171, 0, 0); -fx-border-width: 2px; -fx-border-color: rgb(171,0,0); -fx-pref-width: 70px; -fx-pref-height: 50px;");
                    }
                }
            });
            return;
        }

        // If macro is not enabled, ignore key
        if (!isEnabled) return;

        // If macro key is pressed and macro isn’t already active
        if (e.getKeyCode() == Mcontroller.mkeyCode && !MacroPressed) {
            MacroPressed = true; // Set macro as active
            try {
                executeMacro(); // Run macro logic
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Core macro action
    private void executeMacro() throws InterruptedException {
        // Press and release the key to switch to rod
        robot.keyPress(Mcontroller.rkeyCode);
        robot.keyRelease(Mcontroller.rkeyCode);

        // Simulate right-click (throw rod)
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
    }

    // Called when a key is released
    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        // Ignore if macro is disabled
        if (!isEnabled) return;

        // If macro key is released
        if (e.getKeyCode() == Mcontroller.mkeyCode) {
            MacroPressed = false; // Reset macro pressed state

            if (Mcontroller.isSelected) {
                // Press and release key to switch to sword
                robot.keyPress(Mcontroller.skeyCode);
                robot.keyRelease(Mcontroller.skeyCode);
            } else {
                return; // Do nothing if switch is not selected
            }
        }
    }

    // Enable or disable the macro
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    // Check macro status
    public boolean isEnabled() {
        return isEnabled;
    }

    // Clean up and unregister native hook
    public void cleanup() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
