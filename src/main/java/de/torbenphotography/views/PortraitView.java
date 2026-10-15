package de.torbenphotography.views;

import de.torbenphotography.images.ImageService;
import de.torbenphotography.images.PhotoCategory;
import de.torbenphotography.services.RequestService;
import com.vaadin.flow.router.Route;

@Route("portrait")
public class PortraitView extends CommonComponents {

    public PortraitView(ImageService imageService, RequestService requestService) {
        super(imageService, requestService, PhotoCategory.PORTRAIT, "Portraitfotografie",
                "Dein Gesicht im Fokus – professionelle Portraitaufnahmen für jeden Anlass.");
    }
}
