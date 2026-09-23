package org.horizonteurbano.system.controller;

import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.service.UserService;
import org.horizonteurbano.system.service.UserSession;
import org.horizonteurbano.system.utils.AlertInformation;
import org.horizonteurbano.system.utils.ViewFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

public class LoginController {

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField pwdPassword;

    private UserService userService;
    private AlertInformation alert;
    private ViewFactory viewFactory;

    public LoginController() {
        this.userService = new UserService();
        this.alert = new AlertInformation();
        this.viewFactory = new ViewFactory();
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

        User loggedUser = userService.login(email, password);
        if (loggedUser != null) {
            UserSession.getInstance().setCurrentUser(loggedUser);
            alert.viewAlert(1, "Login Exitoso", "¡Bienvenido a Horizonte Urbano, " + loggedUser.getName() + "!", null);
            clearFields();
            closeCurrentWindow(event);
            viewFactory.showDashboardWindow();
        } else {
            alert.viewAlert(3, "Acceso Denegado", "Correo o contraseña incorrectos, o cuenta inactiva.", null);
        }
    }

    @FXML
    public void buttonLogout(ActionEvent event) {
        alert.viewAlert(1, "Sesión Cerrada", "Has cerrado sesión correctamente.", null);
    }

    @FXML
    public void goToRegister(ActionEvent event) {
        closeCurrentWindow(event);
        viewFactory.showRegisterWindow();
    }

    @FXML
    public void goToChangePassword(ActionEvent event) {
        closeCurrentWindow(event);
        viewFactory.showChangePasswordWindow();
    }

    private void closeCurrentWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        txtEmail.clear();
        pwdPassword.clear();
    }
}
