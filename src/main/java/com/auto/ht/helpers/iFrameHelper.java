package com.auto.ht.helpers;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class iFrameHelper {
    private static final Logger log = LoggerFactory.getLogger(iFrameHelper.class);

    // Common iframe selectors that might intercept clicks
    private static final String[] COMMON_FRAME_SELECTORS = {
            "iframe#preview-notification-frame",
            "iframe.st_preview_frame_banner",
            "iframe[title='smtiframetitle44']",
            "iframe.notification-frame",
            "iframe.ad-frame",
            "iframe[style*='position:fixed']"
    };

    // Common close button selectors
    private static final String[] CLOSE_BUTTON_SELECTORS = {
            "button.close-notification",
            ".close-button",
            ".close-icon",
            ".btn-close",
            "[aria-label='Close']",
            "[title='Close']",
            "button.st_close",
            ".st-close",
            ".x-button"
    };

    /**
     * Checks and handles any intercepting frames on the page
     */
    public static void handleInterceptingFrames() {
        log.debug("Checking for intercepting frames");
        try {
            // Check for common notification/ad iframes
            for (String frameSelector : COMMON_FRAME_SELECTORS) {
                handleFrame(frameSelector);
            }

            // Remove all fixed position iframes as a backup approach
            executeJavaScript(
                    "document.querySelectorAll('iframe[style*=\"position:fixed\"], iframe[style*=\"position: fixed\"]').forEach(el => el.remove());"
            );

            // Wait until all fixed-position iframes are gone
            $$(By.cssSelector("iframe[style*='position:fixed'], iframe[style*='position: fixed']"))
                    .shouldHave(CollectionCondition.size(0));
        } catch (Exception e) {
            log.warn("Error while handling intercepting frames: {}", e.getMessage());
        }
    }

    /**
     * Handles a specific frame
     * @param frameSelector CSS selector for the frame
     */
    private static void handleFrame(String frameSelector) {
        try {
            SelenideElement frame = $(frameSelector);
            if (frame.exists()) {
                log.info("Found potentially intercepting frame: {}", frameSelector);

                // Try to close frame using close button if available
                if (tryToClickCloseButton(frame)) {
                    return;
                }

                // If frame still exists, remove it using JavaScript
                if (frame.exists()) {
                    executeJavaScript("arguments[0].remove()", frame);
                    log.info("Removed intercepting frame with JavaScript: {}", frameSelector);
                }
            }
        } catch (Exception e) {
            log.debug("Error handling frame {}: {}", frameSelector, e.getMessage());
        }
    }

    /**
     * Try to find and click a close button within or near the frame
     * @param frame The frame element
     * @return true if close button was found and clicked
     */
    private static boolean tryToClickCloseButton(SelenideElement frame) {
        try {
            // Try to find close button in parent document
            for (String selector : CLOSE_BUTTON_SELECTORS) {
                SelenideElement closeButton = $(selector);
                if (closeButton.exists() && closeButton.isDisplayed()) {
                    // Use JavaScript click to avoid potential intercept issues
                    log.debug("Clicked close button: {}", selector);
                    executeJavaScript("arguments[0].click()", closeButton);
                    closeButton.shouldNot(Condition.visible);
                    return true;
                }
            }

            // Try to switch to frame and find close button inside
            try {
                switchTo().frame(frame);
                for (String selector : CLOSE_BUTTON_SELECTORS) {
                    SelenideElement closeButton = $(selector);
                    if (closeButton.exists() && closeButton.isDisplayed()) {
                        closeButton.click();
                        log.debug("Clicked close button inside frame: {}", selector);
                        closeButton.shouldNotBe(Condition.visible);
                        return true;
                    }
                }
            } finally {
                // Always switch back to default content
                switchTo().defaultContent();
            }
        } catch (Exception e) {
            log.debug("Error trying to find/click close button: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Safely clicks an element, handling any intercepting frames if necessary
     * @param element The element to click
     */
    public static void safeClick(SelenideElement element) {
        try {
            // First make sure element is visible and scrolled into view
            element.scrollIntoView(true).shouldBe(visible);
            element.click();
        } catch (Exception e) {
            log.debug("Regular click failed, trying alternate methods: {}", e.getMessage());

            // Handle any intercepting frames
            handleInterceptingFrames();

            // Try again after handling frames
            try {
                element.click();
            } catch (Exception e2) {
                // If still fails, try JavaScript click which can bypass some overlays
                log.debug("Second click attempt failed, using JavaScript click");
                executeJavaScript("arguments[0].click()", element);
            }
        }
    }

    /**
     * Switches to a frame, performs an action, then switches back to main content
     * @param frameElement The frame to switch to
     * @param action The action to perform while in the frame
     */
    public static void withinFrame(SelenideElement frameElement, Runnable action) {
        try {
            switchTo().frame(frameElement);
            action.run();
        } finally {
            switchTo().defaultContent();
        }
    }

    /**
     * Takes a screenshot of the current state for debugging purposes
     */
    public static void takeDebugScreenshot(String name) {
        try {
            screenshot("iframe_debug_" + name);
            log.info("Took debug screenshot: iframe_debug_{}", name);
        } catch (Exception e) {
            log.debug("Failed to take debug screenshot: {}", e.getMessage());
        }
    }
}
