package de.torbenphotography.views;

import de.torbenphotography.images.ImageService;
import de.torbenphotography.images.PhotoCategory;
import de.torbenphotography.services.RequestService;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("car-photography")
@PermitAll
public class CarPhotographyView extends CommonComponents {

    public CarPhotographyView(ImageService imageService, RequestService requestService) {
        super(imageService, requestService, PhotoCategory.AUTO, "Autofotografie",
                "Perfekte Inszenierung deines Autos – egal ob Sportwagen oder Oldtimer.");
    }
}
