package org.horizonteurbano.system.controller;

import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.models.PropertyType;
import org.horizonteurbano.system.models.State;
import org.horizonteurbano.system.repositories.PropertyRepository;
import org.horizonteurbano.system.repositories.PropertyTypeRepository;
import org.horizonteurbano.system.repositories.StateRepository;
import org.horizonteurbano.system.utils.AlertInformation;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PropertyController implements Initializable {

    @FXML
    private TextField txtInternalCode;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtArea;
    @FXML
    private TextField txtPrice;
    @FXML
    private ComboBox<String> cmbPropertyType;
    @FXML
    private ComboBox<String> cmbState;

    private PropertyRepository propertyRepository;
    private PropertyTypeRepository propertyTypeRepository;
    private StateRepository stateRepository;
    private AlertInformation alert;

    public PropertyController() {
        this.propertyRepository = new PropertyRepository();
        this.propertyTypeRepository = new PropertyTypeRepository();
        this.stateRepository = new StateRepository();
        this.alert = new AlertInformation();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPropertyTypes();
        loadPropertyStates();
    }

    private void loadPropertyTypes() {
        List<PropertyType> propertyTypeList = propertyTypeRepository.getAllPropertyTypes();
        for (PropertyType propertyType : propertyTypeList) {
            cmbPropertyType.getItems().add(propertyType.getNameType());
        }
    }

    private void loadPropertyStates() {
        List<State> stateList = stateRepository.getAllStates();
        for (State state : stateList) {
            cmbState.getItems().add(state.getNameState());
        }
    }

    @FXML
    public void buttonRegisterProperty(ActionEvent event) {
        try {
            Property newProperty = new Property();
            newProperty.setInternalCode(txtInternalCode.getText());
            newProperty.setAddress(txtAddress.getText());
            newProperty.setArea(Double.parseDouble(txtArea.getText()));
            newProperty.setPrice(Double.parseDouble(txtPrice.getText()));

            PropertyType type = new PropertyType();
            type.setIdType(cmbPropertyType.getSelectionModel().getSelectedIndex() + 1);
            newProperty.setType(type);

            State state = new State();
            state.setIdState(cmbState.getSelectionModel().getSelectedIndex() + 1);
            newProperty.setState(state);

            newProperty.setActive(true);

            //1 = SUCCESS
            //2 = ERROR
            //3 = ALERT
            if (propertyRepository.saveProperty(newProperty)) {
                alert.viewAlert(1, "Éxito", "Propiedad registrada en el inventario.", null);
                clearFields();
            } else {
                alert.viewAlert(3, "Error", "No se pudo guardar la propiedad en la base de datos.", null);
            }

        } catch (NumberFormatException e) {
            alert.viewAlert(2, "Alerta de Formato", "El área y el precio deben ser únicamente números.", null);
        }
    }

    private void clearFields() {
        txtInternalCode.clear();
        txtAddress.clear();
        txtArea.clear();
        txtPrice.clear();
        cmbPropertyType.getSelectionModel().clearSelection();
        cmbState.getSelectionModel().clearSelection();
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
