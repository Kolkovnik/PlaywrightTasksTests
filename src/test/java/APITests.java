import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import io.qameta.allure.AllureId;
import io.qameta.allure.Owner;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class APITests extends BaseApiTest {
    private int savedBookingId;
    private String savedToken;

    // Only positive проверки :)

    @AllureId("026")
    @Test
    @Order(1)
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Создание токена авторизации на ручку 'POST /auth'")
    public void createTokenTest() {
        String body = """
                {
                    "username": "admin",
                    "password": "password123"
                }
                """;

        APIResponse createTokenResponse = request.post("/auth", RequestOptions.create().setData(body));
//        assertTrue(createToken.ok());
        // Так надёжнее, если нужный конкретный код (200), а не просто "ok"
        assertEquals(200, createTokenResponse.status());

        JsonObject jsonResponse = JsonParser.parseString(createTokenResponse.text()).getAsJsonObject();
        savedToken = jsonResponse.get("token").getAsString();

        assertNotNull(savedToken);
        assertFalse(savedToken.isEmpty());
    }

    @AllureId("027")
    @Test
    @Order(2)
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Создание книги на ручку 'POST /booking'")
    public void createNewBookTest() {
        String body = """
                {
                    "firstname" : "Nikita",
                    "lastname" : "Testerovich",
                    "totalprice" : 111,
                    "depositpaid" : true,
                    "bookingdates" : {
                    "checkin" : "2021-11-11",
                    "checkout" : "2022-12-12"
                    },
                    "additionalneeds" : "Breakfast"
                }
                """;
        APIResponse createNewBookResponse = request.post("/booking", RequestOptions.create().setData(body));
        assertEquals(200, createNewBookResponse.status());

        JsonObject jsonResponse = JsonParser.parseString(createNewBookResponse.text()).getAsJsonObject();
        savedBookingId = jsonResponse.get("bookingid").getAsInt();

        assertTrue(savedBookingId >0);
    }

    @AllureId("028")
    @Test
    @Order(3)
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Получить 'id' существующей книг/и по имени автора с помощью 'GET /booking'")
    public void getBookListByDateTest() {
        APIResponse getBookListByNameResponse = request.get("/booking?firstname=Nikita&lastname=Testerovich", RequestOptions.create().setData(""));
        assertEquals(200, getBookListByNameResponse.status());

        JsonArray jsonArray = JsonParser.parseString(getBookListByNameResponse.text()).getAsJsonArray();
        assertFalse(jsonArray.isEmpty(), "Таких книг не существует :(");

        System.out.println("Найдено книг: " + jsonArray.size());

        for (JsonElement element : jsonArray) {
            int id = element.getAsJsonObject().get("bookingid").getAsInt();
            System.out.println("id книги: " + id);
        }
    }

    @AllureId("029")
    @Test
    @Order(4)
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Найти данные существующей книги по id с помощью 'GET /booking/:id'")
    public void getBookDataTest() {
        APIResponse getBookIdResponse = request.get("/booking/" + savedBookingId);

        assertEquals(200, getBookIdResponse.status());
        assertNotNull(getBookIdResponse.text());
        assertFalse(getBookIdResponse.text().isEmpty());
    }

    @AllureId("030")
    @Test
    @Order(999)
    @Owner("Kolkov")
    @Tag("smoke")
    @DisplayName("Удалить книгу по 'id' на ручку 'DELETE /booking/:id'")
    public void deleteBookById() {
        var deleteBookByIdResponse = request.delete("/booking/" + savedBookingId,
                RequestOptions.create()
                        .setHeader("Cookie", "token=" + savedToken));

        assertEquals(201, deleteBookByIdResponse.status());
    }
}