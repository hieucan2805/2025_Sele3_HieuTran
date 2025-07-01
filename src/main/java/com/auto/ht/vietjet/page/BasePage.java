package com.auto.ht.vietjet.page;

import com.auto.ht.utils.Constants;
import com.auto.ht.helpers.LanguageHelper;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class BasePage {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(BasePage.class);

    private final SelenideElement buttonAcceptCookie = $x("//div[@id='popup-dialog-description']//following-sibling::div//button");
    private final SelenideElement buttonCancelAds = $x("//button[@id='NC_CTA_TWO']");
    private final SelenideElement imgAdsInfo = $x("//alt='popup information'");
    private final SelenideElement buttonCloseAdsInfo = $x("//img[@alt='popup information']/parent::div/preceding-sibling::button");

    protected void waitForVisible(SelenideElement element) {
        element.shouldBe(visible, Duration.ofSeconds(10));
    }

    @Step("Navigate to Homepage")
    public void openHomePage() {
        String URL = LanguageHelper.getLocalizedURL();
        open(URL);
        log.debug("Navigate to {}", URL);
        acceptCookie();
//        cancelAds();
    }

    @Step("Wait And Accept Cookie")
    public void acceptCookie() {
        buttonAcceptCookie.shouldBe(visible, Duration.ofSeconds(5)).click();
    }

    @Step("Wait And Cancel Ads")
    public void cancelAds() {
        buttonCloseAdsInfo.shouldBe(visible, Duration.ofSeconds(5)).click();
        log.info("Close ads pop-up");
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
