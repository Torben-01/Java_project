package com.example.demo.views;

import com.example.demo.services.RequestService;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;

@Route("portrait")
public class PortraitView extends CommonComponents {

    public PortraitView(RequestService requestService) {
        super(requestService);

        FormLayout formLayout = new FormLayout();

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("Portraitfotografie");
        Paragraph underTitle = new Paragraph("Dein Gesicht im Fokus – professionelle Portraitaufnahmen für jeden Anlass.");

        add(title, formLayout);
        add(underTitle, formLayout);
        super.createCommonLayout(formLayout, "Portraitfotografie");
    }
}
