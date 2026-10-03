package org.horizonteurbano.system.controller;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import org.horizonteurbano.system.models.Role;
import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.repositories.RoleRepository;
import org.horizonteurbano.system.repositories.UserRepository;
import org.horizonteurbano.system.service.UserService;
import org.horizonteurbano.system.utils.AlertInformation;
import org.horizonteurbano.system.utils.ViewFactory;

public class UserController implements Initializable {

    @FXML private Button btnGoBack;
    @FXML private Button btnClear;
    @FXML private Button btnEdit;
    @FXML private Button btnSave;
    @FXML private Button btnDeactivate;
    @FXML private Button btnRefresh;

    @FXML private CheckBox chkShowInactive;
    @FXML private TextField txtUserId;
    @FXML private TextField txtName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private PasswordField pwdPassword;
    @FXML private ComboBox<Role> cmbRole;

    @FXML private TableView<User> tblUsers;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;

    private UserService userService;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private AlertInformation alert;

    public UserController() {
        this.userService = new UserService();
        this.userRepository = new UserRepository();
        this.roleRepository = new RoleRepository();
        this.alert = new AlertInformation();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureRoleCombo();
        loadRoles();
        configurarTabla();
        listUsers(null);
    }

    private void configureRoleCombo() {
        cmbRole.setConverter(new StringConverter<Role>() {
            @Override public String toString(Role role) { return role == null ? "" : role.getNameRole(); }
            @Override public Role fromString(String string) { return null; }
        });
    }

    private void loadRoles() {
        List<Role> roleList = roleRepository.getAllRoles();
        cmbRole.getItems().setAll(roleList);
    }

    private void configurarTabla() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        if (colName != null) colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (colEmail != null) colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        if (colRole != null) {
            colRole.setCellValueFactory(cellData -> new SimpleStringProperty(
                    cellData.getValue().getRol() != null ? cellData.getValue().getRol().getNameRole() : ""));
        }
    }

    @FXML
    public void actionEditUser(ActionEvent event) {
        User seleccionado = tblUsers.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            alert.viewAlert(2, "Selección Requerida", "Selecciona un usuario de la tabla para editar.", null);
            return;
        }
        txtUserId.setText(seleccionado.getIdUser());
        txtName.setText(seleccionado.getName());
        txtLastName.setText(seleccionado.getLastName());
        txtEmail.setText(seleccionado.getEmail());
        txtPhone.setText(seleccionado.getPhone() != null ? seleccionado.getPhone() : "");
        
        // FIX: Buscar el rol por ID para asegurar que se seleccione visualmente en el ComboBox
        Role rolSeleccionado = seleccionado.getRol();
        for (Role role : cmbRole.getItems()) {
            if (role.getIdRole() == rolSeleccionado.getIdRole()) {
                cmbRole.getSelectionModel().select(role);
                break;
            }
        }
        
        pwdPassword.clear();
        pwdPassword.setPromptText("Dejar en blanco para mantener la actual");
    }

    @FXML
    public void actionSaveUser(ActionEvent event) {
        if (isEmpty(txtName.getText()) || isEmpty(txtLastName.getText()) || isEmpty(txtEmail.getText()) || cmbRole.getSelectionModel().getSelectedItem() == null) {
            alert.viewAlert(2, "Campos Vacíos", "Por favor, completa todos los campos obligatorios.", null);
            return;
        }
        if (!userService.isValidEmail(txtEmail.getText())) {
            alert.viewAlert(2, "Correo Inválido", "Ingresa un correo electrónico válido.", null);
            return;
        }

        boolean isUpdate = !isEmpty(txtUserId.getText());

        try {
            User user = new User();
            user.setName(txtName.getText());
            user.setLastName(txtLastName.getText());
            user.setEmail(txtEmail.getText());
            user.setPhone(txtPhone.getText());
            user.setUserName(generateUserName(txtEmail.getText()));
            user.setActive(true);
            user.setRol(cmbRole.getSelectionModel().getSelectedItem());

            if (isUpdate) {
                user.setIdUser(txtUserId.getText());
                if (userRepository.updateUser(user)) {
                    alert.viewAlert(1, "Éxito", "Usuario actualizado correctamente.", null);
                    actionClearForm(null);
                    listUsers(null);
                } else {
                    alert.viewAlert(3, "Error", "No se pudo actualizar el usuario.", null);
                }
            } else {
                if (isEmpty(pwdPassword.getText())) {
                    alert.viewAlert(2, "Contraseña Requerida", "La contraseña es obligatoria para nuevos usuarios.", null);
                    return;
                }
                user.setIdUser(UUID.randomUUID().toString());
                if (userService.register(user, pwdPassword.getText())) {
                    alert.viewAlert(1, "Éxito", "Usuario registrado correctamente.", null);
                    actionClearForm(null);
                    listUsers(null);
                } else {
                    alert.viewAlert(3, "Error", "No se pudo registrar al usuario.", null);
                }
            }
        } catch (Exception e) {
            alert.viewAlert(3, "Error Crítico", "Ocurrió un problema: " + e.getMessage(), null);
        }
    }

    @FXML
    public void actionClearForm(ActionEvent event) {
        txtUserId.clear();
        txtName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPhone.clear();
        pwdPassword.clear();
        pwdPassword.setPromptText("Contraseña");
        cmbRole.getSelectionModel().clearSelection();
    }

    @FXML
    public void deactivateUser(ActionEvent event) {
        User seleccionado = tblUsers.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            alert.viewAlert(2, "Selección Requerida", "Selecciona un usuario de la tabla para desactivarlo.", null);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Baja");
        confirm.setHeaderText("Desactivar empleado: " + seleccionado.getName());
        confirm.setContentText("¿Estás seguro de que deseas revocar el acceso a este usuario?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (userRepository.deleteUser(seleccionado.getIdUser())) {
                alert.viewAlert(1, "Éxito", "Usuario desactivado.", null);
                listUsers(null);
            } else {
                alert.viewAlert(3, "Error", "No se pudo desactivar el usuario.", null);
            }
        }
    }

    @FXML
    public void listUsers(ActionEvent event) {
        if (tblUsers != null && userRepository != null) {
            List<User> usuarios = (chkShowInactive != null && chkShowInactive.isSelected())
                    ? userRepository.getAllUsers()
                    : userRepository.getAllActiveUsers();
            tblUsers.setItems(FXCollections.observableArrayList(usuarios));
        }
    }

    private String generateUserName(String email) {
        return email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    @FXML
    public void actionGoBack(ActionEvent event) {
        ViewFactory.getInstance().showDashboardWindow();
    }
}