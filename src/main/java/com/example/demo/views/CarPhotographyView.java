package com.example.demo.views;

import com.example.demo.services.RequestService;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("car-photography")
@PermitAll
public class CarPhotographyView extends CommonComponents {

    public CarPhotographyView(RequestService requestService) {
        super(requestService);

        FormLayout formLayout = new FormLayout();

        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        H1 title = new H1("Autofotografie");
        Paragraph underTitle = new Paragraph("Perfekte Inszenierung deines Autos – egal ob Sportwagen oder Oldtimer.");

        add(title, formLayout);
        add(underTitle, formLayout);
        super.createCommonLayout(formLayout, "Autofotografie");
    }
}
