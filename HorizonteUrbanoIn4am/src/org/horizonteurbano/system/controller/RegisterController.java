package org.horizonteurbano.system.controller;

import org.horizonteurbano.system.models.Role;
import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.repositories.UserRepository;
import org.horizonteurbano.system.utils.AlertInformation;
import org.horizonteurbano.system.utils.ViewFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class RegisterController implements Initializable {

    @FXML
    private AnchorPane apMainContainer;
    @FXML
    private ImageView imgLogo;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField pwdPassword;
    @FXML
    private PasswordField pwdConfirmPassword;
    @FXML
    private CheckBox chkTerms;

    private double angle = 0;
    private UserRepository userRepository;
    private AlertInformation alert;
    private ViewFactory viewFactory;

    // TODO Luis: confirmar cuál id_role corresponde a un usuario auto-registrado
    private static final int DEFAULT_ROLE_ID = 1;

    public RegisterController() {
        this.userRepository = new UserRepository();
        this.alert = new AlertInformation();
        this.viewFactory = ViewFactory.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Image img = new Image(getClass().getResourceAsStream("/org/horizonteurbano/system/view/logoImage.png"));
            if (imgLogo != null && img != null) {
                imgLogo.setImage(img);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
        }

        applyCircularCrop();
        startGradientAnimation();
    }

    private void applyCircularCrop() {
        if (imgLogo != null) {
            double size = 160.0; //Dimensión cuadrada para el logo
            imgLogo.setFitWidth(size);
            imgLogo.setFitHeight(size);

            Circle clip = new Circle(size / 2.0, size / 2.0, size / 2.0);
            imgLogo.setClip(clip);
        }
    }

    private void startGradientAnimation() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(35), e -> {
            angle = (angle + 0.4) % 360;

            double startX = 50 + 50 * Math.cos(Math.toRadians(angle));
            double startY = 50 + 50 * Math.sin(Math.toRadians(angle));
            double endX = 50 - 50 * Math.cos(Math.toRadians(angle));
            double endY = 50 - 50 * Math.sin(Math.toRadians(angle));

            String cssGradiente = String.format(
                    "-fx-background-color: linear-gradient(from %.1f%% %.1f%% to %.1f%% %.1f%%, #E3D8C8 0%%, #A4AD8F 50%%, #6E8354 100%%);",
                    startX, startY, endX, endY
            );

            if (apMainContainer != null) {
                apMainContainer.setStyle(cssGradiente);
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    public void buttonRegister(ActionEvent event) {
        String name = txtName.getText();
        String lastName = txtLastName.getText();
        String email = txtEmail.getText();
        String password = pwdPassword.getText();
        String confirmPassword = pwdConfirmPassword.getText();

        if (isEmpty(name) || isEmpty(lastName) || isEmpty(email) || isEmpty(password) || isEmpty(confirmPassword)) {
            alert.viewAlert(2, "Campos Vacíos", "Por favor, completa todos los campos.", null);
            return;
        }

        if (!password.equals(confirmPassword)) {
            alert.viewAlert(2, "Contraseñas no coinciden", "La contraseña y su confirmación deben ser iguales.", null);
            return;
        }

        if (!chkTerms.isSelected()) {
            alert.viewAlert(2, "Términos y Condiciones", "Debes aceptar los Términos y Condiciones para continuar.", null);
            return;
        }

        User newUser = new User();
        newUser.setIdUser(UUID.randomUUID().toString());
        newUser.setName(name);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setUserName(generateUserName(email));
        newUser.setPassword(password);
        newUser.setActive(true);

        Role role = new Role();
        role.setIdRole(DEFAULT_ROLE_ID);
        newUser.setRol(role);

        //1 = SUCCESS
        //2 = ALERT
        //3 = ERROR
        if (userRepository.saveUser(newUser)) {
            alert.viewAlert(1, "Registro Exitoso", "Cuenta creada correctamente. Ya puedes iniciar sesión.", null);
            closeCurrentWindow(event);
            viewFactory.showLoginWindow();
        } else {
            alert.viewAlert(3, "Error", "No se pudo crear la cuenta. Verifica que el correo no esté registrado.", null);
        }
    }

    @FXML
    public void goToLogin(ActionEvent event) {
        closeCurrentWindow(event);
        viewFactory.showLoginWindow();
    }

    private String generateUserName(String email) {
        return email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void closeCurrentWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
