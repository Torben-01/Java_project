package de.torbenphotography.views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

/*
 * Login for the photographer, Spring Security handles the submitted form
 */
@Route(value = "login", layout = MainLayout.class)
@AnonymousAllowed
public class LoginView extends Div implements BeforeEnterObserver, HasDynamicTitle {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassNames("section", "login-view");

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle(getTranslation("login.title"));
        i18n.getForm().setUsername(getTranslation("login.username"));
        i18n.getForm().setPassword(getTranslation("login.password"));
        i18n.getForm().setSubmit(getTranslation("login.submit"));
        i18n.getErrorMessage().setTitle(getTranslation("login.error.title"));
        i18n.getErrorMessage().setMessage(getTranslation("login.error.message"));
        i18n.getErrorMessage().setUsername(getTranslation("login.error.username"));
        i18n.getErrorMessage().setPassword(getTranslation("login.error.password"));
        login.setI18n(i18n);
        login.setForgotPasswordButtonVisible(false);
        login.setAction("login");
        add(login);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("login.title") + " | Torben Photography";
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        login.setError(event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}
