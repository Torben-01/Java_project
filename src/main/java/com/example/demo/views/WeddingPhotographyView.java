package com.example.demo.views;

import com.example.demo.images.ImageService;
import com.example.demo.images.PhotoCategory;
import com.vaadin.flow.router.Route;

@Route("wedding-photography")
public class WeddingPhotographyView extends CommonComponents {

    public WeddingPhotographyView(ImageService imageService) {
        super(imageService, PhotoCategory.HOCHZEIT, "Hochzeitsfotografie",
                "Unvergessliche Momente für die Ewigkeit festhalten.");
    }
}
