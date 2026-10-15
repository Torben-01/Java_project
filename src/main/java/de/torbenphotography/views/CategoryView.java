package de.torbenphotography.views;

import java.util.List;

import de.torbenphotography.images.ImageService;
import de.torbenphotography.images.PhotoCategory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.icon.VaadinIcon;

/*
 * Page of one photo category: introduction with vertical cover, button to the request form
 * and a gallery of the example pictures
 */
public abstract class CategoryView extends Div {

    private final PhotoCategory category;

    protected CategoryView(ImageService imageService, PhotoCategory category) {
        this.category = category;
        addClassName("category-view");
        add(createIntro(imageService));

        List<String> examples = imageService.findAll(category.examples());
        if (!examples.isEmpty()) {
            add(createGallery(examples));
        }
    }

    private Component createIntro(ImageService imageService) {
        H1 title = new H1(CategoryViews.headline(category));
        Paragraph lead = new Paragraph(CategoryViews.description(category));
        lead.addClassName("section-lead");

        Button request = new Button("Jetzt anfragen", VaadinIcon.ENVELOPE.create(),
                event -> getUI().ifPresent(ui -> ui.navigate(RequestFormView.class, category.key())));
        request.addThemeVariants(ButtonVariant.PRIMARY);

        Div text = new Div(title, lead, request);
        text.addClassName("category-intro-text");

        Section intro = new Section(text);
        intro.addClassNames("section", "category-intro");
        imageService.findFirst(category.coverVertical()).ifPresent(url -> {
            Image cover = new Image(url, CategoryViews.title(category));
            cover.addClassName("category-cover");
            intro.add(cover);
        });
        return intro;
    }

    private Component createGallery(List<String> urls) {
        Div gallery = new Div();
        gallery.addClassName("gallery");
        for (String url : urls) {
            Image image = new Image(url, CategoryViews.title(category));
            image.addClickListener(event -> openImage(url));
            gallery.add(image);
        }

        H2 title = new H2("Beispiele");
        title.addClassName("section-title");
        Section section = new Section(title, gallery);
        section.addClassName("section");
        return section;
    }

    private void openImage(String url) {
        Dialog dialog = new Dialog(new Image(url, CategoryViews.title(category)));
        dialog.addClassName("image-dialog");
        dialog.setCloseOnOutsideClick(true);
        dialog.open();
    }
}
