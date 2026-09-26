package org.horizonteurbano.system.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.models.PropertyType;
import org.horizonteurbano.system.models.State;
import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.repositories.PropertyRepository;
import org.horizonteurbano.system.repositories.PropertyTypeRepository;
import org.horizonteurbano.system.repositories.StateRepository;
import org.horizonteurbano.system.service.UserSession;
import org.horizonteurbano.system.utils.AlertInformation;
import org.horizonteurbano.system.utils.ViewFactory;

public class DashboardController implements Initializable {

    @FXML private Label lblUser;
    
    @FXML private TextField txtCode;
    @FXML private ComboBox<String> cmbType;
    @FXML private TextField txtArea;
    
    @FXML private TextField txtAddress;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private TextField txtPrice;
    
    @FXML private TextArea txtDescription;
    
    @FXML private Button btnClear;
    @FXML private Button btnCancel;
    @FXML private Button btnSave;
    @FXML private Button btnManageUsers;
    
    @FXML private ListView<String> listRecords;

    private PropertyRepository propertyRepository;
    private PropertyTypeRepository propertyTypeRepository;
    private StateRepository stateRepository;
    private AlertInformation alertInformation;
    private ViewFactory viewFactory;
    private UserSession session;

    public DashboardController() {
        this.propertyRepository = new PropertyRepository();
        this.propertyTypeRepository = new PropertyTypeRepository();
        this.stateRepository = new StateRepository();
        this.alertInformation = new AlertInformation(); 
        this.viewFactory = ViewFactory.getInstance();
        this.session = UserSession.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarTiposDePropiedad();
        cargarEstadosDePropiedad();
        cargarDatosUsuario();
    }
    
    private void cargarDatosUsuario() {
        if (session.isLoggedIn()) {
            User loggedUser = session.getLoggedUser();
            lblUser.setText("Usuario: " + loggedUser.getName() + " " + loggedUser.getLastName());
            
            if (loggedUser.getRol() == null || loggedUser.getRol().getIdRole() != 1) {
                if (btnManageUsers != null) {
                    btnManageUsers.setVisible(false);
                    btnManageUsers.setManaged(false);
                }
            }
        }
    }

    private void cargarTiposDePropiedad() {
        cmbType.getItems().clear();
        List<PropertyType> types = propertyTypeRepository.getAllPropertyTypes();
        for (PropertyType type : types) {
            cmbType.getItems().add(type.getNameType());
        }
    }

    private void cargarEstadosDePropiedad() {
        cmbStatus.getItems().clear();
        List<State> states = stateRepository.getAllStates();
        for (State state : states) {
            cmbStatus.getItems().add(state.getNameState());
        }
    }

    @FXML
    public void actionSaveProperty(ActionEvent event) {
        try {
            if (txtCode.getText().isEmpty() || txtAddress.getText().isEmpty() || 
                txtArea.getText().isEmpty() || txtPrice.getText().isEmpty() || 
                cmbType.getSelectionModel().isEmpty() || cmbStatus.getSelectionModel().isEmpty()) {
                
                alertInformation.viewAlert(2, "Datos Incompletos", "Por favor, llena todos los campos obligatorios del formulario.", null);
                return;
            }

            Property newProperty = new Property();
            newProperty.setInternalCode(txtCode.getText());
            newProperty.setAddress(txtAddress.getText());
            newProperty.setArea(Double.parseDouble(txtArea.getText()));
            newProperty.setPrice(Double.parseDouble(txtPrice.getText()));

            PropertyType type = new PropertyType();
            type.setIdType(cmbType.getSelectionModel().getSelectedIndex() + 1); 
            newProperty.setType(type);
            
            State state = new State();
            state.setIdState(cmbStatus.getSelectionModel().getSelectedIndex() + 1);
            newProperty.setState(state);
            
            newProperty.setActive(true);
            
            if (session.isLoggedIn()) {
                newProperty.setIdUser(session.getLoggedUser().getIdUser());
            } else {
                newProperty.setIdUser("SISTEMA"); 
            }

            if (propertyRepository.saveProperty(newProperty)) {
                alertInformation.viewAlert(1, "Guardado Exitoso", "La propiedad se ha registrado correctamente en el inventario.", null);
                listRecords.getItems().add(0, "✅ Guardado: " + newProperty.getInternalCode() + " - " + newProperty.getAddress());
                actionClear(null);
            } else {
                alertInformation.viewAlert(3, "Error de Registro", "Ocurrió un problema al guardar en la base de datos.", null);
            }

        } catch (NumberFormatException e) {
            alertInformation.viewAlert(2, "Error de Formato", "El Área y el Precio deben contener únicamente números.", null);
        }
    }

    @FXML
    public void actionClear(ActionEvent event) {
        txtCode.clear();
        txtAddress.clear();
        txtArea.clear();
        txtPrice.clear();
        txtDescription.clear();
        cmbType.getSelectionModel().clearSelection();
        cmbStatus.getSelectionModel().clearSelection();
    }

    @FXML
    public void actionCancel(ActionEvent event) {
        actionClear(event);
        alertInformation.viewAlert(1, "Cancelado", "Operación cancelada. El formulario ha sido limpiado.", null);
    }
    
    @FXML
    public void actionManageUsers(ActionEvent event) {
        viewFactory.showUsersWindow();
    }
}