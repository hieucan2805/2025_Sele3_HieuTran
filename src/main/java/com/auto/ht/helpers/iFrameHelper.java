package com.auto.ht.helpers;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.NoSuchFrameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Supplier;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

/**
 * Helper class for working with iFrames in Selenide tests.
 * This class provides methods to explicitly switch between frames
 * and perform operations within specific frames.
 */
public class iFrameHelper {
    private static final Logger log = LoggerFactory.getLogger(iFrameHelper.class);
    
    /**
     * Switches to a frame using its CSS selector
     * 
     * @param frameSelector CSS selector for the frame
     * @return true if successfully switched to frame
     */
    public static boolean switchToFrame(String frameSelector) {
        try {
            log.debug("Switching to frame: {}", frameSelector);
            SelenideElement frameElement = $(frameSelector).shouldBe(visible, Duration.ofSeconds(5));
            switchTo().frame(frameElement);
            return true;
        } catch (Exception e) {
            log.warn("Failed to switch to frame {}: {}", frameSelector, e.getMessage());
            return false;
        }
    }

    /**
     * Switches to a frame by index
     * 
     * @param index Index of the frame (0-based)
     * @return true if successfully switched to frame
     */
    public static boolean switchToFrame(int index) {
        try {
            log.debug("Switching to frame at index: {}", index);
            switchTo().frame(index);
            return true;
        } catch (NoSuchFrameException e) {
            log.warn("No frame found at index {}: {}", index, e.getMessage());
            return false;
        }
    }

    /**
     * Switches to a frame and performs an action, then switches back to the parent
     * 
     * @param frameSelector CSS selector for the frame
     * @param action Lambda function containing the actions to perform in the frame
     */
    public static void withFrame(String frameSelector, Runnable action) {
        try {
            if (switchToFrame(frameSelector)) {
                try {
                    action.run();
                } finally {
                    switchTo().parentFrame();
                    log.debug("Switched back to parent frame from {}", frameSelector);
                }
            }
        } catch (Exception e) {
            log.error("Error while working with frame {}: {}", frameSelector, e.getMessage());
            switchTo().defaultContent(); // Safety measure to return to main document
        }
    }
    
    /**
     * Switches to a frame, performs an action that returns a result, then switches back
     * 
     * @param <T> Type of the result
     * @param frameSelector CSS selector for the frame
     * @param supplier Lambda function returning a value from operations in the frame
     * @return The result of the operations or null if there was an error
     */
    public static <T> T withFrameResult(String frameSelector, Supplier<T> supplier) {
        try {
            if (switchToFrame(frameSelector)) {
                try {
                    return supplier.get();
                } finally {
                    switchTo().parentFrame();
                    log.debug("Switched back to parent frame from {}", frameSelector);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Error while working with frame {}: {}", frameSelector, e.getMessage());
            switchTo().defaultContent(); // Safety measure to return to main document
            return null;
        }
    }
    
    /**
     * Switches back to the main document (out of all frames)
     */
    public static void switchToMainDocument() {
        log.debug("Switching to main document");
        switchTo().defaultContent();
    }
}
