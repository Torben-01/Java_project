package de.torbenphotography.views;

import de.torbenphotography.images.ImageService;
import de.torbenphotography.images.PhotoCategory;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "portrait", layout = MainLayout.class)
@AnonymousAllowed
public class PortraitView extends CategoryView {

    public PortraitView(ImageService imageService) {
        super(imageService, PhotoCategory.PORTRAIT);
    }
}
