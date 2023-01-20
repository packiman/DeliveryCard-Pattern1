package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;
import java.util.Locale;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static ru.netology.delivery.data.DataGenerator.generateDate;

public class DeliveryTest {
    String planDate = generateDate(3);
    private Faker faker;


    @BeforeEach
    void setup() {
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999");
        faker = new Faker(new Locale("ru"));
    }

    @Test
    void shouldTestSuccessfulFormSubmission() {
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(generateDate(3));
        $("[data-test-id=city] input").setValue(DataGenerator.generateCity("ru"));
        $("[data-test-id=name] input").setValue(DataGenerator.generateName("ru"));
        $("[data-test-id=phone] input").setValue(DataGenerator.generatePhone("ru"));
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        $("[data-test-id=success-notification]").should(appear, Duration.ofSeconds(15));
        $x("//*[contains(text(), 'Встреча успешно запланирована')]").shouldHave(Condition.text("Встреча успешно запланирована на " + planDate), Duration.ofSeconds(15)).shouldBe(visible);
    }


    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = generateDate(daysToAddForSecondMeeting);


        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(firstMeetingDate);
        $("[data-test-id=city] input").setValue(validUser.getCity());
        $("[data-test-id=name] input").setValue(validUser.getName());
        $("[data-test-id=phone] input").setValue(validUser.getPhone());
        $("[data-test-id=agreement] span").click();
        $x("//*[@class='button__content']").click();
        $x("//*[contains(text(), 'Встреча успешно запланирована')]").shouldHave(Condition.text("Встреча успешно запланирована на " + firstMeetingDate));
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(secondMeetingDate);
        $x("//*[@class='button__content']").click();
        $x("//*[contains(text(), 'У вас уже запланирована встреча на другую дату. Перепланировать?')]").shouldBe(visible);
        $x("//span[contains(text(), 'Перепланировать')]").click();
        $x("//*[contains(text(), 'Встреча успешно запланирована')]").shouldHave(Condition.text("Встреча успешно запланирована на " + secondMeetingDate));
    }
}
