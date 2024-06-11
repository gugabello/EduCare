package com.example.application.views.atendimentos;

import com.example.application.data.Atendimentos;
import com.example.application.services.AtendimentosService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
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
import jakarta.annotation.security.PermitAll;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("Atendimentos")
@Route(value = "/:atendimentosID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class AtendimentosView extends Div implements BeforeEnterObserver {

    private final String ATENDIMENTOS_ID = "atendimentosID";
    private final String ATENDIMENTOS_EDIT_ROUTE_TEMPLATE = "/%s/edit";

    private final Grid<Atendimentos> grid = new Grid<>(Atendimentos.class, false);

    private TextField nome;
    private TextField periodo;
    private TextField curso;
    private DatePicker dataDaUltimaConsulta;
    private TextField email;
    private TextField telefone;
    private TextField observacoes;
    private TextField links;


    private final Button cancel = new Button("Cancelar");
    private final Button save = new Button("Salvar");
    private final Button delete = new Button("Deletar");


    private final BeanValidationBinder<Atendimentos> binder;

    private Atendimentos atendimentos;

    private final AtendimentosService atendimentosService;

    public AtendimentosView(AtendimentosService atendimentosService) {
        this.atendimentosService = atendimentosService;
        addClassNames("atendimentos-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addClassName("grid-atendimentos");
        grid.addColumn("nome").setAutoWidth(true);
        grid.addColumn("periodo").setAutoWidth(true);
        grid.addColumn("curso").setAutoWidth(true);
        grid.addColumn("dataDaUltimaConsulta").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("telefone").setAutoWidth(true);
        grid.addColumn("observacoes").setAutoWidth(true);
        grid.addColumn("links").setAutoWidth(true);
        grid.setItems(query -> atendimentosService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ATENDIMENTOS_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AtendimentosView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Atendimentos.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.atendimentos == null) {
                    this.atendimentos = new Atendimentos();
                }
                binder.writeBean(this.atendimentos);
                atendimentosService.update(this.atendimentos);
                clearForm();
                refreshGrid();
                Notification.show("\n" +
                        "Dados atualizados");
                UI.getCurrent().navigate(AtendimentosView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Erro ao atualizar os dados. Outra pessoa atualizou o registro enquanto você fazia alterações.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Falha ao atualizar os dados. Verifique novamente se todos os valores são válidos");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> atendimentosId = event.getRouteParameters().get(ATENDIMENTOS_ID).map(Long::parseLong);
        if (atendimentosId.isPresent()) {
            Optional<Atendimentos> atendimentosFromBackend = atendimentosService.get(atendimentosId.get());
            if (atendimentosFromBackend.isPresent()) {
                populateForm(atendimentosFromBackend.get());
            } else {
                Notification.show(
                        String.format("\n" +
                                "Os atendimentos solicitados não foram encontrados, ID = %s", atendimentosId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(AtendimentosView.class);
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
        nome = new TextField("Nome");
        periodo = new TextField("Periodo");
        curso = new TextField("Curso");
        dataDaUltimaConsulta = new DatePicker("Data Da Ultima Consulta");
        email = new TextField("Email");
        telefone = new TextField("Telefone");
        observacoes = new TextField("Observações");
        links = new TextField("Links");
        formLayout.add(nome, periodo, curso, dataDaUltimaConsulta, email, telefone, observacoes, links);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cancel.addClassName("cancel");
        save.addClassName("save");
        delete.addClassName("delete");
        buttonLayout.add(save, cancel, delete); // Add the new "Delete" button
        editorLayoutDiv.add(buttonLayout);
        delete.addClickListener(e -> deletePaciente());
    }

    private void deletePaciente() {
        Atendimentos selectedPaciente = grid.asSingleSelect().getValue();
        if (selectedPaciente != null) {
            atendimentosService.delete(selectedPaciente.getId());
            refreshGrid();
            clearForm();
            Notification.show("Paciente deletado");
        } else {
            Notification.show("Nenhum paciente selecionado para exclusão");
        }
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.addClassName("grid-wrapper");
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

    private void populateForm(Atendimentos value) {
        this.atendimentos = value;
        binder.readBean(this.atendimentos);

    }
}
