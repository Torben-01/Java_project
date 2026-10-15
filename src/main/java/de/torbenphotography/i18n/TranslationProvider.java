package de.torbenphotography.i18n;

import java.util.List;
import java.util.Locale;

import com.vaadin.flow.i18n.DefaultI18NProvider;
import org.springframework.stereotype.Component;

/*
 * Loads the texts from src/main/resources/vaadin-i18n/translations_<language>.properties.
 * German comes first, so it is used whenever the browser asks for a language we do not offer.
 */
@Component
public class TranslationProvider extends DefaultI18NProvider {

    public static final List<Locale> LOCALES = List.of(Locale.GERMAN, Locale.ENGLISH);

    public TranslationProvider() {
        super(LOCALES);
    }
}
