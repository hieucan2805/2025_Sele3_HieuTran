package com.auto.ht.projects.vietjet.page;

import com.auto.ht.helpers.LanguageHelper;
import com.auto.ht.utils.Constants;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class BasePage {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(BasePage.class);
    private final String language = LanguageHelper.getLanguage();

    private final String buttonAcceptCookie = "//div[@id='popup-dialog-description']//following-sibling::div//button";
    private final String buttonCloseAdsInfo = "//img[@alt='popup information']/parent::div/preceding-sibling::button";
    private final String labelChangeLanguage = "//button//span//div";
    private final String inputSearchLanguage = "//input[@aria-label= 'search']";

    @Step("Navigate to Homepage")
    public void openHomePage() {
        open(Configuration.baseUrl);
        log.debug("Navigate to {}", Configuration.baseUrl);
        acceptCookie();

        changeLanguage(language);
    }

    @Step("Wait And Accept Cookie")
    public void acceptCookie() {
        $x(buttonAcceptCookie).shouldBe(visible, Constants.SHORT_WAIT).click();
    }

    @Step("Wait And Cancel Ads")
    public void cancelAds() {
        $x(buttonCloseAdsInfo).shouldBe(visible,Constants.MEDIUM_WAIT).click();
        log.info("Close ads pop-up");
        try {
            if ($x(buttonCloseAdsInfo).is(visible)) {
                $x(buttonCloseAdsInfo).click();
            }
        } catch (Exception e) {
            log.info("Ads pop-up did not appear or couldn't be closed: {}", e.getMessage());
        }
    }

    @Step("Change Language to {language}")
    public void changeLanguage(String language) {
        if (!$x(labelChangeLanguage).shouldBe(visible).getText().equalsIgnoreCase(language)) {
            $x(labelChangeLanguage).shouldBe(visible, Constants.SHORT_WAIT).click();
            $x(inputSearchLanguage).shouldBe(visible, Constants.SHORT_WAIT).setValue(language);
            $x("//span[text()='" + language + "']").shouldBe(visible, Constants.SHORT_WAIT).click();
            log.info("Change language to {}", language);
        }
    }

    // Check if element is in viewport
    public static boolean isElementInViewport(SelenideElement element) {
        Boolean visible = executeJavaScript(
                "var rect = arguments[0]?.getBoundingClientRect();" +
                        "if (!rect) return null;" +
                        "return (" +
                        "rect.top >= 0 && rect.left >= 0 && " +
                        "rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) && " +
                        "rect.right <= (window.innerWidth || document.documentElement.clientWidth)" +
                        ");", element);

        return Boolean.TRUE.equals(visible); // an toàn nếu visible là null
    }

    public void scrollToElement(SelenideElement element) {
        while (!isElementInViewport(element)) {
            element.shouldBe(visible, Constants.VERY_SHORT_WAIT).scrollIntoView(true);
        }
    }
}
