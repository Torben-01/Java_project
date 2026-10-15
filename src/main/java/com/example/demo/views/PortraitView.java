package com.example.demo.views;

import com.example.demo.images.ImageService;
import com.example.demo.images.PhotoCategory;
import com.vaadin.flow.router.Route;

@Route("portrait")
public class PortraitView extends CommonComponents {

    public PortraitView(ImageService imageService) {
        super(imageService, PhotoCategory.PORTRAIT, "Portraitfotografie",
                "Dein Gesicht im Fokus – professionelle Portraitaufnahmen für jeden Anlass.");
    }
}
