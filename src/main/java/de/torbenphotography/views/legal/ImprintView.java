package de.torbenphotography.views.legal;

import de.torbenphotography.views.MainLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "impressum", layout = MainLayout.class)
@AnonymousAllowed
public class ImprintView extends LegalPage {

    public ImprintView() {
        super("imprint", 3);
    }
}
