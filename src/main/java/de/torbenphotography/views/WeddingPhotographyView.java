package de.torbenphotography.views;

import de.torbenphotography.images.ImageService;
import de.torbenphotography.images.PhotoCategory;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "wedding-photography", layout = MainLayout.class)
@PageTitle("Hochzeitsfotografie | Torben Photography")
public class WeddingPhotographyView extends CategoryView {

    public WeddingPhotographyView(ImageService imageService) {
        super(imageService, PhotoCategory.HOCHZEIT);
    }
}
