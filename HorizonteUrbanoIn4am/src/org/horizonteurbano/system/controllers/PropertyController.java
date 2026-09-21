package org.horizonteurbano.system.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.event.ActionEvent;

import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.models.PropertyType;
import org.horizonteurbano.system.models.State;
import org.horizonteurbano.system.repositories.PropertyRepository;
import org.horizonteurbano.system.repositories.PropertyTypeRepository;
import org.horizonteurbano.system.repositories.StateRepository;
import org.horizonteurbano.system.utils.AlertInformation;

public class PropertyController implements Initializable {

    @FXML
    private TextField textFieldInternalCode;
    @FXML
    private TextField textFieldAddress;
    @FXML
    private TextField textFieldArea;
    @FXML
    private TextField textFieldPrice;
    @FXML
    private ComboBox<String> comboBoxPropertyType;
    @FXML
    private ComboBox<String> comboBoxState;

    private PropertyRepository propertyRepository;
    private PropertyTypeRepository propertyTypeRepository;
    private StateRepository stateRepository;
    private AlertInformation alerta;

    public PropertyController() {
        this.propertyRepository = new PropertyRepository();
        this.propertyTypeRepository = new PropertyTypeRepository();
        this.stateRepository = new StateRepository();
        this.alerta = new AlertInformation();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarTiposDePropiedad();
        cargarEstadosDePropiedad();
    }

    private void cargarTiposDePropiedad() {
        List<PropertyType> propertyTypeList = propertyTypeRepository.getAllPropertyTypes();
        for (PropertyType propertyType : propertyTypeList) {
            comboBoxPropertyType.getItems().add(propertyType.getNameType());
        }
    }

    private void cargarEstadosDePropiedad() {
        List<State> stateList = stateRepository.getAllStates();
        for (State state : stateList) {
            comboBoxState.getItems().add(state.getNameState());
        }
    }

    @FXML
    public void buttonRegisterProperty(ActionEvent event) {
        try {
            Property newProperty = new Property();
            newProperty.setInternalCode(textFieldInternalCode.getText());
            newProperty.setAddress(textFieldAddress.getText());
            newProperty.setArea(Double.parseDouble(textFieldArea.getText()));
            newProperty.setPrice(Double.parseDouble(textFieldPrice.getText()));

            PropertyType type = new PropertyType();
            type.setIdType(comboBoxPropertyType.getSelectionModel().getSelectedIndex() + 1);
            newProperty.setType(type);

            State state = new State();
            state.setIdState(comboBoxState.getSelectionModel().getSelectedIndex() + 1);
            newProperty.setState(state);

            newProperty.setActive(true);

            if (propertyRepository.saveProperty(newProperty)) {
                alerta.viewAlert(1, "Éxito", "Propiedad registrada en el inventario.", null);
                limpiarCampos();
            } else {
                alerta.viewAlert(3, "Error", "No se pudo guardar la propiedad en la base de datos.", null);
            }

        } catch (NumberFormatException e) {
            alerta.viewAlert(2, "Alerta de Formato", "El área y el precio deben ser únicamente números.", null);
        }
    }

    private void limpiarCampos() {
        textFieldInternalCode.clear();
        textFieldAddress.clear();
        textFieldArea.clear();
        textFieldPrice.clear();
        comboBoxPropertyType.getSelectionModel().clearSelection();
        comboBoxState.getSelectionModel().clearSelection();
    }

    @FXML
    public void editProperty(ActionEvent event) {
    }

    @FXML
    public void changeStatus(ActionEvent event) {
    }

    @FXML
    public void deactivateProperty(ActionEvent event) {
    }

    @FXML
    public void searchProperty(ActionEvent event) {
    }

    @FXML
    public void exportPropertyBrochure(ActionEvent event) {
    }
}
