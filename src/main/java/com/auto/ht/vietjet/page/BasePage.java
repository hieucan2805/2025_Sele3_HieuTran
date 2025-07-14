package com.auto.ht.vietjet.page;

import com.auto.ht.helpers.LanguageHelper;
import com.auto.ht.utils.Constants;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class BasePage {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(BasePage.class);

    private final String buttonAcceptCookie = "//div[@id='popup-dialog-description']//following-sibling::div//button";
    private final String buttonCloseAdsInfo = "//img[@alt='popup information']/parent::div/preceding-sibling::button";
    private final String buttonChangeLanguage = "//button//span//i";
    private final String inputSearchLanguage = "//input[@aria-label= 'search']";

    protected void waitForVisible(SelenideElement element) {
        element.shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Navigate to Homepage")
    public void openHomePage() {
        String URL = LanguageHelper.getLocalizedURL();
        open(URL);
        log.debug("Navigate to {}", URL);
        acceptCookie();

    }

    @Step("Wait And Accept Cookie")
    public void acceptCookie() {
        $x(buttonAcceptCookie).shouldBe(visible, Constants.SHORT_WAIT).click();
    }

    @Step("Wait And Cancel Ads")
    public void cancelAds() {
        $x(buttonCloseAdsInfo).shouldBe(visible, Constants.SHORT_WAIT).click();
        log.info("Close ads pop-up");
    }

    public void changeLanguage(String language) {
        $x(buttonChangeLanguage).shouldBe(visible, Constants.SHORT_WAIT).click();
        $x(inputSearchLanguage).shouldBe(visible, Constants.SHORT_WAIT).setValue(language);
        $x("//span[text()='" + language + "']").shouldBe(visible, Constants.SHORT_WAIT).click();
        log.info("Change language to {}", language);
    }

    // Check if element is in viewport
    public static boolean isElementInViewport(SelenideElement element) {
        return executeJavaScript(
                "var rect = arguments[0].getBoundingClientRect();" +
                        "return (" +
                        "rect.top >= 0 && rect.left >= 0 && " +
                        "rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) && " +
                        "rect.right <= (window.innerWidth || document.documentElement.clientWidth)" +
                        ");", element);
    }

    public void scrollToElement(SelenideElement element) {
        while (!isElementInViewport(element)) {
            element.shouldBe(visible, Constants.VERY_SHORT_WAIT).scrollIntoView(true);
        }
    }
}
