package de.torbenphotography.views;

import de.torbenphotography.images.ImageService;
import de.torbenphotography.images.PhotoCategory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.card.CardVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Torben Photography")
public class HomeView extends Div {

    private final ImageService imageService;

    public HomeView(ImageService imageService) {
        this.imageService = imageService;
        add(createHero(), createCategories(), createSteps());
    }

    private Component createHero() {
        H1 title = new H1("Willkommen bei Torben Photography");
        Paragraph lead = new Paragraph(
                "Professionelle Fotografie für jeden Anlass – von Porträts über Autos bis hin zu Hochzeiten.");

        Button request = new Button("Anfrage stellen",
                event -> getUI().ifPresent(ui -> ui.navigate(RequestFormView.class)));
        request.addThemeVariants(ButtonVariant.PRIMARY);
        Button explore = new Button("Leistungen ansehen",
                event -> getElement().executeJs("document.getElementById('leistungen').scrollIntoView({behavior: 'smooth'})"));

        Div actions = new Div(request, explore);
        actions.addClassName("hero-actions");

        Div content = new Div(title, lead, actions);
        content.addClassNames("section", "hero-content");

        Section hero = new Section(content);
        hero.addClassName("hero");
        imageService.findFirst(ImageService.START_PAGE_BACKGROUND)
                .ifPresent(url -> hero.getStyle().set("background-image", "url('" + url + "')"));
        return hero;
    }

    private Component createCategories() {
        Div grid = new Div();
        grid.addClassName("card-grid");
        for (PhotoCategory category : PhotoCategory.values()) {
            grid.add(createCategoryCard(category));
        }

        H2 title = new H2("Leistungen");
        title.addClassName("section-title");
        Paragraph lead = new Paragraph("Wähle eine Kategorie und lass dich von Beispielen inspirieren.");
        lead.addClassName("section-lead");

        Section section = new Section(title, lead, grid);
        section.setId("leistungen");
        section.addClassName("section");
        return section;
    }

    private Card createCategoryCard(PhotoCategory category) {
        String title = CategoryViews.title(category);
        Card card = new Card();
        card.addThemeVariants(CardVariant.OUTLINED, CardVariant.COVER_MEDIA);
        imageService.findFirst(category.cover())
                .ifPresent(url -> card.setMedia(new Image(url, title)));
        card.setTitle(title);
        card.add(new Paragraph(CategoryViews.description(category)));

        Button more = new Button("Mehr erfahren",
                event -> getUI().ifPresent(ui -> ui.navigate(CategoryViews.viewClass(category))));
        card.addToFooter(more);
        return card;
    }

    private Component createSteps() {
        Div steps = new Div(
                createStep(1, "Anfrage stellen", "Wähle eine Kategorie und schick mir deinen Wunschtermin."),
                createStep(2, "Abstimmen", "Wir besprechen Ort, Stil und Ablauf des Shootings."),
                createStep(3, "Shooting", "Wir setzen deine Ideen um – die Bilder bekommst du bearbeitet."));
        steps.addClassName("steps");

        H2 title = new H2("So läuft's ab");
        title.addClassName("section-title");
        Paragraph lead = new Paragraph("In drei Schritten zu deinen Bildern.");
        lead.addClassName("section-lead");

        Section section = new Section(title, lead, steps);
        section.addClassName("section");
        return section;
    }

    private Card createStep(int number, String title, String text) {
        Span badge = new Span(String.valueOf(number));
        badge.addClassName("step-number");

        Card card = new Card();
        card.addThemeVariants(CardVariant.OUTLINED);
        card.setHeaderPrefix(badge);
        card.setTitle(title);
        card.add(new Paragraph(text));
        return card;
    }
}
