package de.torbenphotography.ui;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.time.LocalDate;
import java.util.regex.Pattern;

import de.torbenphotography.entities.Request;
import de.torbenphotography.images.PhotoCategory;
import de.torbenphotography.repositories.RequestRepository;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.GridElement;
import org.vaadin.addons.dramafinder.element.LoginFormElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;

class AdminIT extends AbstractUiIT {

    @Autowired
    private RequestRepository requestRepository;

    // unique per test, so earlier tests do not show up in the search
    private String lastName;

    @BeforeEach
    void createRequest() {
        lastName = "Verwaltungstest" + System.nanoTime();
        Request request = new Request(PhotoCategory.HOCHZEIT);
        request.setSalutation("Frau");
        request.setFirstName("Greta");
        request.setLastName(lastName);
        request.setEmail("greta.admin@example.com");
        request.setDate(LocalDate.now().plusMonths(2));
        request.setComment("Trauung im Garten");
        requestRepository.save(request);
    }

    @Test
    void overviewNeedsLogin() {
        open("/admin/anfragen");

        assertThat(page).hasURL(Pattern.compile(".*/login$"));
        LoginFormElement.get(page).assertVisible();
    }

    @Test
    void wrongPasswordShowsError() {
        open("/login");
        LoginFormElement login = LoginFormElement.get(page);
        login.login(ADMIN_USERNAME, "falsch");

        login.assertErrorVisible();
        login.assertErrorTitle("Anmeldung fehlgeschlagen");
    }

    @Test
    void adminCanFindAndCompleteRequest() {
        open("/admin/anfragen");
        LoginFormElement.get(page).login(ADMIN_USERNAME, ADMIN_PASSWORD);
        waitForVaadin();

        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Anfragen").setLevel(1)))
                .isVisible();
        TextFieldElement.getByLabel(page, "Suche").setValue(lastName);
        GridElement grid = GridElement.get(page);
        grid.assertRowCount(1);
        grid.assertCellContent(0, "Name", "Greta " + lastName);
        grid.assertCellContent(0, "Kategorie", "Hochzeit");

        ButtonElement.getByText(page, "Als erledigt markieren").click();

        // the status filter shows open requests, a completed one disappears
        grid.assertEmpty();
    }

    @Test
    void logoutEndsTheSession() {
        open("/admin/anfragen");
        LoginFormElement.get(page).login(ADMIN_USERNAME, ADMIN_PASSWORD);
        waitForVaadin();

        ButtonElement.getByText(page, "Abmelden").click();
        // after logging out the application redirects to the start page
        page.waitForURL(getUrl() + "/");
        open("/admin/anfragen");

        assertThat(page).hasURL(Pattern.compile(".*/login$"));
    }
}
