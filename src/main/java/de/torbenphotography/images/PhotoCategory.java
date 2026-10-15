package de.torbenphotography.images;

import java.util.Arrays;
import java.util.Optional;

/*
 * Photo categories and their image folders below images/<folder>/
 * cover          image for the category card on the start page
 * cover-vertikal portrait format image on the category page
 * beispiele      example pictures shown as gallery on the category page
 */
public enum PhotoCategory {
    PORTRAIT("portrait"),
    AUTO("auto"),
    HOCHZEIT("hochzeit");

    private final String folder;

    PhotoCategory(String folder) {
        this.folder = folder;
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
