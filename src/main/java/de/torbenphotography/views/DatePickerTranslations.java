package de.torbenphotography.views;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.vaadin.flow.component.datepicker.DatePicker.DatePickerI18n;
import com.vaadin.flow.i18n.I18NProvider;

/*
 * Month and weekday names of the date picker calendar in the visitor's language
 */
final class DatePickerTranslations {

    private DatePickerTranslations() {}

    static DatePickerI18n forLocale(Locale locale) {
        DateFormatSymbols symbols = DateFormatSymbols.getInstance(locale);
        boolean german = Locale.GERMAN.getLanguage().equals(locale.getLanguage());
        return new DatePickerI18n()
                .setMonthNames(Arrays.asList(symbols.getMonths()).subList(0, 12))
                // DateFormatSymbols starts with an empty entry, the date picker expects Sunday first
                .setWeekdays(withoutEmptyFirst(symbols.getWeekdays()))
                .setWeekdaysShort(withoutEmptyFirst(symbols.getShortWeekdays()))
                .setFirstDayOfWeek(german ? 1 : 0)
                .setDateFormat(german ? "dd.MM.yyyy" : "MM/dd/yyyy")
                .setToday(I18NProvider.translate(locale, "datepicker.today"))
                .setCancel(I18NProvider.translate(locale, "datepicker.cancel"));
    }

    private static List<String> withoutEmptyFirst(String[] names) {
        return Arrays.asList(names).subList(1, 8);
    }
}
