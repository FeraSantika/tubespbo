package com.example.application.views.daftar_keluhan;

import com.example.application.data.entity.DaftarKeluhan;
import com.example.application.data.service.DaftarKeluhanService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Daftar_keluhan")
@Route(value = "daftar_keluhan/:daftarKeluhanID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "daftar_keluhan", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class Daftar_keluhanView extends Div implements BeforeEnterObserver {

    private final String DAFTARKELUHAN_ID = "daftarKeluhanID";
    private final String DAFTARKELUHAN_EDIT_ROUTE_TEMPLATE = "daftar_keluhan/%s/edit";

    private final Grid<DaftarKeluhan> grid = new Grid<>(DaftarKeluhan.class, false);

    private TextField keluhan;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<DaftarKeluhan> binder;

    private DaftarKeluhan daftarKeluhan;

    private final DaftarKeluhanService daftarKeluhanService;

    public Daftar_keluhanView(DaftarKeluhanService daftarKeluhanService) {
        this.daftarKeluhanService = daftarKeluhanService;
        addClassNames("daftarkeluhan-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("keluhan").setAutoWidth(true);
        grid.setItems(query -> daftarKeluhanService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(DAFTARKELUHAN_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(Daftar_keluhanView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(DaftarKeluhan.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.daftarKeluhan == null) {
                    this.daftarKeluhan = new DaftarKeluhan();
                }
                binder.writeBean(this.daftarKeluhan);
                daftarKeluhanService.update(this.daftarKeluhan);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(Daftar_keluhanView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> daftarKeluhanId = event.getRouteParameters().get(DAFTARKELUHAN_ID).map(Long::parseLong);
        if (daftarKeluhanId.isPresent()) {
            Optional<DaftarKeluhan> daftarKeluhanFromBackend = daftarKeluhanService.get(daftarKeluhanId.get());
            if (daftarKeluhanFromBackend.isPresent()) {
                populateForm(daftarKeluhanFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested daftarKeluhan was not found, ID = %s", daftarKeluhanId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(Daftar_keluhanView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        keluhan = new TextField("Keluhan");
        formLayout.add(keluhan);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(DaftarKeluhan value) {
        this.daftarKeluhan = value;
        binder.readBean(this.daftarKeluhan);

    }
}
