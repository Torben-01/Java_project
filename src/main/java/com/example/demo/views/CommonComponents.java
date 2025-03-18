package com.example.demo.views;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class CommonComponents extends VerticalLayout {

    private boolean isValid(ComboBox<String> salutation, TextField firstName, TextField lastName, EmailField email, DatePicker date) {
        return salutation.getValue() != null && !firstName.isEmpty() && !lastName.isEmpty() &&
               email.getValue() != null && !email.isEmpty() && date.getValue() != null;
    }

    public FormLayout createCommonLayout(FormLayout formLayout) {
        setAlignItems(Alignment.CENTER);

        ComboBox<String> salutation = new ComboBox<>("salutation");
        salutation.setItems("Herr", "Frau", "Divers");
        salutation.setRequired(true);
        add(salutation);

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
}
