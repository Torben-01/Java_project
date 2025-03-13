package com.example.application.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("wedding-photography")
public class WeddingPhotographyView extends VerticalLayout {

    public WeddingPhotographyView() {
        setAlignItems(Alignment.CENTER);
        add(new H1("Hochzeitsfotografie"));
        add(new Paragraph("Unvergessliche Momente stilvoll festgehalten – dein perfekter Hochzeitstag in Bildern."));
        add(new Button("Zurück", event -> event.getSource().getUI().ifPresent(ui -> ui.navigate("photography"))));
    }
}
