package de.torbenphotography.views;

import de.torbenphotography.images.ImageService;
import de.torbenphotography.images.PhotoCategory;
import de.torbenphotography.services.RequestService;
import com.vaadin.flow.router.Route;

@Route(value = "wedding-photography", layout = MainLayout.class)
public class WeddingPhotographyView extends CommonComponents {

    public WeddingPhotographyView(ImageService imageService, RequestService requestService) {
        super(imageService, requestService, PhotoCategory.HOCHZEIT, "Hochzeitsfotografie",
                "Unvergessliche Momente für die Ewigkeit festhalten.");
    }
}
