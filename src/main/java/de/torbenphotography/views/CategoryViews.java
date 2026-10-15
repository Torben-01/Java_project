package de.torbenphotography.views;

import de.torbenphotography.images.PhotoCategory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;

/*
 * UI information per photo category: the view showing it, its icon and texts
 */
final class CategoryViews {

    private CategoryViews() {}

    static Class<? extends Component> viewClass(PhotoCategory category) {
        return switch (category) {
            case PORTRAIT -> PortraitView.class;
            case AUTO -> CarPhotographyView.class;
            case HOCHZEIT -> WeddingPhotographyView.class;
        };
    }

    static VaadinIcon icon(PhotoCategory category) {
        return switch (category) {
            case PORTRAIT -> VaadinIcon.USER;
            case AUTO -> VaadinIcon.CAR;
            case HOCHZEIT -> VaadinIcon.HEART;
        };
    }

    static String title(PhotoCategory category) {
        return switch (category) {
            case PORTRAIT -> "Portrait";
            case AUTO -> "Auto";
            case HOCHZEIT -> "Hochzeit";
        };
    }

    static String description(PhotoCategory category) {
        return switch (category) {
            case PORTRAIT -> "Dein Gesicht im Fokus – professionelle Portraitaufnahmen für jeden Anlass.";
            case AUTO -> "Perfekte Inszenierung deines Autos – egal ob Sportwagen oder Oldtimer.";
            case HOCHZEIT -> "Unvergessliche Momente eures großen Tages für die Ewigkeit festhalten.";
        };
    }
}
