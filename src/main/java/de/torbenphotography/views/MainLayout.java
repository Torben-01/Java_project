package de.torbenphotography.views;

import java.util.Locale;

import de.torbenphotography.i18n.TranslationProvider;
import de.torbenphotography.images.PhotoCategory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

/*
 * Common layout of all pages: navigation bar on top, the current view and a footer.
 * On small screens the navigation links move into the drawer.
 */
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private final Main content = new Main();
    private final Div navbarActions = new Div();

    public MainLayout() {
        setPrimarySection(Section.NAVBAR);

        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("mobile-only");

        Div navbar = new Div(toggle, createBrand(), createNavigation(), navbarActions);
        navbar.addClassName("navbar");
        navbarActions.addClassName("navbar-actions");
        Button request = new Button(getTranslation("nav.request"),
                event -> getUI().ifPresent(ui -> ui.navigate(RequestFormView.class)));
        request.addThemeVariants(ButtonVariant.PRIMARY);
        request.addClassName("desktop-only");
        navbarActions.add(createDarkModeToggle(), createLanguageSelect(), request);
        addToNavbar(navbar);

        addToDrawer(createDrawerNavigation());
        setDrawerOpened(false);

        content.addClassName("page-content");
        Div page = new Div(content, createFooter());
        page.addClassName("page");
        setContent(page);
    }

    /*
     * Gives the views and later features a place for buttons on the right side of the navigation bar
     */
    protected void addNavbarActions(Component... components) {
        navbarActions.add(components);
    }

    @Override
    public void showRouterLayoutContent(HasElement routerContent) {
        content.removeAll();
        content.getElement().appendChild(routerContent.getElement());
    }

    @Override
    public void removeRouterLayoutContent(HasElement oldContent) {
        content.getElement().removeChild(oldContent.getElement());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        // close the mobile menu after a link in it was used
        setDrawerOpened(false);
    }

    private Component createBrand() {
        RouterLink brand = new RouterLink(HomeView.class);
        brand.add(VaadinIcon.CAMERA.create(), new Span("Torben Photography"));
        brand.addClassName("brand");
        return brand;
    }

    private Component createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("main-nav", "desktop-only");

        RouterLink home = new RouterLink(getTranslation("nav.home"), HomeView.class);
        home.setHighlightCondition(HighlightConditions.sameLocation());
        nav.add(home);
        for (PhotoCategory category : PhotoCategory.values()) {
            nav.add(new RouterLink(CategoryViews.title(category), CategoryViews.viewClass(category)));
        }
        return nav;
    }

    private Component createDrawerNavigation() {
        SideNav nav = new SideNav();
        nav.addClassName("drawer-nav");
        nav.addItem(new SideNavItem(getTranslation("nav.home"), HomeView.class, VaadinIcon.HOME.create()));
        nav.addItem(new SideNavItem(getTranslation("nav.request"), RequestFormView.class, VaadinIcon.ENVELOPE.create()));
        for (PhotoCategory category : PhotoCategory.values()) {
            nav.addItem(new SideNavItem(CategoryViews.title(category), CategoryViews.viewClass(category),
                    CategoryViews.icon(category).create()));
        }
        return nav;
    }

    private Component createDarkModeToggle() {
        Button toggle = new Button();
        toggle.addThemeVariants(ButtonVariant.TERTIARY);
        toggle.setAriaLabel(getTranslation("nav.darkmode"));
        toggle.setTooltipText(getTranslation("nav.darkmode"));
        toggle.addClassName("dark-mode-toggle");
        toggle.addClickListener(event -> UserPreferences.toggleDarkMode(dark -> updateDarkModeIcon(toggle, dark)));
        toggle.addAttachListener(event -> UserPreferences.isDarkMode(dark -> updateDarkModeIcon(toggle, dark)));
        return toggle;
    }

    private void updateDarkModeIcon(Button toggle, boolean dark) {
        // shows what a click switches to
        toggle.setIcon(dark ? VaadinIcon.SUN_O.create() : VaadinIcon.MOON_O.create());
    }

    private Component createLanguageSelect() {
        Select<Locale> language = new Select<>();
        language.setItems(TranslationProvider.LOCALES);
        language.setItemLabelGenerator(locale -> locale.getLanguage().toUpperCase(Locale.ROOT));
        language.setValue(TranslationProvider.LOCALES.contains(getLocale()) ? getLocale() : TranslationProvider.LOCALES.get(0));
        language.setAriaLabel(getTranslation("nav.language"));
        language.setTooltipText(getTranslation("nav.language"));
        language.addClassName("language-select");
        language.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                UserPreferences.changeLocale(event.getValue());
            }
        });
        return language;
    }

    private Component createFooter() {
        Div inner = new Div(new Span("© Torben Photography"));
        inner.addClassName("section");
        Footer footer = new Footer(inner);
        footer.addClassName("footer");
        return footer;
    }
}
