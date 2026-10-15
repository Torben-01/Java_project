package de.torbenphotography.images;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/*
 * Photo categories and their image folders below the image directory (bilder/<folder>/)
 * cover          image for the category card on the start page
 * cover-vertikal portrait format image on the category page
 * beispiele      example pictures shown as gallery on the category page
 * SONSTIGES has no page of its own, it can only be chosen in the request form.
 */
public enum PhotoCategory {
    PORTRAIT("portrait", true),
    AUTO("auto", true),
    HOCHZEIT("hochzeit", true),
    SONSTIGES("sonstiges", false);

    private final String folder;
    private final boolean showcase;

    PhotoCategory(String folder, boolean showcase) {
        this.folder = folder;
        this.showcase = showcase;
    }

    /*
     * Categories with their own page, card on the start page and navigation entry
     */
    public static List<PhotoCategory> showcases() {
        return Arrays.stream(values()).filter(category -> category.showcase).toList();
    }

    /*
     * Short name used in URLs, translation keys and image folders, e.g. "auto"
     */
    public String key() { return folder; }

    public static Optional<PhotoCategory> fromKey(String key) {
        return Arrays.stream(values()).filter(category -> category.folder.equalsIgnoreCase(key)).findFirst();
    }

    public String cover() { return folder + "/cover"; }
    public String coverVertical() { return folder + "/cover-vertikal"; }
    public String examples() { return folder + "/beispiele"; }
}
