package de.torbenphotography.views.legal;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.HasDynamicTitle;

/*
 * Text page built from translation keys: <prefix>.title, <prefix>.<n>.heading and <prefix>.<n>.text.
 * Paragraphs inside a text are separated by "|".
 */
abstract class LegalPage extends Div implements HasDynamicTitle {

    private final String prefix;

    protected LegalPage(String prefix, int sectionCount) {
        this.prefix = prefix;
        addClassNames("section", "legal-page");

        H1 title = new H1(getTranslation(prefix + ".title"));
        title.addClassName("section-title");
        Paragraph notice = new Paragraph(getTranslation("legal.notice"));
        notice.addClassName("legal-notice");
        add(title, notice);

        for (int i = 1; i <= sectionCount; i++) {
            add(new H2(getTranslation(prefix + "." + i + ".heading")));
            for (String paragraph : getTranslation(prefix + "." + i + ".text").split("\\|")) {
                add(new Paragraph(paragraph.trim()));
            }
        }
    }

    @Override
    public String getPageTitle() {
        return getTranslation(prefix + ".title") + " | Torben Photography";
    }
}
