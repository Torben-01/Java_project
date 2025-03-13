package com.example.application.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("photography")
public class PhotographerView extends VerticalLayout {

    public PhotographerView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassName("photographer-view");

        Div backgroundDiv = setStyle();

        H1 title = new H1("Willkommen bei Torben Photography");
        title.addClassNames(LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Bottom.LARGE);

        Paragraph description = new Paragraph("Professionelle Fotografie für jeden Anlass – von Porträts über Autos bis hin zu Hochzeiten.");
        description.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.Bottom.LARGE);

        HorizontalLayout categoryLayout = new HorizontalLayout();
        categoryLayout.setWidth("80%");
        categoryLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        categoryLayout.addClassName("category-layout");

        categoryLayout.add(
            createCategoryCard("portrait.jpg", "Porträtfotografie", "portrait"),
            createCategoryCard("car.jpg", "Autofotografie", "car-photography"),
            createCategoryCard("wedding.jpg", "Hochzeitsfotografie", "wedding-photography")
        );

        backgroundDiv.add(title, description);
        add(backgroundDiv, categoryLayout);
    }
    /*
     * Creates a category card with an image, a title and a button
     * imagePath String name of the image file
     * title String title of the category
     * route String route to navigate to when the button is clicked 
     */
    private VerticalLayout createCategoryCard(String imagePath, String title, String route) {
        Image image = new Image(imagePath, title);
        image.setWidth("300px");
        image.setHeight("200px");
        image.getStyle().set("border-radius", "15px");
        image.addClassName("category-image");

        Button button = new Button(title, event -> 
            event.getSource().getUI().ifPresent(ui -> ui.navigate(route))
        );
        button.addClassName("category-button");

        VerticalLayout card = new VerticalLayout(image, button);
        card.setAlignItems(Alignment.CENTER);
        card.addClassName("category-card");

        return card;
    }
    /*
     * Sets the style of the background div
     */
    private Div setStyle() {
        Div backgroundDiv = new Div();
        backgroundDiv.getStyle().set("background-image", "url('background.jpg')");
        backgroundDiv.getStyle().set("background-size", "cover");
        backgroundDiv.getStyle().set("background-position", "center");
        backgroundDiv.getStyle().set("width", "100%");
        backgroundDiv.getStyle().set("height", "300%");
        backgroundDiv.getStyle().set("display", "flex");
        backgroundDiv.getStyle().set("flex-direction", "column");
        backgroundDiv.getStyle().set("align-items", "center");
        backgroundDiv.getStyle().set("justify-content", "center");
        backgroundDiv.getStyle().set("padding", "20px");
        backgroundDiv.getStyle().set("box-sizing", "border-box");
        backgroundDiv.getStyle().set("border-radius", "15px");
        return backgroundDiv;
    }
}
