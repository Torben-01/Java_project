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
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
@PageTitle("Torben Photography")
public class HomeView extends Div {

    private final ImageService imageService;

    public HomeView(ImageService imageService) {
        this.imageService = imageService;
        add(createHero(), createCategories(), createSteps());
    }

    private Component createHero() {
        H1 title = new H1(getTranslation("home.title"));
        Paragraph lead = new Paragraph(getTranslation("home.lead"));

        Button request = new Button(getTranslation("nav.request"),
                event -> getUI().ifPresent(ui -> ui.navigate(RequestFormView.class)));
        request.addThemeVariants(ButtonVariant.PRIMARY);
        Button explore = new Button(getTranslation("home.explore"),
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

        H2 title = new H2(getTranslation("home.services.title"));
        title.addClassName("section-title");
        Paragraph lead = new Paragraph(getTranslation("home.services.lead"));
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

        Button more = new Button(getTranslation("home.more"),
                event -> getUI().ifPresent(ui -> ui.navigate(CategoryViews.viewClass(category))));
        card.addToFooter(more);
        return card;
    }

    private Component createSteps() {
        Div steps = new Div(
                createStep(1), createStep(2), createStep(3));
        steps.addClassName("steps");

        H2 title = new H2(getTranslation("home.steps.title"));
        title.addClassName("section-title");
        Paragraph lead = new Paragraph(getTranslation("home.steps.lead"));
        lead.addClassName("section-lead");

        Section section = new Section(title, lead, steps);
        section.addClassName("section");
        return section;
    }

    private Card createStep(int number) {
        Span badge = new Span(String.valueOf(number));
        badge.addClassName("step-number");

        Card card = new Card();
        card.addThemeVariants(CardVariant.OUTLINED);
        card.setHeaderPrefix(badge);
        card.setTitle(getTranslation("home.step" + number + ".title"));
        card.add(new Paragraph(getTranslation("home.step" + number + ".text")));
        return card;
    }
}
