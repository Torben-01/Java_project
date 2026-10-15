package de.torbenphotography.images;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

/*
 * Finds the images in the image directory (app.images.dir, default ./bilder) and returns them
 * as URLs below /images/ that can be used in an Image component. The directory is read on every
 * call, so new pictures appear without a restart.
 * Images are sorted by file name, so the order can be controlled with prefixes like 01_, 02_.
 */
@Service
public class ImageService {

    public static final String START_PAGE_BACKGROUND = "startseite/hintergrund";
    public static final String URL_PREFIX = "images/";

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif", "avif", "svg");

    private final Path baseDirectory;

    public ImageService(@Value("${app.images.dir:./bilder}") String directory) {
        this.baseDirectory = Path.of(directory).toAbsolutePath().normalize();
    }

    public Path getBaseDirectory() {
        return baseDirectory;
    }

    /*
     * Returns the URLs of all images in a folder
     * folder String folder below the image directory, e.g. "auto/beispiele"
     */
    public List<String> findAll(String folder) {
        Path directory = baseDirectory.resolve(folder).normalize();
        if (!directory.startsWith(baseDirectory) || !Files.isDirectory(directory)) {
            return List.of();
        }
        try (Stream<Path> files = Files.list(directory)) {
            return files.filter(Files::isRegularFile)
                    .map(file -> file.getFileName().toString())
                    .filter(ImageService::isImage)
                    .sorted()
                    .map(name -> URL_PREFIX + folder + "/" + UriUtils.encodePathSegment(name, "UTF-8"))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Bilder in " + directory + " konnten nicht gelesen werden", e);
        }
    }

    /*
     * Returns the URL of the first image in a folder, if there is one
     * folder String folder below the image directory, e.g. "auto/cover"
     */
    public Optional<String> findFirst(String folder) {
        return findAll(folder).stream().findFirst();
    }

    private static boolean isImage(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return dot > 0 && IMAGE_EXTENSIONS.contains(fileName.substring(dot + 1).toLowerCase(Locale.ROOT));
    }
}
