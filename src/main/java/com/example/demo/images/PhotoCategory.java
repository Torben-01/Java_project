package com.example.demo.images;

/*
 * Photo categories and their image folders below images/<folder>/
 * cover          image for the category card on the start page
 * cover-vertikal portrait format image next to the request form
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

    public String cover() { return folder + "/cover"; }
    public String coverVertical() { return folder + "/cover-vertikal"; }
    public String examples() { return folder + "/beispiele"; }
}
