package com.example.demo.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.router.Route;

@Route("wedding-photography")
public class WeddingPhotographyView extends CommonComponents {

    public WeddingPhotographyView() {
        FormLayout formLayout = new FormLayout();

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("Hochzeitsfotografie");
        Paragraph underTitle = new Paragraph("Unvergessliche Momente für die Ewigkeit festhalten.");

        add(title, formLayout);
        add(underTitle, formLayout);
        super.createCommonLayout(formLayout);
    }
}
