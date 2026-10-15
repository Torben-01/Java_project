package de.torbenphotography.views;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

/*
 * Applies the stored user preferences to every new browser tab
 */
@Component
public class UserPreferencesInitListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiInit -> UserPreferences.restore(uiInit.getUI(), VaadinRequest.getCurrent()));
    }
}
