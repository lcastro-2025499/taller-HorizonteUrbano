package org.horizonteurbano.system.controller;

import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.service.UserService;
import org.horizonteurbano.system.service.UserSession;
import org.horizonteurbano.system.utils.AlertInformation;
import org.horizonteurbano.system.utils.ViewFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
            
            session.setCurrentUser(loggedUser);
            
            closeCurrentWindow(event);
            
            redirectByRole(loggedUser);
        } else {
            alert.viewAlert(3, "Acceso Denegado", "Correo o contraseña incorrectos, o cuenta inactiva.", null);
        }
    }

    private void redirectByRole(User user) {
        int roleId = user.getRole() != null ? user.getRole().getIdRole() : -1;
        
        switch (roleId) {
            case UserSession.ROLE_ADMIN:
                viewFactory.showDashboardWindow();
                break;
            case UserSession.ROLE_ASESOR:
                viewFactory.showMainViewWindow();
                break;
            case UserSession.ROLE_GERENTE:
                viewFactory.showSearchPropertyWindow();
                break;
            default:
                alert.viewAlert(3, "Error", "Rol desconocido. Contacta al administrador.", null);
                viewFactory.showDashboardWindow();
                break;
        }
    }

    @FXML
    public void buttonLogout(ActionEvent event) {
        session.logout();
        alert.viewAlert(1, "Sesión Cerrada", "Has cerrado sesión correctamente.", null);
        
        closeCurrentWindow(event);
        viewFactory.showMainViewWindow();
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