package com.example.demo.views;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.entities.Request;
import com.example.demo.images.ImageService;
import com.example.demo.images.PhotoCategory;
import com.example.demo.services.RequestService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

public class CommonComponents extends VerticalLayout {

    private static final Logger LOG = LoggerFactory.getLogger(CommonComponents.class);

    private final RequestService requestService;

    /*
     * Builds a category page with title, request form, vertical cover and example gallery
     * imageService ImageService to look up the images of the category
     * requestService RequestService to store the submitted requests
     * category PhotoCategory whose image folders are used
     * title String headline of the page
     * subtitle String text below the headline
     */
    protected CommonComponents(ImageService imageService, RequestService requestService, PhotoCategory category,
                               String title, String subtitle) {
        this.requestService = requestService;

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout content = new HorizontalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.getStyle().set("flex-shrink", "0");
        imageService.findFirst(category.coverVertical())
                .ifPresent(url -> content.add(createVerticalCover(url, title)));
        content.add(createCommonLayout(new FormLayout(), category));

        add(new H1(title), new Paragraph(subtitle), content);

        List<String> examples = imageService.findAll(category.examples());
        if (!examples.isEmpty()) {
            add(new H2("Beispiele"), createGallery(examples, title));
        }
    }

    /*
     * Creates the request form, a valid request is stored for the given category
     * formLayout FormLayout the fields are added to
     * category PhotoCategory the request belongs to
     */
    private FormLayout createCommonLayout(FormLayout formLayout, PhotoCategory category) {
        Binder<Request> binder = new Binder<>(Request.class);

        ComboBox<String> salutation = new ComboBox<>("Anrede");
        salutation.setItems("Herr", "Frau", "Divers");
        binder.forField(salutation)
                .asRequired("Bitte eine Anrede auswählen")
                .bind(Request::getSalutation, Request::setSalutation);

        TextField firstName = new TextField("Vorname");
        firstName.setMaxLength(Request.NAME_MAX_LENGTH);
        binder.forField(firstName)
                .asRequired("Bitte den Vornamen eingeben")
                .bind(Request::getFirstName, Request::setFirstName);

        TextField lastName = new TextField("Nachname");
        lastName.setMaxLength(Request.NAME_MAX_LENGTH);
        binder.forField(lastName)
                .asRequired("Bitte den Nachnamen eingeben")
                .bind(Request::getLastName, Request::setLastName);

        EmailField email = new EmailField("E-Mail");
        email.setMaxLength(Request.EMAIL_MAX_LENGTH);
        binder.forField(email)
                .asRequired("Bitte eine E-Mail-Adresse eingeben")
                .withValidator(new EmailValidator("Bitte eine gültige E-Mail eingeben"))
                .bind(Request::getEmail, Request::setEmail);

        DatePicker date = new DatePicker("Wunschtermin");
        date.setMin(LocalDate.now());
        binder.forField(date)
                .asRequired("Bitte einen Wunschtermin auswählen")
                .withValidator(value -> !value.isBefore(LocalDate.now()), "Der Wunschtermin darf nicht in der Vergangenheit liegen")
                .bind(Request::getDate, Request::setDate);

        TextArea comment = new TextArea("Kommentar");
        comment.setPlaceholder("Gibt es besondere Wünsche?");
        comment.setMaxLength(Request.COMMENT_MAX_LENGTH);
        binder.forField(comment).bind(Request::getComment, Request::setComment);

        Button sendButton = new Button("Anfrage senden", event -> {
            Request request = new Request(category);
            if (!binder.writeBeanIfValid(request)) {
                showNotification("Bitte alle Pflichtfelder korrekt ausfüllen!", NotificationVariant.LUMO_ERROR);
                return;
            }
            try {
                requestService.save(request);
                binder.readBean(null);
                showNotification("Anfrage erfolgreich gesendet!", NotificationVariant.LUMO_SUCCESS);
            } catch (DataAccessException e) {
                LOG.error("Anfrage konnte nicht gespeichert werden", e);
                showNotification("Die Anfrage konnte gerade nicht gespeichert werden. Bitte später erneut versuchen.",
                        NotificationVariant.LUMO_ERROR);
            }
        });

        Button backButton = new Button("Zurück", event -> event.getSource().getUI().ifPresent(ui -> ui.navigate("")));
        formLayout.add(salutation, firstName, lastName, email, date, comment, sendButton, backButton);
        return formLayout;
    }

    private void showNotification(String text, NotificationVariant variant) {
        Notification notification = Notification.show(text, 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(variant);
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
