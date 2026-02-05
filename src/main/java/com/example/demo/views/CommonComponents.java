package com.example.demo.views;

import com.example.demo.entities.Request;
import com.example.demo.services.RequestService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class CommonComponents extends VerticalLayout {

    private final RequestService requestService;

    protected CommonComponents(RequestService requestService) {
        this.requestService = requestService;
    }

    private boolean isValid(ComboBox<String> salutation, TextField firstName, TextField lastName, EmailField email, DatePicker date) {
        return salutation.getValue() != null
                && !firstName.isEmpty()
                && !lastName.isEmpty()
                && email.getValue() != null
                && !email.isEmpty()
                && date.getValue() != null;
    }

    public FormLayout createCommonLayout(FormLayout formLayout, String moduleName) {
        setAlignItems(Alignment.CENTER);

        ComboBox<String> salutation = new ComboBox<>("Anrede");
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
                Request request = new Request(
                        salutation.getValue(),
                        firstName.getValue(),
                        lastName.getValue(),
                        email.getValue(),
                        date.getValue(),
                        comment.getValue(),
                        moduleName
                );
                requestService.save(request);
                Notification.show("Anfrage erfolgreich gesendet und gespeichert!", 3000, Notification.Position.MIDDLE);
                formLayout.getChildren()
                        .filter(component -> component instanceof TextField || component instanceof EmailField
                                || component instanceof DatePicker || component instanceof TextArea || component instanceof ComboBox)
                        .forEach(component -> {
                            if (component instanceof TextField textField) {
                                textField.clear();
                            } else if (component instanceof EmailField emailField) {
                                emailField.clear();
                            } else if (component instanceof DatePicker datePicker) {
                                datePicker.clear();
                            } else if (component instanceof TextArea textArea) {
                                textArea.clear();
                            } else if (component instanceof ComboBox<?> comboBox) {
                                comboBox.clear();
                            }
                        });
            } else {
                Notification.show("Bitte alle Pflichtfelder ausfüllen!", 3000, Notification.Position.MIDDLE);
            }
        });

        Button backButton = new Button("Zurück", event -> event.getSource().getUI().ifPresent(ui -> ui.navigate("")));
        formLayout.add(salutation, firstName, lastName, email, date, comment, sendButton, backButton);
        return formLayout;
    }
}
