import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.AllureId;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class PlaywrightTasksTests extends BaseTest {

    @AllureId("001")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность нажатия на кнопку с динамическим 'id'")
    // Если из TestOps, то название метода было бы по типу 'public void tc_12345()'
    public void dynamicIdTest() {
        page.navigate("http://uitestingplayground.com/dynamicid");
        Locator dynamicIdButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Button with Dynamic ID"));
        dynamicIdButton.click();

        assertThat(dynamicIdButton).isVisible();
    }

    @AllureId("002")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность нажатия на кнопку, содержащую в локаторе только часть класса")
    public void classAttributeTest() {
        page.navigate("http://uitestingplayground.com/classattr");
        Locator blueButton = page.locator("xpath=//button[contains(@class, 'btn-primary')]");
        page.onceDialog(dialog -> {
            System.out.println("Сообщение во всплывающем окне: " + dialog.message());
            dialog.accept();
        });
        blueButton.click();

        assertThat(blueButton).isEnabled();
    }

    @AllureId("003")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Невозможность нажатия на кнопку, если она перекрыта другим элементом")
    @Description("Скриншот цвета перекрывающей кнопки хранится в папке 'screenshot/overlayButton.png'")
    public void hiddenLayersTest() {
        page.navigate("http://uitestingplayground.com/hiddenlayers");
        Locator greenButton = page.locator("button.btn-success");
        greenButton.click();
        try {
            greenButton.click(new Locator.ClickOptions().setTrial(true).setTimeout(1000));
            fail("Кнопка почему-то нажалась :(");
        } catch (TimeoutError e) {
            System.out.println(
                    "Кнопку нельзя нажать. Ура!\nСкрин перекрывающей кнопки в папке 'screenshot/overlayButton.png'");
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("screenshot/overlayButton.png")));
        }
        assertThat(page.locator("//button[@class='btn btn-primary']")).isVisible();
    }

    @AllureId("004")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность ожидания загрузки страницы")
    public void loadDelayTest() {
        page.navigate("http://uitestingplayground.com");
        page.getByText("Load Delay").click();
        Locator delayButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Button Appearing After Delay"));
        delayButton.click();

        assertThat(delayButton).isEnabled();
    }

    @AllureId("005")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность ожидания загрузки элемента на странице (AJAX)")
    public void ajaxDataTest() {
        page.navigate("http://uitestingplayground.com/ajax");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Button Triggering AJAX Request")).click();
        Locator requiredText = page.getByText("Data loaded with AJAX get request.");
        requiredText.click();

        assertThat(requiredText).isVisible();
    }

    @AllureId("006")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность ожидания загрузки элемента на странице со стороны клиента")
    public void clientSideDelayTest() {
        page.navigate("http://uitestingplayground.com/clientdelay");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Button Triggering Client Side Logic"))
                .click();
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
        page.navigate("http://uitestingplayground.com/click");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Button That Ignores DOM Click")).click();

        assertThat(page.locator("button[class='btn btn-success']")).isVisible();
    }

    @AllureId("008")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Сравнение введённого текста в поле с названием кнопки")
    public void textInputTest() {
        page.navigate("http://uitestingplayground.com/textinput");
        page.locator("input[class='form-control']").fill("testButton");
        page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Button That Should Change it's Name Based on Input Value")).click();

        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("testButton"))).hasText("testButton");
    }

    @AllureId("009")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность скролла до отображения элемента")
    public void scrollbarsTest() {
        page.navigate("http://uitestingplayground.com/scrollbars");
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
        page.navigate("http://uitestingplayground.com/dynamictable");
        List<String> allColumnHeaders = page.locator("span[role='columnheader']").allInnerTexts();
        int cpuIndex = allColumnHeaders.indexOf("CPU");
        Locator chromeRow = page.locator("div[role='row']").filter(new Locator.FilterOptions().setHasText("Chrome"));
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
        page.navigate("http://uitestingplayground.com/verifytext");

        assertThat(page.locator("//span[normalize-space(.)='Welcome UserName!']")).isVisible();
    }

    @AllureId("012")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность взаимодействия с элементом с меняющимся значением (полоса загрузки)")
    public void progressBarTest() {
        page.navigate("http://uitestingplayground.com/progressbar");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Start")).click();
        Locator progressBar = page.locator("#progressBar");
        assertThat(progressBar).hasText(
                Pattern.compile("(7[5-9]|[8-9][0-9])%"),
                new LocatorAssertions.HasTextOptions().setTimeout(30000));
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Stop")).click();

        System.out.println(
                "Разница в секундах между нажатием кнопки и значением '75%' = " + page.locator("#result").innerText());
    }

    @AllureId("013")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Отображение скрытых кнопок на странице")
    public void visibilityButtonsTest() {
        page.navigate("http://uitestingplayground.com/visibility");
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
         /*page.navigate("http://uitestingplayground.com/visibility");
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
        page.navigate("http://uitestingplayground.com/sampleapp");
        String userName = "testUser";
        page.locator("//input[@type='text']").fill(userName);
        page.locator("//input[@type='password']").fill("pwd");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log In")).click();

        assertThat(page.locator("#loginstatus")).containsText(userName);
    }

    @AllureId("015")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность нажатия на элемент при изменении этого элемента в DOM при наведении курсора")
    public void mouseOverTest() {
        page.navigate("http://uitestingplayground.com/mouseover");
        page.locator("xpath=//a[contains(text(), 'Click me')]").click();
        page.getByText("Link Button").click();

        assertThat(page.locator("//span[@id='clickCount']")).hasText("1");
        assertThat(page.locator("//span[@id='clickButtonCount']")).hasText("1");
    }

    @AllureId("016")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность поиска локатора со скрытыми символами")
    public void nonBreakingSpaceTest() {
        page.navigate("http://uitestingplayground.com/nbsp");
        Locator myButton = page.locator("xpath=//button[text()='My\u00A0Button']");
        myButton.click();
        /* Эти способы не прокатили:
        page.locator("xpath=//button[text()='My' + '&nbsp;' + 'Button']").click();
        page.GetByRoleOptions().setName("My&#160Button")).click();
        page.locator("xpath=//button[text()='My&nbsp;Button']").click();*/
        assertThat(myButton).isVisible();
    }

    @AllureId("017")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность взаимодействия с перекрытым элементом")
    public void overlappedElementTest() {
        page.navigate("http://uitestingplayground.com/overlapped");
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
        page.navigate("http://uitestingplayground.com/shadowdom");

        page.locator("#buttonGenerate").click();
        page.locator("#buttonCopy").click(); // Кнопка на сайте не работает, поэтому тест не точный :(

        String actualText = page.locator("#editField").inputValue();

        assertThat(page.locator("#editField")).hasValue(actualText); // Просто чтобы была проверка :)
    }

    @AllureId("019")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Взаимодействие со всплывающими окнами")
    public void alertsTest() {
        page.navigate("http://uitestingplayground.com/alerts");
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
        page.locator("#promptButton").click();
    }

    @AllureId("020")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность загрузки файла через кнопку")
    @Description("Загрузка файла через перетаскивание пока не возможна. Получится только через преобразование файла в массив байтов...")
    public void fileUploadTest() {
        page.navigate("http://uitestingplayground.com/upload");
        // Загрузка файла через кнопку
        page.locator("iframe").contentFrame().getByText("Browse files").setInputFiles(Paths.get("screenshot/overlayButton.png"));

        assertThat(page.locator("iframe").contentFrame().getByText("overlayButton.png")).isVisible();
    }

    @AllureId("021")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Взаимодействие с движущимся элементом")
    public void animatedButtonTest() {
        page.navigate("http://uitestingplayground.com/animation");
        page.locator("#animationButton").click();
        page.locator("//button[@class = 'btn btn-primary']").click();

        assertThat(page.locator("#opstatus")).hasText("Moving Target clicked. It's class name is 'btn btn-primary'");
    }

    @AllureId("022")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность ввода текста в поле после ожидания доступности")
    public void disabledInputTest() {
        page.navigate("http://uitestingplayground.com/disabledinput");
        page.locator("#enableButton").click();
        String actualText = "Hello!";
        page.locator("#inputField").fill(actualText);
        page.locator("#inputField").press("Enter");

        assertThat(page.locator("#opstatus")).containsText(actualText);
    }

    @AllureId("023")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность взаимодействия с чекбоксами, выпадающим списком")
    public void autoWaitTest() {
        page.navigate("http://uitestingplayground.com/autowait");
        Locator target = page.locator("#target");
        Locator applyThreeSecond = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Apply 3s"));
        Locator elementType = page.getByLabel("Choose an element type:");
        Locator opStatus = page.locator("#opstatus");

        // Чекбокс 'Visible' и элемент 'Button'
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Visible")).uncheck();
        applyThreeSecond.click();
        assertThat(target).isHidden();

        // Чекбокс 'Enabled' и элемент 'Textarea'
        elementType.selectOption("Textarea");
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Enabled")).uncheck();
        applyThreeSecond.click();
        target.fill("test");
        elementType.click();
        assertThat(opStatus).hasText("Text: test");

        // Чекбокс 'Editable' и элемент 'Input'
        elementType.selectOption("input");
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Editable")).uncheck();
        applyThreeSecond.click();
        target.fill("test");
        target.press("Enter");
        assertThat(opStatus).hasText("Text: test");

        // Чекбокс 'On Top' и элемент 'Select'
        elementType.selectOption("Select");
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("On Top")).uncheck();
        applyThreeSecond.click();
        target.selectOption("Item 2");
        assertThat(opStatus).hasText("Selected: Item 2");

        // Чекбокс 'Non Zero Size' и элемент 'Label'
        elementType.selectOption("Label");
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Non Zero Size")).uncheck();
        applyThreeSecond.click();
        assertThat(target).hasText("This is a Label");
    }

    @AllureId("024")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность работы с разными фреймами")
    public void framesTest() {
        page.navigate("http://uitestingplayground.com/frames");
        // Внутренний фрейм
        FrameLocator outerFrame = page.frameLocator("iframe[name='frame-outer']");
        Locator outerResult = outerFrame.locator("#result");
        // Кнопка "Edit"
        outerFrame.locator("[data-action='edit']").click();
        assertThat(outerResult).hasText("Button pressed: Edit");
        // Кнопка "Submit"
        outerFrame.getByText("Submit").click();
        assertThat(outerResult).hasText("Button pressed: Submit");
        // Кнопка "Click me"
        outerFrame.locator("button[name='my-button']").click();
        assertThat(outerResult).hasText("Button pressed: Click me");
        // Кнопка "Primary"
        outerFrame.locator("xpath=//button[@class='btn-class']").click();
        assertThat(outerResult).hasText("Button pressed: Primary");

        // Внешний фрейм
        FrameLocator innerFrame = outerFrame.frameLocator("iframe[name='frame-inner']");
        Locator innerResult = innerFrame.locator("#result");
        // Кнопка "Edit"
        innerFrame.locator("[data-action='edit']").click();
        assertThat(innerResult).hasText("Button pressed: Edit");
        // Кнопка "Submit"
        innerFrame.getByText("Submit").click();
        assertThat(innerResult).hasText("Button pressed: Submit");
        // Кнопка "Click me"
        innerFrame.locator("button[name='my-button']").click();
        assertThat(innerResult).hasText("Button pressed: Click me");
        // Кнопка "Primary"
        innerFrame.locator("xpath=//button[@class='btn-class']").click();
        assertThat(innerResult).hasText("Button pressed: Primary");
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

        page.navigate("http://uitestingplayground.com/geolocation");
        page.locator("#requestLocation").click();
        assertThat(page.locator("#location")).hasText("unavailable");

//        assertThat(page.locator("#lat")).hasText("55.788556");
//        assertThat(page.locator("#long")).hasText("37.359521");
    }
}