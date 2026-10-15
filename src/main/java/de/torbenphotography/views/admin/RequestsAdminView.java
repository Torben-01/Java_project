package de.torbenphotography.views.admin;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import de.torbenphotography.entities.Request;
import de.torbenphotography.entities.RequestStatus;
import de.torbenphotography.images.PhotoCategory;
import de.torbenphotography.services.RequestService;
import de.torbenphotography.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.badge.Badge;
import com.vaadin.flow.component.badge.BadgeVariant;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

/*
 * Overview of all incoming requests for the photographer: filter, mark as done, delete
 */
@Route(value = "admin/anfragen", layout = MainLayout.class)
@RolesAllowed(RequestService.ADMIN_ROLE)
public class RequestsAdminView extends Div implements HasDynamicTitle {

    private final RequestService requestService;
    private final Grid<Request> grid = new Grid<>(Request.class, false);
    private final Select<PhotoCategory> categoryFilter = new Select<>();
    private final Select<RequestStatus> statusFilter = new Select<>();
    private final TextField searchFilter = new TextField();

    public RequestsAdminView(RequestService requestService) {
        this.requestService = requestService;
        addClassNames("section", "admin-view");

        H1 title = new H1(getTranslation("admin.title"));
        title.addClassName("section-title");
        Paragraph lead = new Paragraph(getTranslation("admin.lead"));
        lead.addClassName("section-lead");

        add(title, lead, createFilters(), createGrid());
        refresh();
    }

    @Override
    public String getPageTitle() {
        return getTranslation("admin.title") + " | Torben Photography";
    }

    private Component createFilters() {
        categoryFilter.setLabel(getTranslation("admin.filter.category"));
        categoryFilter.setItems(PhotoCategory.values());
        categoryFilter.setItemLabelGenerator(category -> category == null ? "" : getTranslation("category." + category.key() + ".title"));
        categoryFilter.setEmptySelectionAllowed(true);
        categoryFilter.setEmptySelectionCaption(getTranslation("admin.filter.all"));
        categoryFilter.setPlaceholder(getTranslation("admin.filter.all"));
        categoryFilter.addValueChangeListener(event -> refresh());

        statusFilter.setLabel(getTranslation("admin.filter.status"));
        statusFilter.setItems(RequestStatus.values());
        statusFilter.setItemLabelGenerator(status -> status == null ? "" : statusLabel(status));
        statusFilter.setEmptySelectionAllowed(true);
        statusFilter.setEmptySelectionCaption(getTranslation("admin.filter.all"));
        statusFilter.setPlaceholder(getTranslation("admin.filter.all"));
        statusFilter.setValue(RequestStatus.OFFEN);
        statusFilter.addValueChangeListener(event -> refresh());

        searchFilter.setLabel(getTranslation("admin.filter.search"));
        searchFilter.setPlaceholder(getTranslation("admin.filter.search.placeholder"));
        searchFilter.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchFilter.setClearButtonVisible(true);
        searchFilter.setValueChangeMode(ValueChangeMode.LAZY);
        searchFilter.addValueChangeListener(event -> refresh());

        Div filters = new Div(categoryFilter, statusFilter, searchFilter);
        filters.addClassName("admin-filters");
        return filters;
    }

    private Component createGrid() {
        DateTimeFormatter dateTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(getLocale());
        DateTimeFormatter date = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

        grid.addColumn(request -> request.getCreatedAt().format(dateTime))
                .setHeader(getTranslation("admin.column.received")).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(request -> getTranslation("category." + request.getCategory().key() + ".title"))
                .setHeader(getTranslation("admin.column.category")).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(request -> request.getFirstName() + " " + request.getLastName())
                .setHeader(getTranslation("admin.column.name")).setAutoWidth(true);
        grid.addColumn(Request::getEmail).setHeader(getTranslation("admin.column.email")).setAutoWidth(true);
        grid.addColumn(request -> request.getDate().format(date))
                .setHeader(getTranslation("admin.column.date")).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(new ComponentRenderer<>(this::createStatusBadge))
                .setHeader(getTranslation("admin.column.status")).setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(new ComponentRenderer<>(this::createActions))
                .setHeader(getTranslation("admin.column.actions")).setAutoWidth(true).setFlexGrow(0);

        // the comment is shown when a row is clicked
        grid.setItemDetailsRenderer(new ComponentRenderer<>(request -> new Paragraph(
                request.getComment() == null || request.getComment().isBlank()
                        ? getTranslation("admin.comment.none")
                        : request.getComment())));
        grid.addThemeVariants(GridVariant.ROW_STRIPES);
        grid.setEmptyStateText(getTranslation("admin.empty"));
        grid.setAllRowsVisible(true);
        return grid;
    }

    private Component createStatusBadge(Request request) {
        Badge badge = new Badge(statusLabel(request.getStatus()));
        badge.addThemeVariants(request.getStatus() == RequestStatus.OFFEN ? BadgeVariant.WARNING : BadgeVariant.SUCCESS);
        return badge;
    }

    private Component createActions(Request request) {
        boolean open = request.getStatus() == RequestStatus.OFFEN;
        Button toggle = new Button(open ? VaadinIcon.CHECK.create() : VaadinIcon.ARROW_BACKWARD.create(), event -> {
            requestService.changeStatus(request, open ? RequestStatus.ERLEDIGT : RequestStatus.OFFEN);
            refresh();
        });
        toggle.addThemeVariants(ButtonVariant.TERTIARY);
        toggle.setTooltipText(getTranslation(open ? "admin.action.done" : "admin.action.reopen"));
        toggle.setAriaLabel(toggle.getTooltip().getText());

        Button delete = new Button(VaadinIcon.TRASH.create(), event -> confirmDelete(request));
        delete.addThemeVariants(ButtonVariant.TERTIARY, ButtonVariant.ERROR);
        delete.setTooltipText(getTranslation("admin.action.delete"));
        delete.setAriaLabel(getTranslation("admin.action.delete"));

        return new Div(toggle, delete);
    }

    private void confirmDelete(Request request) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader(getTranslation("admin.delete.title"));
        dialog.setText(getTranslation("admin.delete.text", request.getFirstName() + " " + request.getLastName()));
        dialog.setCancelable(true);
        dialog.setCancelText(getTranslation("admin.delete.cancel"));
        dialog.setConfirmText(getTranslation("admin.delete.confirm"));
        dialog.setConfirmButtonTheme("error primary");
        dialog.addConfirmListener(event -> {
            requestService.delete(request);
            refresh();
        });
        dialog.open();
    }

    private String statusLabel(RequestStatus status) {
        return getTranslation("admin.status." + status.name().toLowerCase());
    }

    private void refresh() {
        grid.setItems(requestService.find(categoryFilter.getValue(), statusFilter.getValue(), searchFilter.getValue()));
    }
}
