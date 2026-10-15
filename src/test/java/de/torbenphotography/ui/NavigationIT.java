package de.torbenphotography.ui;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.CardElement;

class NavigationIT extends AbstractUiIT {

    @Test
    void startPageShowsHeroAndAllCategories() {
        assertThat(heading("Willkommen bei Torben Photography")).isVisible();
        CardElement.getByTitle(page, "Portrait").assertVisible();
        CardElement.getByTitle(page, "Auto").assertVisible();
        CardElement.getByTitle(page, "Hochzeit").assertVisible();
    }

    @Test
    void categoryCardLeadsToCategoryPage() {
        CardElement car = CardElement.getByTitle(page, "Auto");
        ButtonElement.getByText(car.getLocator(), "Mehr erfahren").click();

        assertThat(page).hasURL(Pattern.compile(".*/car-photography$"));
        assertThat(heading("Autofotografie")).isVisible();
    }

    @Test
    void categoryPageOpensRequestFormWithCategory() {
        open("/wedding-photography");
        ButtonElement.getByText(page, "Jetzt anfragen").click();

        assertThat(page).hasURL(Pattern.compile(".*/anfrage/hochzeit$"));
        assertThat(heading("Anfrage stellen")).isVisible();
    }

    @Test
    void footerLinksToLegalPages() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Impressum")).click();
        assertThat(heading("Impressum")).isVisible();

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Datenschutz")).click();
        assertThat(heading("Datenschutzerklärung")).isVisible();
    }

    private Locator heading(String text) {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(text).setLevel(1));
    }
}
