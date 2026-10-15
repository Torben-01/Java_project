package com.example.demo.views;

import com.example.demo.images.ImageService;
import com.example.demo.images.PhotoCategory;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("car-photography")
@PermitAll
public class CarPhotographyView extends CommonComponents {

    public CarPhotographyView(ImageService imageService) {
        super(imageService, PhotoCategory.AUTO, "Autofotografie",
                "Perfekte Inszenierung deines Autos – egal ob Sportwagen oder Oldtimer.");
    }
}
