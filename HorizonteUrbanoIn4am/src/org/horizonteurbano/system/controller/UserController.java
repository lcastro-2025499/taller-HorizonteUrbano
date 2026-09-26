package org.horizonteurbano.system.controller;

import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.models.Role;
import org.horizonteurbano.system.repositories.UserRepository;
import org.horizonteurbano.system.repositories.RoleRepository;
import org.horizonteurbano.system.utils.AlertInformation;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Optional;

// IMPORTANTE: Asegúrate de tener la clase UserService que dice el Handoff
import org.horizonteurbano.system.service.UserService; 

public class UserController implements Initializable {

    @FXML private TextField txtIdUser;
    @FXML private TextField txtName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private PasswordField pwdPassword;
    @FXML private ComboBox<String> cmbRole;

    // Elementos nuevos para la Tabla
    @FXML private TableView<User> tblUsers;
    @FXML private TableColumn<User, String> colId;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colRole;

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserService userService; 
    private AlertInformation alert;
    private List<Role> rolesDisponibles;

    public UserController() {
        this.userRepository = new UserRepository();
        this.roleRepository = new RoleRepository();
        this.userService = new UserService();
        this.alert = new AlertInformation();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarRoles();
        configurarTabla();
        listUsers(null);
    }

    private void cargarRoles() {
        rolesDisponibles = roleRepository.getAllRoles();
        for (Role role : rolesDisponibles) {
             cmbRole.getItems().add(role.getNameRole());
        }
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idUser"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getRol() != null ? cellData.getValue().getRol().getNameRole() : ""));
    }

    @FXML
    public void createUser(ActionEvent event) {
        try {
            if (cmbRole.getSelectionModel().getSelectedIndex() < 0 || txtIdUser.getText().isEmpty() || pwdPassword.getText().isEmpty()) {
                alert.viewAlert(2, "Datos Incompletos", "Por favor, llena todos los campos.", null);
                return;
            }

            User newUser = new User();
            newUser.setIdUser(txtIdUser.getText());
            newUser.setName(txtName.getText());
            newUser.setLastName(txtLastName.getText());
            newUser.setEmail(txtEmail.getText());
            newUser.setPhone(txtPhone.getText());
            
            Role role = rolesDisponibles.get(cmbRole.getSelectionModel().getSelectedIndex());
            newUser.setRol(role);
            newUser.setActive(true);

            if (userService.register(newUser, pwdPassword.getText())) {
                alert.viewAlert(1, "Éxito", "Usuario registrado correctamente.", null);
                clearFields();
                listUsers(null); 
                alert.viewAlert(3, "Error", "No se pudo registrar al usuario.", null);
            }
        } catch (Exception e) {
            alert.viewAlert(3, "Error Crítico", "Ocurrió un problema: " + e.getMessage(), null);
        }
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
        List<User> usuarios = userRepository.getAllActiveUsers(); 
        tblUsers.setItems(FXCollections.observableArrayList(usuarios));
    }

    private void clearFields() {
        txtIdUser.clear(); txtName.clear(); txtLastName.clear(); 
        txtEmail.clear(); txtPhone.clear(); pwdPassword.clear(); 
        cmbRole.getSelectionModel().clearSelection();
    }

    @FXML public void updateUser(ActionEvent event) {}
    @FXML public void updateProfile(ActionEvent event) {}
}