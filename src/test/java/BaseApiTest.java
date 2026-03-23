import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

public class BaseApiTest {
    protected Playwright playwright;
    protected APIRequestContext request;

    @BeforeEach
    void setupApi() {
        playwright = Playwright.create();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL("https://restful-booker.herokuapp.com")
                .setExtraHTTPHeaders(headers));
    }

    @AfterEach
    void tearDownApi() {
        if (request != null) request.dispose();
        if (playwright != null) playwright.close();
    }
}