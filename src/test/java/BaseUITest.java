import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class BaseUITest {
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
        );
        context = browser.newContext();
        page = context.newPage();
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));
        PlaywrightAssertions.setDefaultAssertionTimeout(10000);
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        if (context != null) {
            Path tracePath = Paths.get("build/traces/trace-" + testInfo.getDisplayName() + ".zip");
            context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
            try {
                Allure.addAttachment("Playwright Trace", "application/zip",
                        Files.newInputStream(tracePath), ".zip");
            } catch (IOException e) {
                System.err.println("Трассировка не подцепилась к Allure: " + e.getMessage());
            }
            context.close();
        }
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    /**
     * Открыть страницу по URL
     */
    protected void open(String url) {
        page.navigate(url);
    }

    /**
     * Клик по кнопке через роль
     */
    protected void clickButton(String name) {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(name)).click();
    }

    /**
     * Универсальный клик
     */
    protected void click(String selector) {
        page.locator(selector).click();
    }

    /**
     * Заполнить поле текстом
     */
    protected void fill(String selector, String text) {
        page.locator(selector).fill(text);
    }

    /**
     * Выбрать в выпадающем списке
     */
    protected void select(String selector, String value) {
        page.locator(selector).selectOption(value);
    }

    /**
     * Состояния чекбокса
     */
    protected void setCheckbox(String label, boolean status) {
        if (status) {
            page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName(label)).check();
        } else {
            page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName(label)).uncheck();
        }
    }
}
