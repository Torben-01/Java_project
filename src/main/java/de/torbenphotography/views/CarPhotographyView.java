package de.torbenphotography.views;

import de.torbenphotography.images.ImageService;
import de.torbenphotography.images.PhotoCategory;
import com.vaadin.flow.router.Route;

@Route(value = "car-photography", layout = MainLayout.class)
public class CarPhotographyView extends CategoryView {

    public CarPhotographyView(ImageService imageService) {
        super(imageService, PhotoCategory.AUTO);
    }
}
