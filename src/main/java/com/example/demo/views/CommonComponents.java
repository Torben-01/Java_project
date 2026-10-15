package com.example.demo.views;

import java.util.List;

import com.example.demo.images.ImageService;
import com.example.demo.images.PhotoCategory;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class CommonComponents extends VerticalLayout {

    /*
     * Builds a category page with title, request form, vertical cover and example gallery
     * imageService ImageService to look up the images of the category
     * category PhotoCategory whose image folders are used
     * title String headline of the page
     * subtitle String text below the headline
     */
    protected CommonComponents(ImageService imageService, PhotoCategory category, String title, String subtitle) {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout content = new HorizontalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.getStyle().set("flex-shrink", "0");
        imageService.findFirst(category.coverVertical())
                .ifPresent(url -> content.add(createVerticalCover(url, title)));
        content.add(createCommonLayout(new FormLayout()));

        add(new H1(title), new Paragraph(subtitle), content);

        List<String> examples = imageService.findAll(category.examples());
        if (!examples.isEmpty()) {
            add(new H2("Beispiele"), createGallery(examples, title));
        }
    }

    private boolean isValid(ComboBox<String> salutation, TextField firstName, TextField lastName, EmailField email, DatePicker date) {
        return salutation.getValue() != null && !firstName.isEmpty() && !lastName.isEmpty() &&
               email.getValue() != null && !email.isEmpty() && date.getValue() != null;
    }

    public FormLayout createCommonLayout(FormLayout formLayout) {
        ComboBox<String> salutation = new ComboBox<>("salutation");
        salutation.setItems("Herr", "Frau", "Divers");
        salutation.setRequired(true);

        TextField firstName = new TextField("Vorname");
        firstName.setRequired(true);
        TextField lastName = new TextField("Nachname");
        lastName.setRequired(true);

        EmailField email = new EmailField("E-Mail");
        email.setRequiredIndicatorVisible(true);
        email.setErrorMessage("Bitte eine gültige E-Mail eingeben");

        DatePicker date = new DatePicker("Wunschtermin");
        date.setRequired(true);

        TextArea comment = new TextArea("Kommentar");
        comment.setPlaceholder("Gibt es besondere Wünsche?");

        Button sendButton = new Button("Anfrage senden", event -> {
            if (isValid(salutation, firstName, lastName, email, date)) {
                Notification.show("Anfrage erfolgreich gesendet!", 3000, Notification.Position.MIDDLE);
            } else {
                Notification.show("Bitte alle Pflichtfelder ausfüllen!", 3000, Notification.Position.MIDDLE);
            }
        });

        Button backButton = new Button("Zurück", event -> event.getSource().getUI().ifPresent(ui -> ui.navigate("")));
        formLayout.add(salutation, firstName, lastName, email, date, comment, sendButton, backButton);
        return formLayout;
    }

    private Image createVerticalCover(String url, String title) {
        Image cover = new Image(url, title);
        cover.setWidth("300px");
        cover.setHeight("450px");
        cover.getStyle().set("object-fit", "cover");
        cover.getStyle().set("border-radius", "15px");
        return cover;
    }

    private Div createGallery(List<String> urls, String title) {
        Div gallery = new Div();
        gallery.getStyle().set("display", "flex");
        gallery.getStyle().set("flex-wrap", "wrap");
        gallery.getStyle().set("justify-content", "center");
        gallery.getStyle().set("gap", "16px");
        gallery.getStyle().set("flex-shrink", "0");
        gallery.setWidth("80%");

        for (String url : urls) {
            Image image = new Image(url, title);
            image.setWidth("300px");
            image.setHeight("200px");
            image.getStyle().set("object-fit", "cover");
            image.getStyle().set("border-radius", "15px");
            gallery.add(image);
        }
        return gallery;
    }
}
