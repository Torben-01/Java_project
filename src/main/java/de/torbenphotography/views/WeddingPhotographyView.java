package de.torbenphotography.views;

import de.torbenphotography.images.ImageService;
import de.torbenphotography.images.PhotoCategory;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "wedding-photography", layout = MainLayout.class)
@AnonymousAllowed
public class WeddingPhotographyView extends CategoryView {

    public WeddingPhotographyView(ImageService imageService) {
        super(imageService, PhotoCategory.HOCHZEIT);
    }
}
