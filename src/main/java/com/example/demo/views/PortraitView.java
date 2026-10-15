package com.example.demo.views;

import com.example.demo.images.ImageService;
import com.example.demo.images.PhotoCategory;
import com.example.demo.services.RequestService;
import com.vaadin.flow.router.Route;

@Route("portrait")
public class PortraitView extends CommonComponents {

    public PortraitView(ImageService imageService, RequestService requestService) {
        super(imageService, requestService, PhotoCategory.PORTRAIT, "Portraitfotografie",
                "Dein Gesicht im Fokus – professionelle Portraitaufnahmen für jeden Anlass.");
    }
}
