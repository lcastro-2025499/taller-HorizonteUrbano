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
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

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
    @FXML private VBox formPanel;
    @FXML private TextField txtCode;
    @FXML private ComboBox<PropertyType> cmbType;
    @FXML private TextField txtArea;
    @FXML private TextField txtAddress;
    @FXML private ComboBox<State> cmbStatus;
    @FXML private TextField txtPrice;
    @FXML private TextArea txtDescription;
    
    @FXML private Button btnClear;
    @FXML private Button btnCancel;
    @FXML private Button btnSave;
    @FXML private Button btnGoSearch;
    @FXML private Button btnManageUsers;
    
    @FXML private ListView<String> listRecords;

    private PropertyRepository propertyRepository;
    private PropertyTypeRepository propertyTypeRepository;
    private StateRepository stateRepository;
    private AlertInformation alerta;
    private ViewFactory viewFactory;
    private UserSession session;

    public DashboardController() {
        this.propertyRepository = new PropertyRepository();
        this.propertyTypeRepository = new PropertyTypeRepository();
        this.stateRepository = new StateRepository();
        this.alerta = new AlertInformation();
        this.session = UserSession.getInstance();
        this.viewFactory = ViewFactory.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureComboConverters();
        cargarTipos();
        cargarEstados();
        loadCurrentUser();
        applyRolePermissions();
    }

    private void loadCurrentUser() {
        if (session.isLoggedIn()) {
            User currentUser = session.getCurrentUser();
            lblUser.setText("Usuario: " + currentUser.getName() + " " + currentUser.getLastName());
        } else {
            lblUser.setText("Usuario: Invitado");
        }
    }

    private void applyRolePermissions() {
        if (!session.isLoggedIn()) {
            hideForm();
            if (btnManageUsers != null) {
                btnManageUsers.setVisible(false);
                btnManageUsers.setManaged(false);
            }
            return;
        }
        
        if (session.isAdmin()) {
            showForm();
            if (btnManageUsers != null) {
                btnManageUsers.setVisible(true);
                btnManageUsers.setManaged(true);
            }
        } else {
            hideForm();
            if (btnManageUsers != null) {
                btnManageUsers.setVisible(false);
                btnManageUsers.setManaged(false);
            }
        }
    }

    private void showForm() {
        if (formPanel != null) {
            formPanel.setVisible(true);
            formPanel.setManaged(true);
        }
    }

    private void hideForm() {
        if (formPanel != null) {
            formPanel.setVisible(false);
            formPanel.setManaged(false);
        }
    }

    private void configureComboConverters() {
        cmbType.setConverter(new StringConverter<PropertyType>() {
            @Override
            public String toString(PropertyType type) {
                return type == null ? "" : type.getNameType();
            }

            @Override
            public PropertyType fromString(String string) {
                return null;
            }
        });

        cmbStatus.setConverter(new StringConverter<State>() {
            @Override
            public String toString(State state) {
                return state == null ? "" : state.getNameState();
            }

            @Override
            public State fromString(String string) {
                return null;
            }
        });
    }

    private void cargarTipos() {
        List<PropertyType> tipos = propertyTypeRepository.getAllPropertyTypes();
        cmbType.getItems().setAll(tipos);
    }

    private void cargarEstados() {
        List<State> estados = stateRepository.getAllStates();
        cmbStatus.getItems().setAll(estados);
    }

    @FXML
    public void actionSaveProperty(ActionEvent event) {
        if (!session.isAdmin()) {
            alerta.viewAlert(3, "Permiso Denegado", "No tienes permisos para registrar propiedades.", null);
            return;
        }

        if (txtCode.getText().isEmpty() || txtAddress.getText().isEmpty()
                || txtArea.getText().isEmpty() || txtPrice.getText().isEmpty()
                || cmbType.getSelectionModel().getSelectedItem() == null
                || cmbStatus.getSelectionModel().getSelectedItem() == null) {
            alerta.viewAlert(2, "Datos Incompletos", "Por favor, llena todos los campos obligatorios.", null);
            return;
        }

        try {
            Property newProperty = new Property();
            newProperty.setInternalCode(txtCode.getText());
            newProperty.setAddress(txtAddress.getText());
            newProperty.setArea(Double.parseDouble(txtArea.getText()));
            newProperty.setPrice(Double.parseDouble(txtPrice.getText()));
            newProperty.setType(cmbType.getSelectionModel().getSelectedItem());
            newProperty.setState(cmbStatus.getSelectionModel().getSelectedItem());
            newProperty.setActive(true);
            
            if (session.isLoggedIn()) {
                newProperty.setIdUser(session.getCurrentUser().getIdUser());
            } else {
                newProperty.setIdUser("SISTEMA"); 
            }

            if (propertyRepository.saveProperty(newProperty)) {
                alerta.viewAlert(1, "Guardado Exitoso", "La propiedad se ha registrado correctamente.", null);
                listRecords.getItems().add(0, "✅ Agregado: " + newProperty.getInternalCode() + " - " + newProperty.getAddress());
                actionClear(null);
            } else {
                alerta.viewAlert(3, "Error de Registro", "Ocurrió un problema al guardar en la base de datos.", null);
            }

        } catch (NumberFormatException e) {
            alerta.viewAlert(2, "Error de Formato", "El Área y el Precio deben contener únicamente números.", null);
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
        alerta.viewAlert(1, "Cancelado", "Se han limpiado los datos del formulario.", null);
    }
    
    @FXML
    public void actionManageUsers(ActionEvent event) {
        viewFactory.showUsersWindow();
    }

    @FXML
    public void goToSearch(ActionEvent event) {
        viewFactory.showSearchPropertyWindow();
    }

    @FXML
    public void goToMainMenu(ActionEvent event) {
        viewFactory.showMainViewWindow();
    }
}