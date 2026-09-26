package org.horizonteurbano.system.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.service.UserService;
import org.horizonteurbano.system.service.UserSession;
import org.horizonteurbano.system.utils.AlertInformation;
import org.horizonteurbano.system.utils.ViewFactory;

public class LoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField pwdPassword;

    private UserService userService;
    private AlertInformation alert;
    private ViewFactory viewFactory;
    private UserSession session;

    public LoginController() {
        this.userService = new UserService();
        this.alert = new AlertInformation();
        this.viewFactory = ViewFactory.getInstance();
        this.session = UserSession.getInstance();
    }

    @FXML
    public void buttonLogin(ActionEvent event) {
        String email = txtEmail.getText();
        String password = pwdPassword.getText();

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            alert.viewAlert(2, "Campos Vacíos", "Por favor, ingresa tu correo y contraseña.", null);
            return;
        }

        User loggedUser = userService.login(email.trim(), password.trim());

        if (loggedUser != null) {
            alert.viewAlert(1, "Login Exitoso", "¡Bienvenido a Horizonte Urbano, " + loggedUser.getName() + "!", null);
            clearFields();
            
            session.login(loggedUser);
            closeCurrentWindow(event);

            int roleId = loggedUser.getRol().getIdRole();
            switch (roleId) {
                case 1: // Administrador -> Va al Dashboard Gerencial / Registro de Propiedades
                    viewFactory.showDashboardWindow();
                    break;
                case 2: // Asesor -> Va al catálogo público principal
                    viewFactory.showMainViewWindow();
                    break;
                case 3: // Gerente -> Va a la vista de métricas y reportes
                    viewFactory.showReportsWindow();
                    break;
                default:
                    viewFactory.showDashboardWindow();
                    break;
            }
        } else {
            alert.viewAlert(3, "Acceso Denegado", "Correo o contraseña incorrectos, o cuenta inactiva.", null);
        }
    }

    @FXML
    public void buttonLogout(ActionEvent event) {
        session.logout();
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
        viewFactory.closeStage(stage);
    }

    private void clearFields() {
        txtEmail.clear();
        pwdPassword.clear();
    }
}