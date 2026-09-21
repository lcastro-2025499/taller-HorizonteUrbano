package org.horizonteurbano.system.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;
import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.repositories.UserRepository;
import org.horizonteurbano.system.utils.AlertInformation;

public class LoginController {

    @FXML
    private TextField textFieldEmail;
    @FXML
    private PasswordField passwordFieldPassword;

    private UserRepository userRepository;
    private AlertInformation alerta;

    public LoginController() {
        this.userRepository = new UserRepository();
        this.alerta = new AlertInformation();
    }

    @FXML
    public void buttonLogin(ActionEvent event) {
        String email = textFieldEmail.getText();
        String password = passwordFieldPassword.getText();

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            // 2 = WARNING
            alerta.viewAlert(2, "Campos Vacíos", "Por favor, ingresa tu correo y contraseña.", null);
            return;
        }

        User loggedUser = userRepository.validateLogin(email, password);

        if (loggedUser != null) {
            alerta.viewAlert(1, "Login Exitoso", "¡Bienvenido a Horizonte Urbano, " + loggedUser.getName() + "!", null);
            limpiarCampos();
        } else {
            alerta.viewAlert(3, "Acceso Denegado", "Correo o contraseña incorrectos, o cuenta inactiva.", null);
        }
    }

    @FXML
    public void buttonLogout(ActionEvent event) {
        alerta.viewAlert(1, "Sesión Cerrada", "Has cerrado sesión correctamente.", null);
    }

    private void limpiarCampos() {
        textFieldEmail.clear();
        passwordFieldPassword.clear();
    }
}
