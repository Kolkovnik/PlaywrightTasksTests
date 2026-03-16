import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.AllureId;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class PlaywrightTasksTests extends BaseTest {

    @AllureId("001")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность нажатия на кнопку с динамическим 'id'")
    // Если из TestOps, то название метода было по типу 'public void tc_12345()'
    public void dynamicIdTest() {
        page.navigate("http://uitestingplayground.com/dynamicid");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Button with Dynamic ID")).click();
        // ИЛИ
        // page.locator("xpath=//button[@class='btn btn-primary']").click();
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
            System.out.println("Кнопку нельзя нажать. Ура!\nСкрин перекрывающей кнопки в папке 'screenshot/overlayButton.png'");
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("screenshot/overlayButton.png")));
        }
    }

    @AllureId("004")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность ожидания загрузки страницы")
    public void loadDelayTest() {
        page.navigate("http://uitestingplayground.com");
        page.getByText("Load Delay").click();
        Locator delayButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Button Appearing After Delay"));

        assertThat(delayButton).isEnabled();
        delayButton.click();
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
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Button Triggering Client Side Logic")).click();
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
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Button That Should Change it's Name Based on Input Value")).click();

        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("testButton"))).isVisible();
    }

    @AllureId("009")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Возможность скролла до отображение элемента")
    public void scrollbarsTest() {
        page.navigate("http://uitestingplayground.com/scrollbars");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Hiding Button")).click();
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
    @DisplayName("Возможность взаимодействия с элементом с меняющимся значением")
    public void progressBarTest() {
        page.navigate("http://uitestingplayground.com/progressbar");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Start")).click();
        Locator progressBar = page.locator("#progressBar");
        assertThat(progressBar).hasText(
                Pattern.compile("(7[5-9]|[8-9][0-9])%"),
                new LocatorAssertions.HasTextOptions().setTimeout(30000)
        );
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Stop")).click();

        System.out.println("Разница в секундах между нажатием кнопки и значением '75%' = " + page.locator("#result").innerText());
    }

    @AllureId("013")
    @Test
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Отображение скрытых кнопок на странице")
    public void visibilityButtonsTest() {
        page.navigate("http://uitestingplayground.com/visibility");
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
        }
    }
}