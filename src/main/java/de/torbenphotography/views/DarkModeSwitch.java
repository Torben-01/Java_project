package de.torbenphotography.views;

import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;

/*
 * Slide switch with a sun on the left and a moon on the right, the knob moves to the active side.
 * It is a native button with the switch role, so it works with keyboard and screen readers.
 */
class DarkModeSwitch extends NativeButton {

    DarkModeSwitch(String label) {
        addClassName("dark-mode-switch");
        getElement().setAttribute("role", "switch");
        getElement().setAttribute("aria-label", label);
        getElement().setAttribute("title", label);

        Span sun = new Span(VaadinIcon.SUN_O.create());
        sun.addClassNames("dark-mode-switch-icon", "dark-mode-switch-sun");
        Span moon = new Span(VaadinIcon.MOON_O.create());
        moon.addClassNames("dark-mode-switch-icon", "dark-mode-switch-moon");
        Span knob = new Span();
        knob.addClassName("dark-mode-switch-knob");
        add(sun, moon, knob);

        setDark(false);
        addClickListener(event -> UserPreferences.toggleDarkMode(this::setDark));
        addAttachListener(event -> UserPreferences.isDarkMode(this::setDark));
    }

    void setDark(boolean dark) {
        getElement().setAttribute("aria-checked", String.valueOf(dark));
    }
}
