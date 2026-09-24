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

    @FXML
    private Label lblUser;
    @FXML
    private VBox formPanel;
    @FXML
    private TextField txtCode;
    @FXML
    private ComboBox<PropertyType> cmbType;
    @FXML
    private TextField txtArea;
    @FXML
    private TextField txtAddress;
    @FXML
    private ComboBox<State> cmbStatus;
    @FXML
    private TextField txtPrice;
    @FXML
    private TextArea txtDescription;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnGoSearch;
    @FXML
    private ListView<String> listRecords;

    private PropertyRepository propertyRepository;
    private PropertyTypeRepository propertyTypeRepository;
    private StateRepository stateRepository;
    private AlertInformation alerta;
    private UserSession session;
    private ViewFactory viewFactory;

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
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            lblUser.setText("Usuario: Invitado");
            return;
        }
        lblUser.setText("Usuario: " + currentUser.getName());
    }

    // Hides the property registration form from users who cannot create properties
    private void applyRolePermissions() {
        if (!session.isLoggedIn()) {
            hideForm();
            return;
        }
        // Only Admin (role 1) can create/edit/delete properties
        if (session.isAdmin()) {
            showForm();
        } else {
            hideForm();
        }
    }

    private void showForm() {
        formPanel.setVisible(true);
        formPanel.setManaged(true);
    }

    private void hideForm() {
        formPanel.setVisible(false);
        formPanel.setManaged(false);
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
        // Defensive check: only Admin can save, regardless of UI state
        if (!session.isAdmin()) {
            alerta.viewAlert(3, "Permiso Denegado", "No tienes permisos para registrar propiedades.", null);
            return;
        }

        if (txtCode.getText().isEmpty() || txtAddress.getText().isEmpty()
                || txtArea.getText().isEmpty() || txtPrice.getText().isEmpty()
                || cmbType.getSelectionModel().getSelectedItem() == null
                || cmbStatus.getSelectionModel().getSelectedItem() == null) {
            alerta.viewAlert(2, "Datos Incompletos", "Por favor, llena todos los campos obligatorios del formulario.", null);
            return;
        }

        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            alerta.viewAlert(3, "Sesión no válida", "Debes iniciar sesión para registrar propiedades.", null);
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
            newProperty.setIdUser(currentUser.getIdUser());

            if (propertyRepository.saveProperty(newProperty)) {
                alerta.viewAlert(1, "Guardado Exitoso", "La propiedad se ha registrado correctamente en el inventario.", null);
                listRecords.getItems().add(0, "✅ Agregado: " + newProperty.getInternalCode() + " - " + newProperty.getAddress());
                actionClear(null);
            } else {
                alerta.viewAlert(3, "Error de Registro", "Ocurrió un problema al guardar en la base de datos.", null);
            }

        } catch (NumberFormatException e) {
            alerta.viewAlert(2, "Error de Formato", "El Área y el Precio deben contener únicamente números, sin letras ni símbolos especiales.", null);
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
    public void goToSearch(ActionEvent event) {
        viewFactory.showSearchPropertyWindow();
    }

    @FXML
    public void goToMainMenu(ActionEvent event) {
        viewFactory.showMainViewWindow();
    }
}
