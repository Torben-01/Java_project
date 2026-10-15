package de.torbenphotography.views;

import java.time.LocalDate;

import de.torbenphotography.entities.Request;
import de.torbenphotography.images.PhotoCategory;
import de.torbenphotography.services.RequestService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.card.CardVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

/*
 * Request form for a photo shoot, reachable as /anfrage or preselected as /anfrage/<category key>
 */
@Route(value = "anfrage", layout = MainLayout.class)
@PageTitle("Anfrage | Torben Photography")
public class RequestFormView extends Div implements HasUrlParameter<String> {

    private static final Logger LOG = LoggerFactory.getLogger(RequestFormView.class);

    private final RequestService requestService;
    private final Binder<Request> binder = new Binder<>(Request.class);
    private final Select<PhotoCategory> category = new Select<>();
    private final Component formCard;
    private final Div confirmation = new Div();

    public RequestFormView(RequestService requestService) {
        this.requestService = requestService;
        addClassNames("section", "request-view");

        H1 title = new H1("Anfrage stellen");
        title.addClassName("section-title");
        Paragraph lead = new Paragraph("Erzähl mir von deinem Wunsch-Shooting – ich melde mich so schnell wie möglich bei dir.");
        lead.addClassName("section-lead");

        formCard = createForm();
        confirmation.setVisible(false);
        add(title, lead, formCard, confirmation);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String categoryKey) {
        PhotoCategory.fromKey(categoryKey).ifPresent(category::setValue);
    }

    private Component createForm() {
        category.setLabel("Kategorie");
        category.setItems(PhotoCategory.values());
        category.setItemLabelGenerator(CategoryViews::title);
        binder.forField(category)
                .asRequired("Bitte eine Kategorie auswählen")
                .bind(Request::getCategory, Request::setCategory);

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

        FormLayout form = new FormLayout(category, salutation, firstName, lastName, email, date, comment);
        form.setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("600px", 2));
        form.setColspan(category, 2);
        form.setColspan(comment, 2);

        Button send = new Button("Anfrage senden", event -> submit());
        send.addThemeVariants(ButtonVariant.PRIMARY);

        Card card = new Card();
        card.addThemeVariants(CardVariant.OUTLINED);
        card.add(form);
        card.addToFooter(send);
        return card;
    }

    private void submit() {
        Request request = new Request();
        if (!binder.writeBeanIfValid(request)) {
            showNotification("Bitte alle Pflichtfelder korrekt ausfüllen!", NotificationVariant.ERROR);
            return;
        }
        try {
            requestService.save(request);
            confirmation.removeAll();
            confirmation.add(createConfirmation(request));
            showConfirmation(true);
        } catch (DataAccessException e) {
            LOG.error("Anfrage konnte nicht gespeichert werden", e);
            showNotification("Die Anfrage konnte gerade nicht gespeichert werden. Bitte später erneut versuchen.",
                    NotificationVariant.ERROR);
        }
    }

    private Component createConfirmation(Request request) {
        Card card = new Card();
        card.addThemeVariants(CardVariant.OUTLINED);
        card.setHeaderPrefix(VaadinIcon.CHECK_CIRCLE.create());
        card.setTitle("Vielen Dank, " + request.getFirstName() + "!");
        card.add(new Paragraph("Deine Anfrage ist angekommen. Ich melde mich in den nächsten Tagen per E-Mail bei dir."));

        Button again = new Button("Weitere Anfrage stellen", event -> {
            PhotoCategory lastCategory = category.getValue();
            binder.readBean(null);
            category.setValue(lastCategory);
            showConfirmation(false);
        });
        card.addToFooter(again);
        return card;
    }

    private void showConfirmation(boolean show) {
        formCard.setVisible(!show);
        confirmation.setVisible(show);
    }

    private void showNotification(String text, NotificationVariant variant) {
        Notification notification = Notification.show(text, 4000, Notification.Position.MIDDLE);
        notification.addThemeVariants(variant);
    }
}
