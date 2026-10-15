package de.torbenphotography.images;

import java.time.Duration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 * Serves the files of the image directory under /images/**
 */
@Configuration
public class ImageResourceConfig implements WebMvcConfigurer {

    private final ImageService imageService;

    public ImageResourceConfig(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + ImageService.URL_PREFIX + "**")
                .addResourceLocations(imageService.getBaseDirectory().toUri().toString())
                .setCacheControl(CacheControl.maxAge(Duration.ofHours(1)));
    }
}
