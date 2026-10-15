package de.torbenphotography.views;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import de.torbenphotography.i18n.TranslationProvider;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinResponse;
import com.vaadin.flow.server.VaadinService;
import jakarta.servlet.http.Cookie;

/*
 * Settings the visitor chooses in the navigation bar. They are kept in cookies,
 * so they survive the end of the session and are applied again on the next visit.
 */
public final class UserPreferences {

    private static final String LANGUAGE_COOKIE = "language";
    private static final String COLOR_SCHEME_COOKIE = "color-scheme";
    private static final String DARK = "dark";
    private static final String LIGHT = "light";
    private static final int ONE_YEAR_IN_SECONDS = 60 * 60 * 24 * 365;

    private UserPreferences() {}

    /*
     * Applies the stored settings to a newly created UI
     */
    static void restore(UI ui, VaadinRequest request) {
        readCookie(request, LANGUAGE_COOKIE)
                .flatMap(UserPreferences::supportedLocale)
                .ifPresent(locale -> {
                    ui.getSession().setLocale(locale);
                    ui.setLocale(locale);
                });
        readCookie(request, COLOR_SCHEME_COOKIE)
                .map(value -> DARK.equals(value) ? ColorScheme.Value.DARK : ColorScheme.Value.LIGHT)
                .ifPresent(ui.getPage()::setColorScheme);
    }

    /*
     * Switches the language for the whole session and reloads the page, so every text is rebuilt
     */
    public static void changeLocale(Locale locale) {
        UI ui = UI.getCurrentOrThrow();
        ui.getSession().setLocale(locale);
        writeCookie(LANGUAGE_COOKIE, locale.getLanguage());
        ui.getPage().reload();
    }

    /*
     * Tells whether the dark color scheme is shown. Without a stored choice the browser decides
     * based on the operating system setting, so it has to be asked.
     */
    public static void isDarkMode(SerializableConsumer<Boolean> callback) {
        Page page = UI.getCurrentOrThrow().getPage();
        ColorScheme.Value scheme = page.getColorScheme();
        if (scheme == ColorScheme.Value.DARK) {
            callback.accept(true);
        } else if (scheme == ColorScheme.Value.LIGHT) {
            callback.accept(false);
        } else {
            page.executeJs("return window.matchMedia('(prefers-color-scheme: dark)').matches")
                    .then(Boolean.class, callback);
        }
    }

    /*
     * Switches between the light and the dark color scheme and remembers the choice
     */
    public static void toggleDarkMode(SerializableConsumer<Boolean> callback) {
        UI ui = UI.getCurrentOrThrow();
        isDarkMode(dark -> {
            boolean newDark = !dark;
            ui.getPage().setColorScheme(newDark ? ColorScheme.Value.DARK : ColorScheme.Value.LIGHT);
            writeCookie(COLOR_SCHEME_COOKIE, newDark ? DARK : LIGHT);
            callback.accept(newDark);
        });
    }

    private static Optional<Locale> supportedLocale(String language) {
        return TranslationProvider.LOCALES.stream()
                .filter(locale -> locale.getLanguage().equals(language))
                .findFirst();
    }

    private static Optional<String> readCookie(VaadinRequest request, String name) {
        if (request == null || request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .map(Cookie::getValue)
                .findFirst();
    }

    private static void writeCookie(String name, String value) {
        VaadinRequest request = VaadinService.getCurrentRequest();
        VaadinResponse response = VaadinService.getCurrentResponse();
        if (request == null || response == null) {
            return;
        }
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        cookie.setMaxAge(ONE_YEAR_IN_SECONDS);
        cookie.setHttpOnly(true);
        // behind the HTTPS proxy the request counts as secure, then the cookie is only sent over HTTPS
        cookie.setSecure(request.isSecure());
        response.addCookie(cookie);
    }
}
