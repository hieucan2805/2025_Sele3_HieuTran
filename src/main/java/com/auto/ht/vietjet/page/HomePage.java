package com.auto.ht.vietjet.page;

import com.auto.ht.utils.*;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

public class HomePage extends BasePage {

    @Getter
    private final LocatorHelper localeBundle = new LocatorHelper(HomePage.class.getSimpleName());

    private final String radioReturnFlight = "//img[@src='/static/media/switch.d8860013.svg']/parent::div/preceding-sibling::div//input[@type='radio'and@value='roundTrip']";
    private final String radioOneWayFlight = "//img[@src='/static/media/switch.d8860013.svg']/parent::div/preceding-sibling::div//input[@type='radio'and@value='oneway']";


    @Step("Select the One Way Flight")
    public void selectOneWayFlight() {
        $x(radioOneWayFlight).shouldBe(visible, Constants.VERY_SHORT_WAIT).click();
    }

    @Step("Select the Return Flight")
    public void selectReturnFlight() {
        $x(radioReturnFlight).shouldBe(visible, Constants.VERY_SHORT_WAIT).click();
    }

}
