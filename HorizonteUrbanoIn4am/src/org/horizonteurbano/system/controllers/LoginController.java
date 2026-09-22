package org.horizonteurbano.system.controllers;

import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.repositories.UserRepository;
import org.horizonteurbano.system.utils.AlertInformation;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;

public class LoginController {

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField pwdPassword;

    private UserRepository userRepository;
    private AlertInformation alert;

    public LoginController() {
        this.userRepository = new UserRepository();
        this.alert = new AlertInformation();
    }

    @FXML
    public void buttonLogin(ActionEvent event) {
        String email = txtEmail.getText();
        String password = pwdPassword.getText();

        //1 = SUCCESS
        //2 = ALERT
        //3 = DENIED
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            alert.viewAlert(2, "Campos Vacíos", "Por favor, ingresa tu correo y contraseña.", null);
            return;
        }

        User loggedUser = userRepository.validateLogin(email, password);
        if (loggedUser != null) {
            alert.viewAlert(1, "Login Exitoso", "¡Bienvenido a Horizonte Urbano, " + loggedUser.getName() + "!", null);
            clearFields();
        } else {
            alert.viewAlert(3, "Acceso Denegado", "Correo o contraseña incorrectos, o cuenta inactiva.", null);
        }
    }

    @FXML
    public void buttonLogout(ActionEvent event) {
        alert.viewAlert(1, "Sesión Cerrada", "Has cerrado sesión correctamente.", null);
    }

    private void clearFields() {
        txtEmail.clear();
        pwdPassword.clear();
    }
}
