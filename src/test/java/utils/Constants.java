package utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {
    public static final String BASE_URL = "http://uitestingplayground.com";

    public static final String DYNAMIC_ID_URL     = BASE_URL + "/dynamicid";
    public static final String CLASS_ATTR_URL     = BASE_URL + "/classattr";
    public static final String HIDDEN_LAYERS_URL  = BASE_URL + "/hiddenlayers";
    public static final String AJAX_URL           = BASE_URL + "/ajax";
    public static final String CLIENT_DELAY_URL   = BASE_URL + "/clientdelay";
    public static final String CLICK_URL          = BASE_URL + "/click";
    public static final String TEXT_INPUT_URL     = BASE_URL + "/textinput";
    public static final String SCROLLBARS_URL     = BASE_URL + "/scrollbars";
    public static final String DYNAMIC_TABLE_URL  = BASE_URL + "/dynamictable";
    public static final String VERIFY_TEXT_URL    = BASE_URL + "/verifytext";
    public static final String PROGRESS_BAR_URL   = BASE_URL + "/progressbar";
    public static final String VISIBILITY_URL     = BASE_URL + "/visibility";
    public static final String SAMPLE_APP_URL     = BASE_URL + "/sampleapp";
    public static final String MOUSE_OVER_URL     = BASE_URL + "/mouseover";
    public static final String NBSP_URL           = BASE_URL + "/nbsp";
    public static final String OVERLAPPED_URL     = BASE_URL + "/overlapped";
    public static final String SHADOW_DOM_URL     = BASE_URL + "/shadowdom";
    public static final String ALERTS_URL         = BASE_URL + "/alerts";
    public static final String UPLOAD_URL         = BASE_URL + "/upload";
    public static final String ANIMATION_URL      = BASE_URL + "/animation";
    public static final String DISABLED_INPUT_URL = BASE_URL + "/disabledinput";
    public static final String AUTO_WAIT_URL      = BASE_URL + "/autowait";
    public static final String FRAMES_URL         = BASE_URL + "/frames";
    public static final String GEOLOCATION_URL    = BASE_URL + "/geolocation";

    public static final Path OVERLAY_SCREENSHOT = Paths.get("screenshot/overlayButton.png");

    public static final String PRIMARY_BUTTON_XPATH = "//button[@class='btn btn-primary']";
    public static final String TEST_NAME = "testUser";
}
