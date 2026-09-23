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
import java.net.URL;
import java.util.ResourceBundle;

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
    private TextField txtPhone;
    @FXML
    private PasswordField pwdPassword;
    @FXML
    private ComboBox<String> cmbRole;

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
        cargarRoles();
    }

    private void cargarRoles() {
        // List<Role> roleList = roleRepository.getAllRoles();
        // for (Role role : roleList) {
        //     comboBoxRole.getItems().add(role.getNameRole());
        // }
    }

    @FXML
    public void createUser(ActionEvent event) {
        try {
            User newUser = new User();
            newUser.setIdUser(txtIdUser.getText());
            newUser.setName(txtName.getText());
            newUser.setLastName(txtLastName.getText());
            newUser.setEmail(txtEmail.getText());
            newUser.setPhone(txtPhone.getText());
            newUser.setPassword(pwdPassword.getText());

            Role role = new Role();
            role.setIdRole(cmbRole.getSelectionModel().getSelectedIndex() + 1);
            newUser.setRol(role);

            newUser.setActive(true);
            
            //1 = SUCCESS
            //2 = ERROR
            //3 = CRITICAL ERROR
            if (userRepository.saveUser(newUser)) {
                alert.viewAlert(1, "Éxito", "Usuario registrado correctamente en el sistema.", null);
                clearFields();
            } else {
                alert.viewAlert(3, "Error", "No se pudo registrar al usuario. Verifica los datos.", null);
            }
        } catch (Exception e) {
            alert.viewAlert(3, "Error Crítico", "Ocurrió un problema al procesar los datos.", null);
        }
    }

    private void clearFields() {
        txtIdUser.clear();
        txtName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPhone.clear();
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
