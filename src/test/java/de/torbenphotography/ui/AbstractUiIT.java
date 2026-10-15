package de.torbenphotography.ui;

import java.util.List;

import com.microsoft.playwright.options.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.vaadin.addons.dramafinder.AbstractBasePlaywrightIT;

/*
 * Base of the browser tests: starts the application on a random port and opens the view of the test
 * in German. The browser runs headless, start with -Dheadless=false to watch it.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "app.admin.username=" + AbstractUiIT.ADMIN_USERNAME,
        "app.admin.password=" + AbstractUiIT.ADMIN_PASSWORD,
        "vaadin.launch-browser=false"
})
public abstract class AbstractUiIT extends AbstractBasePlaywrightIT {

    static final String ADMIN_USERNAME = "admin";
    static final String ADMIN_PASSWORD = "test-password";

    @LocalServerPort
    private int port;

    @Override
    public String getUrl() {
        return "http://localhost:" + port;
    }

    @Override
    public String getView() {
        return "/";
    }

    /*
     * The headless browser asks for English, the tests are written against the German texts
     */
    @BeforeEach
    void useGerman() {
        page.context().addCookies(List.of(new Cookie("language", "de").setUrl(getUrl())));
        open(getView());
    }

    protected void open(String view) {
        page.navigate(getUrl() + view);
        waitForVaadin();
    }

    protected void waitForVaadin() {
        page.waitForFunction(WAIT_FOR_VAADIN_SCRIPT);
    }
}
