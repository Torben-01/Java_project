package com.example.demo.images;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

/*
 * Finds the images stored in src/main/resources/META-INF/resources/images
 * and returns them as URLs that can be used in an Image component.
 * Images are sorted by file name, so the order can be controlled with prefixes like 01_, 02_.
 */
@Service
public class ImageService {

    public static final String START_PAGE_BACKGROUND = "startseite/hintergrund";

    private static final String BASE_PATH = "META-INF/resources/images/";
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif", "avif", "svg");

    private final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /*
     * Returns the URLs of all images in a folder
     * folder String folder below images/, e.g. "auto/beispiele"
     */
    public List<String> findAll(String folder) {
        try {
            Resource[] resources = resolver.getResources("classpath*:" + BASE_PATH + folder + "/*");
            return Arrays.stream(resources)
                    .map(Resource::getFilename)
                    .filter(Objects::nonNull)
                    .filter(ImageService::isImage)
                    .distinct()
                    .sorted(Comparator.naturalOrder())
                    .map(name -> "images/" + folder + "/" + UriUtils.encodePathSegment(name, "UTF-8"))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Bilder in " + folder + " konnten nicht gelesen werden", e);
        }
    }

    /*
     * Returns the URL of the first image in a folder, if there is one
     * folder String folder below images/, e.g. "auto/cover"
     */
    public Optional<String> findFirst(String folder) {
        return findAll(folder).stream().findFirst();
    }

    private static boolean isImage(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 && IMAGE_EXTENSIONS.contains(fileName.substring(dot + 1).toLowerCase(Locale.ROOT));
    }
}
