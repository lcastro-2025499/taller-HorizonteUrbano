package org.horizonteurbano.system.controller;

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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class DashboardController implements Initializable {

    @FXML private Label lblUser;
    @FXML private Label lblFooterStatus;
    @FXML private VBox formPanel;
    
    @FXML private TextField txtCode;
    @FXML private ComboBox<PropertyType> cmbType;
    @FXML private TextField txtArea;
    @FXML private TextField txtAddress;
    @FXML private ComboBox<State> cmbStatus;
    @FXML private TextField txtPrice;
    @FXML private TextArea txtDescription;

    @FXML private Button btnClear;
    @FXML private Button btnSave;
    @FXML private Button btnGoSearch;
    @FXML private Button btnManageUsers;
    @FXML private Button btnSelectImage;
    @FXML private Label lblImagePath;

    private static final String UPLOAD_DIR = "uploads/properties/";
    private String selectedImagePath;
    
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
        this.session = UserSession.getInstance();
        this.viewFactory = ViewFactory.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureComboConverters();
        loadPropertyTypes();
        loadStates();
        loadCurrentUser();
        applyRolePermissions();
        updateFooterStatus("Esperando ingreso de datos...");
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
        if (!session.isLoggedIn() || !session.isAdmin()) {
            hideForm();
            if (btnManageUsers != null) {
                btnManageUsers.setVisible(false);
                btnManageUsers.setManaged(false);
            }
        } else {
            showForm();
            if (btnManageUsers != null) {
                btnManageUsers.setVisible(true);
                btnManageUsers.setManaged(true);
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
        cmbType.setConverter(new StringConverter<>() {
            @Override public String toString(PropertyType type) { return type == null ? "" : type.getNameType(); }
            @Override public PropertyType fromString(String string) { return null; }
        });

        cmbStatus.setConverter(new StringConverter<>() {
            @Override public String toString(State state) { return state == null ? "" : state.getNameState(); }
            @Override public State fromString(String string) { return null; }
        });
    }

    private void loadPropertyTypes() {
        List<PropertyType> propertyTypes = propertyTypeRepository.getAllPropertyTypes();
        cmbType.getItems().setAll(propertyTypes);
    }

    private void loadStates() {
        List<State> states = stateRepository.getAllStates();
        cmbStatus.getItems().setAll(states);
    }

    private void updateFooterStatus(String message) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        lblFooterStatus.setText("Estado: " + message + "  |  " + time);
    }

    @FXML
    public void actionSaveProperty(ActionEvent event) {
        if (!session.isAdmin()) {
            alertInformation.viewAlert(3, "Permiso Denegado", "No tienes permisos para registrar propiedades.", null);
            return;
        }

        if (txtCode.getText().trim().isEmpty() || txtAddress.getText().trim().isEmpty()
                || txtArea.getText().trim().isEmpty() || txtPrice.getText().trim().isEmpty()
                || cmbType.getSelectionModel().getSelectedItem() == null
                || cmbStatus.getSelectionModel().getSelectedItem() == null) {
            alertInformation.viewAlert(2, "Datos Incompletos", "Por favor, llena todos los campos obligatorios.", null);
            updateFooterStatus("Error: Campos incompletos");
            return;
        }

        try {
            Property newProperty = new Property();
            newProperty.setInternalCode(txtCode.getText().trim());
            newProperty.setAddress(txtAddress.getText().trim());
            newProperty.setArea(Double.parseDouble(txtArea.getText().trim()));
            newProperty.setPrice(Double.parseDouble(txtPrice.getText().trim()));
            newProperty.setType(cmbType.getSelectionModel().getSelectedItem());
            newProperty.setState(cmbStatus.getSelectionModel().getSelectedItem());
            newProperty.setCoverUrl(selectedImagePath);
            newProperty.setActive(true);
            newProperty.setIdUser(session.isLoggedIn() ? session.getCurrentUser().getIdUser() : "SISTEMA");

            if (propertyRepository.saveProperty(newProperty)) {
                updateFooterStatus("Propiedad " + newProperty.getInternalCode() + " guardada exitosamente");
                alertInformation.viewAlert(1, "Guardado Exitoso", "La propiedad se ha registrado correctamente.", null);
                actionClear(null);
            } else {
                updateFooterStatus("Error al guardar en la base de datos");
                alertInformation.viewAlert(3, "Error de Registro", "Ocurrió un problema al guardar en la base de datos.", null);
            }
        } catch (NumberFormatException e) {
            updateFooterStatus("Error: Formato numérico inválido");
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
        selectedImagePath = null;
        lblImagePath.setText("Ningún archivo seleccionado");
        updateFooterStatus("Formulario limpiado, listo para nuevo ingreso");
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

    @FXML
    public void actionSelectImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen de Portada");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(btnSelectImage.getScene().getWindow());
        if (selectedFile == null) return;

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf('.'));
            String newFileName = UUID.randomUUID().toString() + extension;
            Path destination = uploadPath.resolve(newFileName);

            Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
            selectedImagePath = destination.toString();
            lblImagePath.setText(selectedFile.getName());
            updateFooterStatus("Imagen de portada seleccionada");
        } catch (IOException e) {
            updateFooterStatus("Error al copiar la imagen");
            alertInformation.viewAlert(3, "Error", "No se pudo guardar la imagen seleccionada.", null);
        }
    }
}