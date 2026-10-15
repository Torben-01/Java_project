package de.torbenphotography.views;

import de.torbenphotography.images.PhotoCategory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.i18n.I18NProvider;

/*
 * UI information per photo category: the view showing it, its icon and translated texts
 */
final class CategoryViews {

    private CategoryViews() {}

    static Class<? extends Component> viewClass(PhotoCategory category) {
        return switch (category) {
            case PORTRAIT -> PortraitView.class;
            case AUTO -> CarPhotographyView.class;
            case HOCHZEIT -> WeddingPhotographyView.class;
            case SONSTIGES -> throw new IllegalArgumentException("Für " + category + " gibt es keine eigene Seite");
        };
    }

    static VaadinIcon icon(PhotoCategory category) {
        return switch (category) {
            case PORTRAIT -> VaadinIcon.USER;
            case AUTO -> VaadinIcon.CAR;
            case HOCHZEIT -> VaadinIcon.HEART;
            case SONSTIGES -> VaadinIcon.ELLIPSIS_DOTS_H;
        };
    }

    static String title(PhotoCategory category) {
        return text(category, "title");
    }

    static String headline(PhotoCategory category) {
        return text(category, "headline");
    }

    static String description(PhotoCategory category) {
        return text(category, "description");
    }

    private static String text(PhotoCategory category, String suffix) {
        return I18NProvider.translate("category." + category.key() + "." + suffix);
    }
}
