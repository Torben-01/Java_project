package de.torbenphotography.views;

import java.util.Locale;

import de.torbenphotography.i18n.TranslationProvider;
import de.torbenphotography.images.PhotoCategory;
import de.torbenphotography.services.RequestService;
import de.torbenphotography.views.admin.RequestsAdminView;
import de.torbenphotography.views.legal.ImprintView;
import de.torbenphotography.views.legal.PrivacyView;
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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;

/*
 * Common layout of all pages: navigation bar on top, the current view and a footer.
 * On small screens the navigation links move into the drawer.
 */
@AnonymousAllowed
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private final transient AuthenticationContext authenticationContext;
    private final Main content = new Main();
    private final Div navbarActions = new Div();

    public MainLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
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
        navbarActions.add(new DarkModeSwitch(getTranslation("nav.darkmode")), createLanguageSelect(), request,
                createAccountButton());
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
        for (PhotoCategory category : PhotoCategory.showcases()) {
            nav.add(new RouterLink(CategoryViews.title(category), CategoryViews.viewClass(category)));
        }
        if (authenticationContext.hasRole(RequestService.ADMIN_ROLE)) {
            nav.add(new RouterLink(getTranslation("nav.admin"), RequestsAdminView.class));
        }
        return nav;
    }

    private Component createDrawerNavigation() {
        SideNav nav = new SideNav();
        nav.addClassName("drawer-nav");
        nav.addItem(new SideNavItem(getTranslation("nav.home"), HomeView.class, VaadinIcon.HOME.create()));
        nav.addItem(new SideNavItem(getTranslation("nav.request"), RequestFormView.class, VaadinIcon.ENVELOPE.create()));
        if (authenticationContext.hasRole(RequestService.ADMIN_ROLE)) {
            nav.addItem(new SideNavItem(getTranslation("nav.admin"), RequestsAdminView.class, VaadinIcon.INBOX.create()));
        }
        for (PhotoCategory category : PhotoCategory.showcases()) {
            nav.addItem(new SideNavItem(CategoryViews.title(category), CategoryViews.viewClass(category),
                    CategoryViews.icon(category).create()));
        }
        return nav;
    }

    private Component createAccountButton() {
        if (!authenticationContext.isAuthenticated()) {
            Button login = new Button(VaadinIcon.SIGN_IN.create(),
                    event -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
            login.addThemeVariants(ButtonVariant.TERTIARY);
            login.setAriaLabel(getTranslation("nav.login"));
            login.setTooltipText(getTranslation("nav.login"));
            return login;
        }
        Button logout = new Button(VaadinIcon.SIGN_OUT.create(), event -> authenticationContext.logout());
        logout.addThemeVariants(ButtonVariant.TERTIARY);
        logout.setAriaLabel(getTranslation("nav.logout"));
        logout.setTooltipText(getTranslation("nav.logout"));
        return logout;
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
        Div links = new Div(new RouterLink(getTranslation("footer.imprint"), ImprintView.class),
                new RouterLink(getTranslation("footer.privacy"), PrivacyView.class));
        links.addClassName("footer-links");
        Div inner = new Div(new Span("© Torben Photography"), links);
        inner.addClassName("section");
        Footer footer = new Footer(inner);
        footer.addClassName("footer");
        return footer;
    }
}
