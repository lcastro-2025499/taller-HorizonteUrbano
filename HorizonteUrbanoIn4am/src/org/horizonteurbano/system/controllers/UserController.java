package org.horizonteurbano.system.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ComboBox;
import javafx.event.ActionEvent;

import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.models.Role;
import org.horizonteurbano.system.repositories.UserRepository;
import org.horizonteurbano.system.repositories.RoleRepository;
import org.horizonteurbano.system.utils.AlertInformation;

public class UserController implements Initializable {

    @FXML
    private TextField textFieldIdUser;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPhone;
    @FXML
    private PasswordField passwordFieldPassword;
    @FXML
    private ComboBox<String> comboBoxRole;

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private AlertInformation alerta;

    public UserController() {
        this.userRepository = new UserRepository();
        this.roleRepository = new RoleRepository();
        this.alerta = new AlertInformation();
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
            newUser.setIdUser(textFieldIdUser.getText());
            newUser.setName(textFieldName.getText());
            newUser.setLastName(textFieldLastName.getText());
            newUser.setEmail(textFieldEmail.getText());
            newUser.setPhone(textFieldPhone.getText());
            newUser.setPassword(passwordFieldPassword.getText());

            Role role = new Role();
            role.setIdRole(comboBoxRole.getSelectionModel().getSelectedIndex() + 1);
            newUser.setRol(role);

            newUser.setActive(true);

            if (userRepository.saveUser(newUser)) {
                alerta.viewAlert(1, "Éxito", "Usuario registrado correctamente en el sistema.", null);
                limpiarCampos();
            } else {
                alerta.viewAlert(3, "Error", "No se pudo registrar al usuario. Verifica los datos.", null);
            }
        } catch (Exception e) {
            alerta.viewAlert(3, "Error Crítico", "Ocurrió un problema al procesar los datos.", null);
        }
    }

    private void limpiarCampos() {
        textFieldIdUser.clear();
        textFieldName.clear();
        textFieldLastName.clear();
        textFieldEmail.clear();
        textFieldPhone.clear();
        passwordFieldPassword.clear();
        comboBoxRole.getSelectionModel().clearSelection();
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
