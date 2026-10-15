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
                .addResourceLocations(directoryLocation())
                .setCacheControl(CacheControl.maxAge(Duration.ofHours(1)));
    }

    /*
     * Path.toUri() only ends with "/" if the directory already exists. Without it Spring would resolve
     * the files next to the directory instead of inside it, so images added after the start would be missing.
     */
    String directoryLocation() {
        String location = imageService.getBaseDirectory().toUri().toString();
        return location.endsWith("/") ? location : location + "/";
    }
}
