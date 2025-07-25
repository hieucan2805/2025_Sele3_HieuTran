package com.auto.ht.projects.vietjet.page;

import com.auto.ht.helpers.LanguageHelper;
import com.auto.ht.helpers.LocatorHelper;
import com.auto.ht.helpers.iFrameHelper;
import com.auto.ht.utils.Constants;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class SelectFlightPage extends BasePage {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SelectFlightPage.class);
    private final String language = LanguageHelper.getLanguage();

    @Getter
    private final LocatorHelper localeBundle = new LocatorHelper(SelectFlightPage.class.getSimpleName());

    //Locator
    private final String listPriceOfFlights = "//div/p[text()='000 VND']/preceding-sibling::p";
    private final String labelPriceOfFlights = "//div/p[text()='000 VND']/preceding-sibling::p[text()='%s']";
    private final String labelVJAAtTheBottomPage = "//div/p[text()='VJ - Vietjet Air']";
    private final String labelFlightPrice = "//div/p[text()='%s']//following-sibling::div/h4";
    private final String buttonContinue = "//button//span[text()='%s']";
    private final String labelTypeAndPassenger = "//img[@src='/static/media/departure-icon.25d3557e.svg']//parent::div//preceding-sibling::p";
    private final String loadingTicketIcon = "//div[@id='progress']";
    private final String labelCurrentMonthOfFlight = "//p[text()='%s']//parent::div//following-sibling::div//div[@class='slick-slide slick-active slick-center slick-current']//p[@weight='Bold']";
    private final String listLPriceOfFlightIn = "//p[text()='%s']//parent::div//following-sibling::div//div[contains(@class,'slick-slide slick-active')]//span[not(contains(text(),'000'))]";
    private final String butonNextMonth = "//p[text()='%s']//parent::div//following-sibling::div//div[@class='slick-list']//following-sibling::button";
    private final String buttonPreviousMonth = "//button[@aria-label='Next month']";

    //Action
    protected ElementsCollection getAllElementsCollectionFlightPrices() {
        scrollToBottomPage();
        return $$x(listPriceOfFlights);
    }

    /**
     * Find and return all flight prices as a list of strings.
     *
     * @return List of prices as strings
     */
    public List<String> getAllFlightPrices() {
        return getAllElementsCollectionFlightPrices().stream().map(SelenideElement::getText).collect(Collectors.toList());
    }

    /**
     * Get flight prices as integers after removing non-numeric characters.
     *
     * @return List of prices as integers
     */
    public List<Integer> getAllFlightPricesAsNumbers() {
        return getAllFlightPrices().stream().map(price -> price.replaceAll("[^\\d]", "")) // Remove non-numeric chars
                .map(Integer::parseInt).collect(Collectors.toList());
    }

    @Step("Wait until loading icon disappears")
    public void waitLoadingIconDisappear() {
        try {
            // First attempt to wait for loading icon to disappear
            $x(loadingTicketIcon).shouldNotBe(visible, Constants.MEDIUM_WAIT);
            log.info("Loading icon has disappeared.");
        } catch (Exception e) {
            log.info("Loading icon still visible. Refreshing page once...");
            refresh();
            $x(loadingTicketIcon).shouldNotBe(visible, Constants.MEDIUM_WAIT);

        }
    }

    @Step("Scroll to the bottom of the page to load all flight prices")
    public void scrollToBottomPage() {
        scrollToElement($x(labelVJAAtTheBottomPage));
    }

    public void selectLowestPriceTicket() {
        try {
            waitLoadingIconDisappear();
            // Instead of using the old handleInterceptingFrames method,
            // we directly interact with elements in the main document
            // as we no longer try to generically handle frames

            ElementsCollection priceElements = getAllElementsCollectionFlightPrices();
            int minPrice = Integer.MAX_VALUE;
            SelenideElement cheapestElement = null;

            for (SelenideElement priceElement : priceElements) {
                priceElement.scrollIntoView(true).shouldBe(visible);
                String priceText = priceElement.getText().replaceAll("[^\\d]", "");
                if (!priceText.isEmpty()) {
                    int price = Integer.parseInt(priceText);
                    if (price < minPrice) {
                        minPrice = price;
                        cheapestElement = priceElement;
                    }
                }
            }

            if (cheapestElement != null) {
                // Use direct click instead of safeClick
                cheapestElement.click();
                log.info("Clicked on the lowest price: {}", minPrice);
            } else {
                throw new IllegalStateException("No valid prices found to click.");
            }
        } catch (Exception e) {
            log.error("Failed to select lowest price ticket: {}", e.getMessage());
            throw e;
        }
    }

    @Step("Select the cheapest ticket for departure flight")
    public void selectCheapestTicketForDepartureFlight() {
        selectLowestPriceTicket();
    }

    @Step("Select the cheapest ticket for return flight")
    public void selectCheapestTicketForReturnFlight() {
        selectLowestPriceTicket();
    }

    public String getDepartureFlightPrice() {
        String newXpath = localeBundle.updateLocatorWithDynamicText(labelFlightPrice, "text.DepartureFlight");
        return $x(newXpath).shouldBe(visible, Constants.SHORT_WAIT).getText();
    }

    public String getReturnFlightPrice() {
        String newXpath = localeBundle.updateLocatorWithDynamicText(labelFlightPrice, "text.ReturnFlight");
        return $x(newXpath).shouldBe(visible, Constants.SHORT_WAIT).getText();
    }

    public void clickContinueButton() {
        String newXpath = localeBundle.updateLocatorWithDynamicText(buttonContinue, "button.Continue");
        $x(newXpath).shouldBe(visible, Constants.SHORT_WAIT).click();
    }

    public void chooseCheapestTicketAndContinue() {
        cancelAds();
        selectCheapestTicketForDepartureFlight();
        clickContinueButton();

        if (getTypeOfFlightText().equalsIgnoreCase(localeBundle.getLocalizedText("text.ReturnFlight"))) {
            selectCheapestTicketForReturnFlight();
            clickContinueButton();
        }
    }


    /**
     * Search for a specific price.
     *
     * @param targetPrice Price to search for
     * @return true if price is found, otherwise false
     */
    public boolean searchPrice(int targetPrice) {
        List<Integer> prices = getAllFlightPricesAsNumbers();
        return prices.contains(targetPrice);
    }

    /**
     * Search for a specific price.
     *
     * @param targetPrice Price to search for
     * @return true if price is found, otherwise false
     */
    public int getIndexOfPrice(int targetPrice) {
        List<Integer> prices = getAllFlightPricesAsNumbers();
        return prices.indexOf(targetPrice);
    }

    /**
     * Count for a specific price.
     *
     * @return total index of list
     */
    public int countAmountPriceInList() {
        List<Integer> prices = getAllFlightPricesAsNumbers();
        return prices.size();
    }

    /**
     * Find the lowest flight price.
     *
     * @return Minimum price found
     */
    public int getLowestPrice() {
        return getAllFlightPricesAsNumbers().stream().min(Integer::compareTo).orElseThrow(() -> new IllegalStateException("No prices found"));
    }

    /**
     * Find the highest flight price.
     *
     * @return Maximum price found
     */
    public int getHighestPrice() {
        return getAllFlightPricesAsNumbers().stream().max(Integer::compareTo).orElseThrow(() -> new IllegalStateException("No prices found"));
    }

    public String getTypeOfFlightText() {
        String tmp_text = $x(labelTypeAndPassenger).shouldBe(visible, Constants.SHORT_WAIT).getText();
        return tmp_text.split("\\|")[0].trim();
    }

    public String getCurrentMonthOfFlight() {
        String newXpath = localeBundle.updateLocatorWithDynamicText(labelCurrentMonthOfFlight, "text.DepartureFlight");
        return $x(newXpath).shouldBe(visible, Constants.SHORT_WAIT).getText();
    }

    public void moveToMonthOfFlight(String month) {
        String nextButtonXpath = localeBundle.updateLocatorWithDynamicText(butonNextMonth, month);

        while (!getCurrentMonthOfFlight().equalsIgnoreCase(month)) {
            if (getCurrentMonthOfFlight().compareTo(month) > 0) {
                $x(buttonPreviousMonth).shouldBe(visible, Constants.SHORT_WAIT).click();
            } else {
                $x(nextButtonXpath).shouldBe(visible, Constants.SHORT_WAIT).click();
            }
            if (getCurrentMonthOfFlight().equalsIgnoreCase(month)) {
                log.info("Already on the month: {}", month);
            }
        }
    }

    public List<String> getFlightPricesInCurrentMonth() {
        String newXpath = localeBundle.updateLocatorWithDynamicText(listLPriceOfFlightIn, "text.DepartureFlight");
        return $$x(newXpath).filter(visible).stream().map(SelenideElement::getText).collect(Collectors.toList());
    }

}
