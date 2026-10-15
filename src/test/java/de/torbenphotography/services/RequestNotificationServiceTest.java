package de.torbenphotography.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import de.torbenphotography.entities.Request;
import de.torbenphotography.i18n.TranslationProvider;
import de.torbenphotography.images.PhotoCategory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

class RequestNotificationServiceTest {

    private final JavaMailSender mailSender = mock(JavaMailSender.class);

    @Test
    void sendsMailToPhotographerWithRequestDetails() {
        service(mailSender, "studio@example.com", false).requestReceived(request(), Locale.ENGLISH);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        assertThat(message.getTo()).containsExactly("studio@example.com");
        assertThat(message.getReplyTo()).isEqualTo("erika@example.com");
        assertThat(message.getSubject()).isEqualTo("Neue Anfrage: Autofotografie – Erika Musterfrau");
        assertThat(message.getText()).contains("Frau Erika Musterfrau", "Oldtimer am Hafen");
    }

    @Test
    void sendsConfirmationInVisitorLanguageWhenEnabled() {
        service(mailSender, "studio@example.com", true).requestReceived(request(), Locale.ENGLISH);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(2)).send(captor.capture());
        List<SimpleMailMessage> messages = captor.getAllValues();
        SimpleMailMessage confirmation = messages.get(1);
        assertThat(confirmation.getTo()).containsExactly("erika@example.com");
        assertThat(confirmation.getSubject()).isEqualTo("Your request at Torben Photography");
        assertThat(confirmation.getText()).startsWith("Hello Erika");
    }

    @Test
    void sendsNothingWithoutRecipient() {
        service(mailSender, "", true).requestReceived(request(), Locale.GERMAN);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendsNothingWithoutMailServer() {
        service(null, "studio@example.com", true).requestReceived(request(), Locale.GERMAN);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @SuppressWarnings("unchecked")
    private static RequestNotificationService service(JavaMailSender sender, String recipient, boolean confirm) {
        ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(sender);
        return new RequestNotificationService(provider, new TranslationProvider(), recipient, "", confirm);
    }

    private static Request request() {
        Request request = new Request(PhotoCategory.AUTO);
        request.setSalutation("Frau");
        request.setFirstName("Erika");
        request.setLastName("Musterfrau");
        request.setEmail("erika@example.com");
        request.setDate(LocalDate.of(2026, 12, 5));
        request.setComment("Oldtimer am Hafen");
        return request;
    }
}
