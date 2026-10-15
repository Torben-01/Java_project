package de.torbenphotography.views;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import de.torbenphotography.entities.Request;
import de.torbenphotography.images.PhotoCategory;
import de.torbenphotography.services.RequestNotificationService;
import de.torbenphotography.services.RequestService;
import de.torbenphotography.views.legal.PrivacyView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.card.CardVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
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
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

/*
 * Request form for a photo shoot, reachable as /anfrage or preselected as /anfrage/<category key>
 */
@Route(value = "anfrage", layout = MainLayout.class)
@AnonymousAllowed
public class RequestFormView extends Div implements HasUrlParameter<String>, HasDynamicTitle {

    private static final Logger LOG = LoggerFactory.getLogger(RequestFormView.class);
    // stored as is in the database, shown translated
    private static final List<String> SALUTATIONS = List.of("Herr", "Frau", "Divers");

    private final RequestService requestService;
    private final RequestNotificationService notificationService;
    private final Binder<Request> binder = new Binder<>(Request.class);
    private final Select<PhotoCategory> category = new Select<>();
    private final Component formCard;
    private final Div confirmation = new Div();

    public RequestFormView(RequestService requestService, RequestNotificationService notificationService) {
        this.requestService = requestService;
        this.notificationService = notificationService;
        addClassNames("section", "request-view");

        H1 title = new H1(getTranslation("request.title"));
        title.addClassName("section-title");
        Paragraph lead = new Paragraph(getTranslation("request.lead"));
        lead.addClassName("section-lead");

        formCard = createForm();
        confirmation.setVisible(false);
        add(title, lead, formCard, confirmation);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("request.pagetitle") + " | Torben Photography";
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String categoryKey) {
        PhotoCategory.fromKey(categoryKey).ifPresent(category::setValue);
    }

    private Component createForm() {
        category.setLabel(getTranslation("request.category"));
        category.setItems(PhotoCategory.values());
        category.setItemLabelGenerator(CategoryViews::title);
        binder.forField(category)
                .asRequired(getTranslation("request.category.required"))
                .bind(Request::getCategory, Request::setCategory);

        ComboBox<String> salutation = new ComboBox<>(getTranslation("request.salutation"));
        salutation.setItems(SALUTATIONS);
        salutation.setItemLabelGenerator(value -> getTranslation("salutation." + value));
        binder.forField(salutation)
                .asRequired(getTranslation("request.salutation.required"))
                .bind(Request::getSalutation, Request::setSalutation);

        TextField firstName = new TextField(getTranslation("request.firstName"));
        firstName.setMaxLength(Request.NAME_MAX_LENGTH);
        binder.forField(firstName)
                .asRequired(getTranslation("request.firstName.required"))
                .bind(Request::getFirstName, Request::setFirstName);

        TextField lastName = new TextField(getTranslation("request.lastName"));
        lastName.setMaxLength(Request.NAME_MAX_LENGTH);
        binder.forField(lastName)
                .asRequired(getTranslation("request.lastName.required"))
                .bind(Request::getLastName, Request::setLastName);

        EmailField email = new EmailField(getTranslation("request.email"));
        email.setMaxLength(Request.EMAIL_MAX_LENGTH);
        binder.forField(email)
                .asRequired(getTranslation("request.email.required"))
                .withValidator(new EmailValidator(getTranslation("request.email.invalid")))
                .bind(Request::getEmail, Request::setEmail);

        DatePicker date = new DatePicker(getTranslation("request.date"));
        date.setMin(LocalDate.now());
        date.setLocale(getLocale());
        date.setI18n(DatePickerTranslations.forLocale(getLocale()));
        binder.forField(date)
                .asRequired(getTranslation("request.date.required"))
                .withValidator(value -> !value.isBefore(LocalDate.now()), getTranslation("request.date.past"))
                .bind(Request::getDate, Request::setDate);

        TextArea comment = new TextArea(getTranslation("request.comment"));
        comment.setPlaceholder(getTranslation("request.comment.placeholder"));
        comment.setMaxLength(Request.COMMENT_MAX_LENGTH);
        binder.forField(comment).bind(Request::getComment, Request::setComment);

        Anchor privacyLink = new Anchor(RouteConfiguration.forSessionScope().getUrl(PrivacyView.class),
                getTranslation("request.privacy.link"));
        privacyLink.setTarget(AnchorTarget.BLANK);
        Checkbox privacy = new Checkbox();
        privacy.setLabelComponent(new Span(new Text(getTranslation("request.privacy.before") + " "), privacyLink,
                new Text(" " + getTranslation("request.privacy.after"))));
        privacy.setRequiredIndicatorVisible(true);
        binder.forField(privacy)
                .withValidator(Boolean::booleanValue, getTranslation("request.privacy.required"))
                .bind(request -> request.getPrivacyAcceptedAt() != null,
                        (request, accepted) -> request.setPrivacyAcceptedAt(accepted ? LocalDateTime.now() : null));

        FormLayout form = new FormLayout(category, salutation, firstName, lastName, email, date, comment, privacy);
        form.setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("600px", 2));
        form.setColspan(category, 2);
        form.setColspan(comment, 2);
        form.setColspan(privacy, 2);

        Button send = new Button(getTranslation("request.send"), event -> submit());
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
            showNotification(getTranslation("request.error.invalid"), NotificationVariant.ERROR);
            return;
        }
        try {
            Request saved = requestService.save(request);
            notificationService.requestReceived(saved, getLocale());
            confirmation.removeAll();
            confirmation.add(createConfirmation(request));
            showConfirmation(true);
        } catch (DataAccessException e) {
            LOG.error("Anfrage konnte nicht gespeichert werden", e);
            showNotification(getTranslation("request.error.save"), NotificationVariant.ERROR);
        }
    }

    private Component createConfirmation(Request request) {
        Card card = new Card();
        card.addThemeVariants(CardVariant.OUTLINED);
        card.setHeaderPrefix(VaadinIcon.CHECK_CIRCLE.create());
        card.setTitle(getTranslation("request.thanks", request.getFirstName()));
        card.add(new Paragraph(getTranslation("request.thanks.text")));

        Button again = new Button(getTranslation("request.again"), event -> {
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
