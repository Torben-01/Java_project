package de.torbenphotography.services;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import de.torbenphotography.entities.Request;
import de.torbenphotography.i18n.TranslationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/*
 * Sends an e-mail to the photographer for every new request and, if enabled, a confirmation to the visitor.
 * Without a configured mail server (spring.mail.host) or recipient the request is only logged.
 */
@Service
public class RequestNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(RequestNotificationService.class);
    private static final Locale OWNER_LOCALE = Locale.GERMAN;

    private final ObjectProvider<JavaMailSender> mailSender;
    private final TranslationProvider translations;
    private final String recipient;
    private final String sender;
    private final boolean confirmCustomer;

    public RequestNotificationService(ObjectProvider<JavaMailSender> mailSender,
                                      TranslationProvider translations,
                                      @Value("${app.notification.recipient:}") String recipient,
                                      @Value("${app.notification.sender:}") String sender,
                                      @Value("${app.notification.confirm-customer:false}") boolean confirmCustomer) {
        this.mailSender = mailSender;
        this.translations = translations;
        this.recipient = recipient;
        this.sender = sender;
        this.confirmCustomer = confirmCustomer;
    }

    /*
     * Runs in the background so the visitor does not wait for the mail server
     * request Request the stored request
     * locale Locale language of the visitor, used for the confirmation
     */
    @Async
    public void requestReceived(Request request, Locale locale) {
        JavaMailSender mail = mailSender.getIfAvailable();
        if (mail == null || recipient.isBlank()) {
            LOG.info("Neue Anfrage {} von {} {} – Mailversand ist nicht konfiguriert",
                    request.getId(), request.getFirstName(), request.getLastName());
            return;
        }
        try {
            mail.send(ownerMessage(request));
            if (confirmCustomer) {
                mail.send(customerMessage(request, locale));
            }
        } catch (MailException e) {
            LOG.error("Benachrichtigung zur Anfrage {} konnte nicht gesendet werden", request.getId(), e);
        }
    }

    private SimpleMailMessage ownerMessage(Request request) {
        SimpleMailMessage message = newMessage(recipient);
        message.setReplyTo(request.getEmail());
        message.setSubject(text(OWNER_LOCALE, "mail.owner.subject", category(request, OWNER_LOCALE), fullName(request)));
        message.setText(text(OWNER_LOCALE, "mail.owner.text",
                category(request, OWNER_LOCALE),
                text(OWNER_LOCALE, "salutation." + request.getSalutation()) + " " + fullName(request),
                request.getEmail(),
                date(request, OWNER_LOCALE),
                request.getComment() == null || request.getComment().isBlank() ? "-" : request.getComment()));
        return message;
    }

    private SimpleMailMessage customerMessage(Request request, Locale locale) {
        SimpleMailMessage message = newMessage(request.getEmail());
        message.setReplyTo(recipient);
        message.setSubject(text(locale, "mail.customer.subject"));
        message.setText(text(locale, "mail.customer.text", request.getFirstName(), category(request, locale), date(request, locale)));
        return message;
    }

    private SimpleMailMessage newMessage(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(sender.isBlank() ? recipient : sender);
        return message;
    }

    private String category(Request request, Locale locale) {
        return text(locale, "category." + request.getCategory().key() + ".headline");
    }

    private static String fullName(Request request) {
        return request.getFirstName() + " " + request.getLastName();
    }

    private static String date(Request request, Locale locale) {
        return request.getDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale));
    }

    private String text(Locale locale, String key, Object... params) {
        return translations.getTranslation(key, locale, params);
    }
}
