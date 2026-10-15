package de.torbenphotography.ui;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;
import org.vaadin.addons.dramafinder.element.SelectElement;

class PreferencesIT extends AbstractUiIT {

    @Test
    void languageCanBeSwitchedAndIsRemembered() {
        SelectElement.getByLabel(page, "Sprache").selectItem("EN");
        waitForVaadin();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Welcome to Torben Photography")))
                .isVisible();

        // the choice is kept in a cookie and applied again when the page is opened later
        open("/anfrage");
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Request a shoot")))
                .isVisible();
    }

    @Test
    void darkModeCanBeSwitchedAndIsRemembered() {
        Locator toggle = darkModeSwitch();
        assertThat(toggle).hasAttribute("aria-checked", "false");

        toggle.click();
        assertThat(toggle).hasAttribute("aria-checked", "true");
        assertThat(page.locator("html")).hasAttribute("theme", "dark");

        open("/portrait");
        assertThat(darkModeSwitch()).hasAttribute("aria-checked", "true");
        assertThat(page.locator("html")).hasAttribute("theme", "dark");

        darkModeSwitch().click();
        assertThat(darkModeSwitch()).hasAttribute("aria-checked", "false");
        assertThat(page.locator("html")).not().hasAttribute("theme", "dark");
    }

    private Locator darkModeSwitch() {
        return page.getByRole(AriaRole.SWITCH, new Page.GetByRoleOptions().setName("Hell/Dunkel umschalten"));
    }
}
