package com.example.application.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("car-photography")
public class CarPhotographyView extends VerticalLayout {

    public CarPhotographyView() {
        setAlignItems(Alignment.CENTER);
        add(new H1("Autofotografie"));
        add(new Paragraph("Perfekte Inszenierung deines Autos – egal ob Sportwagen oder Oldtimer."));
        add(new Button("Zurück", event -> event.getSource().getUI().ifPresent(ui -> ui.navigate("photography"))));
    }
}
