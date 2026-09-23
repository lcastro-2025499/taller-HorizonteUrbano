package org.horizonteurbano.system.controller;

import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.models.Role;
import org.horizonteurbano.system.repositories.UserRepository;
import org.horizonteurbano.system.repositories.RoleRepository;
import org.horizonteurbano.system.utils.AlertInformation;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ComboBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.util.StringConverter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class UserController implements Initializable {

    @FXML
    private TextField txtIdUser;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField pwdPassword;
    @FXML
    private ComboBox<Role> cmbRole;

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private AlertInformation alert;

    public UserController() {
        this.userRepository = new UserRepository();
        this.roleRepository = new RoleRepository();
        this.alert = new AlertInformation();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureRoleCombo();
        loadRoles();
    }

    // Displays the role name in the combo box while keeping the Role object as the value
    private void configureRoleCombo() {
        cmbRole.setConverter(new StringConverter<Role>() {
            @Override
            public String toString(Role role) {
                return role == null ? "" : role.getNameRole();
            }

            @Override
            public Role fromString(String string) {
                return null;
            }
        });
    }

    private void loadRoles() {
        List<Role> roleList = roleRepository.getAllRoles();
        cmbRole.getItems().setAll(roleList);
    }

    @FXML
    public void createUser(ActionEvent event) {
        if (isEmpty(txtName.getText()) || isEmpty(txtLastName.getText())
                || isEmpty(txtEmail.getText()) || isEmpty(pwdPassword.getText())
                || cmbRole.getSelectionModel().getSelectedItem() == null) {
            alert.viewAlert(2, "Campos Vacíos", "Por favor, completa todos los campos.", null);
            return;
        }

        try {
            User newUser = new User();
            // ID is generated automatically, same as in self-registration
            newUser.setIdUser(UUID.randomUUID().toString());
            newUser.setName(txtName.getText());
            newUser.setLastName(txtLastName.getText());
            newUser.setEmail(txtEmail.getText());
            newUser.setUserName(generateUserName(txtEmail.getText()));
            newUser.setPassword(pwdPassword.getText());
            newUser.setActive(true);
            newUser.setRol(cmbRole.getSelectionModel().getSelectedItem());

            //1 = SUCCESS
            //2 = ALERT
            //3 = ERROR
            if (userRepository.saveUser(newUser)) {
                alert.viewAlert(1, "Éxito", "Usuario registrado correctamente en el sistema.", null);
                clearFields();
            } else {
                alert.viewAlert(3, "Error", "No se pudo registrar al usuario. Verifica los datos.", null);
            }
        } catch (Exception e) {
            alert.viewAlert(3, "Error Crítico", "Ocurrió un problema al procesar los datos.", null);
            System.err.println("Error creating user: " + e.getMessage());
        }
    }

    private String generateUserName(String email) {
        return email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void clearFields() {
        if (txtIdUser != null) {
            txtIdUser.clear();
        }
        txtName.clear();
        txtLastName.clear();
        txtEmail.clear();
        pwdPassword.clear();
        cmbRole.getSelectionModel().clearSelection();
    }

    @FXML
    public void updateUser(ActionEvent event) {
    }

    @FXML
    public void deactivateUser(ActionEvent event) {
    }

    @FXML
    public void listUsers(ActionEvent event) {
    }

    @FXML
    public void updateProfile(ActionEvent event) {
    }
}
