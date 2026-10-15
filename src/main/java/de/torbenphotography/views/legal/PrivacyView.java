package de.torbenphotography.views.legal;

import de.torbenphotography.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "datenschutz", layout = MainLayout.class)
@AnonymousAllowed
public class PrivacyView extends LegalPage {

    public PrivacyView() {
        super("privacy", 5);
    }
}
