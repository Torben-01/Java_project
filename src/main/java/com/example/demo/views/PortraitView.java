package com.example.demo.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("portrait")
public class PortraitView extends VerticalLayout {

    public PortraitView() {
        setAlignItems(Alignment.CENTER);
        add(new H1("Porträtfotografie"));
        add(new Paragraph("Erhalte professionelle Porträtfotos mit einzigartiger Atmosphäre."));
        add(new Button("Zurück", event -> event.getSource().getUI().ifPresent(ui -> ui.navigate(""))));
    }
}
