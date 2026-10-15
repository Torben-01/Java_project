package de.torbenphotography.ui;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import de.torbenphotography.entities.Request;
import de.torbenphotography.entities.RequestStatus;
import de.torbenphotography.images.PhotoCategory;
import de.torbenphotography.repositories.RequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.dramafinder.element.ButtonElement;
import org.vaadin.addons.dramafinder.element.CardElement;
import org.vaadin.addons.dramafinder.element.CheckboxElement;
import org.vaadin.addons.dramafinder.element.ComboBoxElement;
import org.vaadin.addons.dramafinder.element.DatePickerElement;
import org.vaadin.addons.dramafinder.element.EmailFieldElement;
import org.vaadin.addons.dramafinder.element.NotificationElement;
import org.vaadin.addons.dramafinder.element.SelectElement;
import org.vaadin.addons.dramafinder.element.TextAreaElement;
import org.vaadin.addons.dramafinder.element.TextFieldElement;

class RequestFormIT extends AbstractUiIT {

    @Autowired
    private RequestRepository requestRepository;

    @Override
    public String getView() {
        return "/anfrage/auto";
    }

    @Test
    void categoryFromUrlIsPreselected() {
        SelectElement.getByLabel(page, "Kategorie").assertValue("Auto");
    }

    @Test
    void emptyFormIsRejected() {
        ButtonElement.getByText(page, "Anfrage senden").click();

        NotificationElement.getByText(page, "Bitte alle Pflichtfelder korrekt ausfüllen!").assertOpen();
        TextFieldElement.getByLabel(page, "Vorname").assertErrorMessage("Bitte den Vornamen eingeben");
        CheckboxElement.getByLabel(page, "Datenschutzerklärung").assertInvalid();
    }

    @Test
    void invalidEmailIsRejected() {
        EmailFieldElement email = EmailFieldElement.getByLabel(page, "E-Mail");
        email.setValue("keine-adresse");
        ButtonElement.getByText(page, "Anfrage senden").click();

        email.assertErrorMessage("Bitte eine gültige E-Mail eingeben");
    }

    @Test
    void validRequestIsStoredAndConfirmed() {
        ComboBoxElement.getByLabel(page, "Anrede").selectItem("Frau");
        TextFieldElement.getByLabel(page, "Vorname").setValue("Erika");
        TextFieldElement.getByLabel(page, "Nachname").setValue("Formulartest");
        EmailFieldElement.getByLabel(page, "E-Mail").setValue("erika.formular@example.com");
        DatePickerElement.getByLabel(page, "Wunschtermin").setValue(LocalDate.now().plusDays(14));
        TextAreaElement.getByLabel(page, "Kommentar").setValue("Oldtimer am Hafen");
        CheckboxElement.getByLabel(page, "Datenschutzerklärung").check();
        ButtonElement.getByText(page, "Anfrage senden").click();

        CardElement.getByTitle(page, "Vielen Dank, Erika!").assertVisible();
        Request stored = requestRepository.findAll().stream()
                .filter(request -> "erika.formular@example.com".equals(request.getEmail()))
                .findFirst()
                .orElseThrow();
        assertThat(stored.getCategory()).isEqualTo(PhotoCategory.AUTO);
        assertThat(stored.getSalutation()).isEqualTo("Frau");
        assertThat(stored.getStatus()).isEqualTo(RequestStatus.OFFEN);
        assertThat(stored.getPrivacyAcceptedAt()).isNotNull();
    }
}
