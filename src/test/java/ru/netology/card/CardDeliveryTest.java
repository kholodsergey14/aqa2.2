package ru.netology.card;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

public class CardDeliveryTest {
    final String cityCssSelector = "[data-test-id=city] input.input__control";
    final String dateCssSelector = "[data-test-id=date] .input__control";
    final String nameCssSelector = "[data-test-id=name] input.input__control";
    final String phoneCssSelector = "[data-test-id=phone] input.input__control";
    final String agreementCssSelector = "[data-test-id=agreement] span.checkbox__box";
    final String notificationCssSelector = "[data-test-id=notification] .notification__content";
    final String menuCssSelector = ".form-field iframe";
    final String calendarCssSelector = ".calendar";
    final String calendarMonthSelectCssSelector = ".calendar__arrow_direction_right[data-step='1']";
    final String calendarYearSelectCssSelector = ".calendar__arrow_direction_right[data-step='12']";
    final String calendarSelectDayCssSelector = "td.calendar__day";
    final String menuitemCSSSelector = ".menu-item .menu-item__control";
    final String buttonCssSelector = "button.button";
    final String dateFormatWithDots = "dd.MM.yyyy";
    final String dateFormatWithoutDots = "ddMMyyyy";
    final int timeToWaitMenu = 1000;
    final int timeToWaitCalendar = 1000;
    final int timeToWaitSending = 12000;
    final int daysToAdd = 35;

    @BeforeEach
    void setup() {
        open("http:\\localhost:9999");
    }

    @AfterEach
    void closeBrowser() {
        Selenide.close();
    }

    @Test
    @DisplayName("Should send form")
    void shouldSendForm() {
        $(cityCssSelector).setValue("Казань");
        $(dateCssSelector).sendKeys(Keys.LEFT_CONTROL + "a" + Keys.BACK_SPACE);
        $(dateCssSelector).setValue(getDate(dateFormatWithoutDots, daysToAdd));
        $(nameCssSelector).setValue("Вася Пупкин");
        $(phoneCssSelector).setValue("+79191234567");
        $(agreementCssSelector).click();
        $(buttonCssSelector).click();
        $(notificationCssSelector).waitUntil(visible, timeToWaitSending);
        String expected = "Встреча успешно забронирована на " + getDate(dateFormatWithDots, daysToAdd);
        $(notificationCssSelector).shouldHave(Condition.exactText(expected));
    }

    @Test
    @DisplayName("Should send form, make choices of date and city with clicks")
    void shouldSendFormWithChoices() {
        $(cityCssSelector).setValue("Ка");
        $(menuCssSelector).waitUntil(exist, timeToWaitMenu);
        $$(menuitemCSSSelector).find(exactText("Казань")).click();
        String selectedDateString = $(dateCssSelector).getValue();
        $(dateCssSelector).sendKeys(Keys.LEFT_CONTROL + "a" + Keys.BACK_SPACE);
        $(calendarCssSelector).waitUntil(visible, timeToWaitCalendar);
        int i = 0;
        int numberOfClicks = getDifferentOfYears(selectedDateString, daysToAdd);
        while (numberOfClicks > i) {
            $(calendarYearSelectCssSelector).click();
            i++;
        }
        i = 0;
        numberOfClicks = getDifferentOfMonthes(selectedDateString, daysToAdd);
        while (numberOfClicks > i) {
            $(calendarMonthSelectCssSelector).click();
            i++;
        }
        $$(calendarSelectDayCssSelector).find(exactText(getDay(daysToAdd))).click();
        $(nameCssSelector).setValue("Вася Пупкин");
        $(phoneCssSelector).setValue("+79191234567");
        $(agreementCssSelector).click();
        $(buttonCssSelector).click();
        $(notificationCssSelector).waitUntil(visible, timeToWaitSending);
        String expected = "Встреча успешно забронирована на " + getDate(dateFormatWithDots, daysToAdd);
        $(notificationCssSelector).shouldHave(Condition.exactText(expected));
    }

    String getDate(String dateFormat, int daysToAdd) {
        LocalDate endDate = LocalDate.now().plusDays(daysToAdd);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
        return endDate.format(formatter);
    }

    int getDifferentOfMonthes(String selectedDateString, int daysToAdd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate selectedDate = LocalDate.parse(selectedDateString, formatter);
        LocalDate endDate = LocalDate.now().plusDays(daysToAdd);
        return endDate.getMonthValue() - selectedDate.getMonthValue();
    }

    int getDifferentOfYears(String selectedDateString, int daysToAdd) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate selectedDate = LocalDate.parse(selectedDateString, formatter);
        LocalDate endDate = LocalDate.now().plusDays(daysToAdd);
        return endDate.getYear() - selectedDate.getYear();
    }

    String getDay(int daysToAdd) {
        LocalDate endDate = LocalDate.now().plusDays(daysToAdd);
        return Integer.toString(endDate.getDayOfMonth());
    }
}