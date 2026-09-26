package org.horizonteurbano.system.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;

import org.horizonteurbano.system.models.*;
import org.horizonteurbano.system.repositories.*;
import org.horizonteurbano.system.service.BrochureService;
import org.horizonteurbano.system.utils.AlertInformation;
import org.horizonteurbano.system.utils.ViewFactory;

public class SearchPropertyController implements Initializable {

    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cmbType;
    @FXML
    private ComboBox<String> cmbStatus;
    @FXML
    private TextField txtPriceMin;
    @FXML
    private TextField txtPriceMax;

    @FXML
    private TableView<Property> tblProperties;
    @FXML
    private TableColumn<Property, String> colCode;
    @FXML
    private TableColumn<Property, String> colAddress;
    @FXML
    private TableColumn<Property, String> colType;
    @FXML
    private TableColumn<Property, Double> colArea;
    @FXML
    private TableColumn<Property, Double> colPrice;
    @FXML
    private TableColumn<Property, String> colStatus;

    @FXML
    private Label lblResults;

    private PropertyRepository propertyRepository;
    private PropertyTypeRepository propertyTypeRepository;
    private StateRepository stateRepository;
    private AlertInformation alertInformation;

    public SearchPropertyController() {
        this.propertyRepository = new PropertyRepository();
        this.propertyTypeRepository = new PropertyTypeRepository();
        this.stateRepository = new StateRepository();
        this.alertInformation = new AlertInformation();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();
        cargarComboBoxes();
        cargarPropiedades();
    }

    private void configurarColumnas() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("internalCode"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        colType.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getType() != null ? cellData.getValue().getType().getNameType() : ""));
        colStatus.setCellValueFactory(cellData
                -> new SimpleStringProperty(cellData.getValue().getState() != null ? cellData.getValue().getState().getNameState() : ""));
    }

    private void cargarComboBoxes() {
        List<PropertyType> types = propertyTypeRepository.getAllPropertyTypes();
        for (PropertyType type : types) {
            cmbType.getItems().add(type.getNameType());
        }

        List<State> states = stateRepository.getAllStates();
        for (State state : states) {
            cmbStatus.getItems().add(state.getNameState());
        }
    }

    private void cargarPropiedades() {
        List<Property> propertiesList = propertyRepository.getAllActiveProperties();
        ObservableList<Property> observableList = FXCollections.observableArrayList(propertiesList);
        tblProperties.setItems(observableList);
        lblResults.setText("Resultados: " + propertiesList.size());
    }

    @FXML
    public void actionChangeStatus(ActionEvent event) {
        Property selectedProperty = tblProperties.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            alertInformation.viewAlert(2, "Selección Requerida", "Por favor, selecciona una propiedad de la tabla primero.", null);
            return;
        }

        List<State> states = stateRepository.getAllStates();
        List<String> stateNames = new ArrayList<>();
        Map<String, Integer> stateMap = new HashMap<>();

        for (State state : states) {
            stateNames.add(state.getNameState());
            stateMap.put(state.getNameState(), state.getIdState());
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(stateNames.get(0), stateNames);
        dialog.setTitle("Cambiar Estado");
        dialog.setHeaderText("Propiedad seleccionada: " + selectedProperty.getInternalCode());
        dialog.setContentText("Selecciona el nuevo estado comercial:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            int newIdState = stateMap.get(result.get());
            if (propertyRepository.changeStatus(selectedProperty.getInternalCode(), newIdState)) {
                alertInformation.viewAlert(1, "Éxito", "El estado se actualizó correctamente.", null);
                cargarPropiedades(); // Recargar tabla
            } else {
                alertInformation.viewAlert(3, "Error", "No se pudo actualizar el estado en la base de datos.", null);
            }
        }
    }

    @FXML
    public void actionDelete(ActionEvent event) {
        Property selectedProperty = tblProperties.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            alertInformation.viewAlert(2, "Selección Requerida", "Por favor, selecciona una propiedad de la tabla.", null);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Baja");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Estás seguro de que deseas dar de baja " + selectedProperty.getInternalCode() + "? (Borrado lógico)");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (propertyRepository.deactivateProperty(selectedProperty.getInternalCode())) {
                alertInformation.viewAlert(1, "Éxito", "Propiedad dada de baja.", null);
                cargarPropiedades();
            } else {
                alertInformation.viewAlert(3, "Error", "No se pudo dar de baja la propiedad.", null);
            }
        }
    }

    @FXML
    public void actionEdit(ActionEvent event) {
        Property selectedProperty = tblProperties.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            alertInformation.viewAlert(2, "Selección Requerida", "Por favor, selecciona una propiedad para editar.", null);
            return;
        }

        ViewFactory.getInstance().showEditPropertyWindow(selectedProperty);
    }

    @FXML
    public void actionExportPDF(ActionEvent event) {
        Property selectedProperty = tblProperties.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            alertInformation.viewAlert(2, "Selección Requerida", "Por favor, selecciona una propiedad de la tabla para exportar su ficha.", null);
            return;
        }

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar Brochure PDF");
        fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Documento PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Ficha_" + selectedProperty.getInternalCode() + ".pdf");

        java.io.File file = fileChooser.showSaveDialog(tblProperties.getScene().getWindow());

        if (file != null) {
            BrochureService brochureService = new BrochureService();
            if (brochureService.exportBrochure(selectedProperty, file)) {
                alertInformation.viewAlert(1, "Exportación Exitosa", "El PDF se generó correctamente en tu equipo.", null);
            } else {
                alertInformation.viewAlert(3, "Error", "No se pudo generar el archivo PDF.", null);
            }
        }
    }
}
