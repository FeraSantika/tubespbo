package com.example.application.views.laporan;

import com.example.application.data.entity.UserSadari;
import com.example.application.data.service.UserSadariService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
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

@PageTitle("Laporan")
@Route(value = "laporan/:userSadariID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class LaporanView extends Div implements BeforeEnterObserver {

    private final String USERSADARI_ID = "userSadariID";
    private final String USERSADARI_EDIT_ROUTE_TEMPLATE = "laporan/%s/edit";

    private final Grid<UserSadari> grid = new Grid<>(UserSadari.class, false);

    private TextField nik;
    private TextField nama;
    private TextField email;
    private TextField nomor_HP;
    private DatePicker tanggal_Lahir;
    private TextField pendidikan;
    private TextField faskes_Terdekat;
    private TextField alamat;
    private Checkbox status;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<UserSadari> binder;

    private UserSadari userSadari;

    private final UserSadariService userSadariService;

    public LaporanView(UserSadariService userSadariService) {
        this.userSadariService = userSadariService;
        addClassNames("laporan-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("nik").setAutoWidth(true);
        grid.addColumn("nama").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("nomor_HP").setAutoWidth(true);
        grid.addColumn("tanggal_Lahir").setAutoWidth(true);
        grid.addColumn("pendidikan").setAutoWidth(true);
        grid.addColumn("faskes_Terdekat").setAutoWidth(true);
        grid.addColumn("alamat").setAutoWidth(true);
        LitRenderer<UserSadari> statusRenderer = LitRenderer.<UserSadari>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", status -> status.isStatus() ? "check" : "minus").withProperty("color",
                        status -> status.isStatus()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(statusRenderer).setHeader("Status").setAutoWidth(true);

        grid.setItems(query -> userSadariService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(USERSADARI_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(LaporanView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(UserSadari.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.userSadari == null) {
                    this.userSadari = new UserSadari();
                }
                binder.writeBean(this.userSadari);
                userSadariService.update(this.userSadari);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(LaporanView.class);
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
        Optional<Long> userSadariId = event.getRouteParameters().get(USERSADARI_ID).map(Long::parseLong);
        if (userSadariId.isPresent()) {
            Optional<UserSadari> userSadariFromBackend = userSadariService.get(userSadariId.get());
            if (userSadariFromBackend.isPresent()) {
                populateForm(userSadariFromBackend.get());
            } else {
                Notification.show(String.format("The requested userSadari was not found, ID = %s", userSadariId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(LaporanView.class);
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
        nik = new TextField("Nik");
        nama = new TextField("Nama");
        email = new TextField("Email");
        nomor_HP = new TextField("Nomor_ HP");
        tanggal_Lahir = new DatePicker("Tanggal_ Lahir");
        pendidikan = new TextField("Pendidikan");
        faskes_Terdekat = new TextField("Faskes_ Terdekat");
        alamat = new TextField("Alamat");
        status = new Checkbox("Status");
        formLayout.add(nik, nama, email, nomor_HP, tanggal_Lahir, pendidikan, faskes_Terdekat, alamat, status);

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

    private void populateForm(UserSadari value) {
        this.userSadari = value;
        binder.readBean(this.userSadari);

    }
}
