package org.horizonteurbano.system.controller;

import org.horizonteurbano.system.repositories.UserRepository;
import org.horizonteurbano.system.utils.AlertInformation;
import org.horizonteurbano.system.utils.ViewFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

public class ChangePasswordController {

    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField pwdNewPassword;
    @FXML
    private PasswordField pwdConfirmPassword;

    private UserRepository userRepository;
    private AlertInformation alert;
    private ViewFactory viewFactory;

    public ChangePasswordController() {
        this.userRepository = new UserRepository();
        this.alert = new AlertInformation();
        this.viewFactory = new ViewFactory();
    }

    @FXML
    public void buttonChangePassword(ActionEvent event) {
        String email = txtEmail.getText();
        String newPassword = pwdNewPassword.getText();
        String confirmPassword = pwdConfirmPassword.getText();

        if (isEmpty(email) || isEmpty(newPassword) || isEmpty(confirmPassword)) {
            alert.viewAlert(2, "Campos Vacíos", "Completa todos los campos.", null);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            alert.viewAlert(2, "Contraseñas no coinciden", "La nueva contraseña y su confirmación deben ser iguales.", null);
            return;
        }

        //1 = SUCCESS
        //2 = ALERT
        //3 = ERROR
        if (userRepository.updatePasswordByEmail(email, newPassword)) {
            alert.viewAlert(1, "Contraseña Actualizada", "Tu contraseña fue cambiada correctamente. Ya puedes iniciar sesión.", null);
            closeCurrentWindow(event);
            viewFactory.showLoginWindow();
        } else {
            alert.viewAlert(3, "Error", "No se encontró ninguna cuenta activa con ese correo.", null);
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void closeCurrentWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
