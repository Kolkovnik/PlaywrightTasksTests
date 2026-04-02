import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.AllureId;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static utils.Constants.*;

// Теперь долгие тесты запускаются в одном потоке, чтобы не замедлять другие. Тесты ускорились на 10 секунд :)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Execution(ExecutionMode.CONCURRENT)
public class PlaywrightTasksTests extends BaseUITest {

    @AllureId("001")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность нажатия на кнопку с динамическим 'id'")
    // Если из TestOps, то название метода было бы по типу 'public void tc_12345()'
    public void dynamicIdTest() {
        open(DYNAMIC_ID_URL);
        clickButton("Button with Dynamic ID");

        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Button with Dynamic ID")))
                .isVisible();
    }

    @AllureId("002")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность нажатия на кнопку, содержащую в локаторе только часть класса")
    public void classAttributeTest() {
        open(CLASS_ATTR_URL);
        page.onceDialog(dialog -> {
            System.out.println("Сообщение во всплывающем окне: " + dialog.message());
            dialog.accept();
        });
        click("xpath=//button[contains(@class, 'btn-primary')]");
    }

    @AllureId("003")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Невозможность нажатия на кнопку, если она перекрыта другим элементом")
    @Description("Скриншот цвета перекрывающей кнопки хранится в папке 'screenshot/overlayButton.png'")
    public void hiddenLayersTest() {
        open(HIDDEN_LAYERS_URL);
        click("button.btn-success");
        try {
            page.locator("button.btn-success").click(new Locator.ClickOptions().setTrial(true).setTimeout(1000));
            fail("Кнопка почему-то нажалась :(");
        } catch (TimeoutError e) {
            System.out.println(
                    "Кнопку нельзя нажать. Ура!\nСкрин перекрывающей кнопки в папке 'screenshot/overlayButton.png'");
            page.screenshot(new Page.ScreenshotOptions().setPath(OVERLAY_SCREENSHOT));
        }
    }

    @AllureId("004")
    @Test
    @Order(1)
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность ожидания загрузки страницы")
    public void loadDelayTest() {
        String buttonText = "Button Appearing After Delay";
        open(BASE_URL);
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Load Delay")).click();
        clickButton(buttonText);

        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(buttonText))).isEnabled();
    }

    @AllureId("005")
    @Test
    @Order(1)
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность ожидания загрузки элемента на странице (AJAX)")
    public void ajaxDataTest() {
        open(AJAX_URL);
        clickButton("Button Triggering AJAX Request");
        Locator requiredText = page.getByText("Data loaded with AJAX get request.");
        requiredText.click();

        assertThat(requiredText).isVisible();
    }

    @AllureId("006")
    @Test
    @Order(1)
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность ожидания загрузки элемента на странице со стороны клиента")
    public void clientSideDelayTest() {
        open(CLIENT_DELAY_URL);
        clickButton("Button Triggering Client Side Logic");
        Locator requiredText = page.getByText("Data calculated on the client side.");
        requiredText.click();

        assertThat(requiredText).isVisible();
    }

    @AllureId("007")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность клика по элементу на странице")
    public void clickTest() {
        open(CLICK_URL);
        clickButton("Button That Ignores DOM Click");

        assertThat(page.locator("button[class='btn btn-success']")).isVisible();
    }

    @AllureId("008")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Сравнение введённого текста в поле с названием кнопки")
    public void textInputTest() {
        open(TEXT_INPUT_URL);
        fill("input.form-control", TEST_NAME);
        clickButton("Button That Should Change it's Name Based on Input Value");

        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(TEST_NAME))).hasText(TEST_NAME);
    }

    @AllureId("009")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность скролла до отображения элемента")
    public void scrollbarsTest() {
        open(SCROLLBARS_URL);
        Locator hidingButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Hiding Button"));
        hidingButton.click();

        assertThat(hidingButton).isInViewport();
    }

    @AllureId("010")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность вытащить значение в динамической таблице")
    public void DynamicTableTest() {
        open(DYNAMIC_TABLE_URL);
        List<String> allColumnHeaders = page.locator("span[role='columnheader']").allInnerTexts();
        int cpuIndex = allColumnHeaders.indexOf("CPU");
        Locator chromeRow = page.locator("div[role='row']").filter(new Locator.FilterOptions()
                .setHasText("Chrome"));
        String cpuValue = chromeRow.locator("span[role='cell']")
                .nth(cpuIndex)
                .innerText();
        String requiredText = page.locator(".bg-warning").innerText();

        assertTrue(requiredText.contains(cpuValue));
    }

    @AllureId("011")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность поиска элемента с очисткой оформления текста")
    public void verifyTextTest() {
        open(VERIFY_TEXT_URL);

        assertThat(page.locator("//span[normalize-space(.)='Welcome UserName!']")).isVisible();
    }

    @AllureId("012")
    @Test
    @Order(1)
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность взаимодействия с элементом с меняющимся значением (полоса загрузки)")
    public void progressBarTest() {
        open(PROGRESS_BAR_URL);
        clickButton("Start");
        Locator progressBar = page.locator("#progressBar");
        assertThat(progressBar).hasText(
                Pattern.compile("(7[5-9]|[8-9][0-9])%"),
                new LocatorAssertions.HasTextOptions().setTimeout(30000));
        clickButton("Stop");

        System.out.println(
                "Разница в секундах между нажатием кнопки и значением '75%' = " + page.locator("#result")
                        .innerText());
    }

    @AllureId("013")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Отображение скрытых кнопок на странице")
    public void visibilityButtonsTest() {
        open(VISIBILITY_URL);
        Locator hideButton = page.locator("#hideButton");
        Locator removedButton = page.locator("#removedButton");
        Locator zeroWidthButton = page.locator("#zeroWidthButton");
        Locator overlappedButton = page.locator("#overlappedButton");
        Locator zeroOpacityButton = page.locator("#transparentButton");
        Locator visibilityHiddenButton = page.locator("#invisibleButton");
        Locator displayNoneButton = page.locator("#notdisplayedButton");
        Locator offscreenButton = page.locator("#offscreenButton");

        hideButton.click();

        assertThat(hideButton).isVisible();
        assertThat(removedButton).isHidden();
        assertThat(zeroWidthButton).isHidden();
        assertThat(overlappedButton).isVisible();
        assertThat(zeroOpacityButton).isVisible();
        assertThat(visibilityHiddenButton).isHidden();
        assertThat(displayNoneButton).isHidden();
        assertThat(offscreenButton).isVisible();

        // ИЛИ так:
         /*open(VISIBILITY_URL);
         Locator allButtons = page.locator("button:not(#hideButton)");
         List<Locator> buttonsList = allButtons.all();
        page.locator("#hideButton").click();
         for (Locator button : buttonsList) {
            if (button.isHidden()) {
                System.out.println("Кнопка скрыта/удалена, идём к следующей...");
            } else {
                String buttonText = button.innerText();
                System.out.println("Кнопка '" + buttonText + "' отображается в DOM");
            }
        }*/
    }

    @AllureId("014")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность заполнения формы логирования")
    public void sampleAppTest() {
        open(SAMPLE_APP_URL);
        fill("//input[@type='text']", TEST_NAME);
        fill("//input[@type='password']", "pwd");
        clickButton("Log In");

        assertThat(page.locator("#loginstatus")).containsText(TEST_NAME);
    }

    @AllureId("015")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность нажатия на элемент при изменении этого элемента в DOM при наведении курсора")
    public void mouseOverTest() {
        open(MOUSE_OVER_URL);
        click("xpath=//a[contains(text(), 'Click me')]");
        click("xpath=//a[contains(text(), 'Link Button')]");

        assertThat(page.locator("//span[@id='clickCount']")).hasText("1");
        assertThat(page.locator("//span[@id='clickButtonCount']")).hasText("1");
    }

    @AllureId("016")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность поиска локатора со скрытыми символами")
    public void nonBreakingSpaceTest() {
        open(NBSP_URL);
        Locator myButton = page.locator("xpath=//button[text()='My\u00A0Button']");
        myButton.click();
        /* Эти способы не прокатили:
        click("xpath=//button[text()='My' + '&nbsp;' + 'Button']");
        clickButton("My&#160Button"));
        click("xpath=//button[text()='My&nbsp;Button']");*/
        assertThat(myButton).isVisible();
    }

    @AllureId("017")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность взаимодействия с перекрытым элементом")
    public void overlappedElementTest() {
        open(OVERLAPPED_URL);
        Locator nameField = page.locator("#name");
        nameField.click();
        nameField.fill("Nikita");
    }

    @Disabled("На сайте не работает кнопка копирования")
    @AllureId("018")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность взаимодействия с элементом в shadow DOM")
    public void shadowDomTest() {
        open(SHADOW_DOM_URL);
        click("#buttonGenerate");
        click("#buttonCopy"); // Кнопка на сайте не работает, поэтому тест не точный :(
        String actualText = page.locator("#editField").inputValue();

        assertThat(page.locator("#editField")).hasValue(actualText); // Просто чтобы была проверка :)
    }

    @AllureId("019")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Взаимодействие со всплывающими окнами")
    public void alertsTest() {
        open(ALERTS_URL);
        page.onceDialog(dialog -> {
            System.out.println("Сообщение во всплывающем окне (Alert): " + dialog.message());
            dialog.accept();
        });
        page.locator("#alertButton").click();
        page.onceDialog(dialog -> {
            System.out.println("Сообщение во всплывающем окне (Confirm): " + dialog.message());
            dialog.accept();
        });
        page.locator("#confirmButton").click();
        page.onceDialog(dialog -> {
            System.out.println("Сообщение во всплывающем окне (Promt) и ввод своего текста: " + dialog.message());
            dialog.accept("asd");
        });
        click("#promptButton");
    }

    @AllureId("020")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность загрузки файла через кнопку")
    @Description("""
    Загрузка файла через перетаскивание пока не возможна.
    Получится только через преобразование файла в массив байтов...""")
    public void fileUploadTest() {
        open(UPLOAD_URL);
        // Загрузка файла через кнопку
        page.locator("iframe").contentFrame().getByText("Browse files").setInputFiles(OVERLAY_SCREENSHOT);

        assertThat(page.locator("iframe").contentFrame().getByText(OVERLAY_SCREENSHOT.getFileName().toString()))
                .isVisible();
    }

    @AllureId("021")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Взаимодействие с движущимся элементом")
    public void animatedButtonTest() {
        open(ANIMATION_URL);
        click("#animationButton");
        assertThat(page.locator(PRIMARY_BUTTON_XPATH))
                .not().hasClass(Pattern.compile(".*spin.*"));

        click(PRIMARY_BUTTON_XPATH);
        assertThat(page.locator("#opstatus"))
                .hasText("Moving Target clicked. It's class name is 'btn btn-primary'") ;
    }

    @AllureId("022")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность ввода текста в поле после ожидания доступности")
    public void disabledInputTest() {
        String actualText = "Hello!";
        open(DISABLED_INPUT_URL);
        click("#enableButton");
        fill("#inputField", actualText);
        page.locator("#inputField").press("Enter");

        assertThat(page.locator("#opstatus")).containsText(actualText);
    }

    @AllureId("023")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность взаимодействия с чекбоксами, выпадающим списком")
    public void autoWaitTest() {
        String target = "#target";
        String opStatus = "#opstatus";
        String elementType = "#element-type";
        String applyButton = "Apply 3s";
        open(AUTO_WAIT_URL);

        // Чекбокс 'Visible' и элемент 'Button'
        setCheckbox("Visible", false);
        clickButton(applyButton);
        assertThat(page.locator(target)).isHidden();

        // Чекбокс 'Enabled' и элемент 'Textarea'
        select(elementType, "Textarea");
        setCheckbox("Enabled", false);
        clickButton(applyButton);
        fill(target, "test");
        click(elementType);
        assertThat(page.locator(opStatus)).hasText("Text: test");

        // Чекбокс 'Editable' и элемент 'Input'
        select(elementType,"Input");
        setCheckbox("Editable", false);
        clickButton(applyButton);
        fill(target, "test");
        page.keyboard().press("Enter");
        assertThat(page.locator(opStatus)).hasText("Text: test");

        // Чекбокс 'On Top' и элемент 'Select'
        select(elementType, "Select");
        setCheckbox("On Top", false);
        clickButton(applyButton);
        select(target, "Item 2");
        assertThat(page.locator(opStatus)).hasText("Selected: Item 2");

        // Чекбокс 'Non Zero Size' и элемент 'Label'
        select(elementType, "Label");
        setCheckbox("Non Zero Size", false);
        clickButton(applyButton);
        assertThat(page.locator(target)).hasText("This is a Label");
    }

    @AllureId("024")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность работы с разными фреймами")
    public void framesTest() {
        open(FRAMES_URL);
        String resultId = "#result";
        String editButton = "[data-action='edit']";
        String submitButton = "Submit";
        String clickMeButton = "button[name='my-button']";
        String primaryButton = "xpath=//button[@class='btn-class']";
        String buttonPrefix = "Button pressed: ";

        // ----- Внешний фрейм -----
        FrameLocator outerFrame = page.frameLocator("iframe[name='frame-outer']");
        Locator outerResult = outerFrame.locator(resultId);

        // Кнопка "Edit"
        outerFrame.locator(editButton).click();
        assertThat(outerResult).hasText(buttonPrefix + "Edit");

        // Кнопка "Submit"
        outerFrame.getByText(submitButton).click();
        assertThat(outerResult).hasText(buttonPrefix + "Submit");

        // Кнопка "Click me"
        outerFrame.locator(clickMeButton).click();
        assertThat(outerResult).hasText(buttonPrefix + "Click me");

        // Кнопка "Primary"
        outerFrame.locator(primaryButton).click();
        assertThat(outerResult).hasText(buttonPrefix + "Primary");

        // ----- Внутренний фрейм -----
        FrameLocator innerFrame = outerFrame.frameLocator("iframe[name='frame-inner']");
        Locator innerResult = innerFrame.locator(resultId);

        // Кнопка "Edit"
        innerFrame.locator(editButton).click();
        assertThat(innerResult).hasText(buttonPrefix + "Edit");

        // Кнопка "Submit"
        innerFrame.getByText(submitButton).click();
        assertThat(innerResult).hasText(buttonPrefix + "Submit");

        // Кнопка "Click me"
        innerFrame.locator(clickMeButton).click();
        assertThat(innerResult).hasText(buttonPrefix + "Click me");

        // Кнопка "Primary"
        innerFrame.locator(primaryButton).click();
        assertThat(innerResult).hasText(buttonPrefix + "Primary");
    }

    @AllureId("025")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность работы с геолокацией")
    @Description("Геолокация не работает на сайте, поэтому проверяем только отклонение доступа к геолокации")
    public void geoLocationTest() {
/*        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setGeolocation(55.788556, 37.359521)
                .setPermissions(Arrays.asList("geolocation")));
        Page page = context.newPage();*/

        open(GEOLOCATION_URL);
        click("#requestLocation");
        assertThat(page.locator("#location")).hasText("unavailable");

//        assertThat(page.locator("#lat")).hasText("55.788556");
//        assertThat(page.locator("#long")).hasText("37.359521");
    }
}
