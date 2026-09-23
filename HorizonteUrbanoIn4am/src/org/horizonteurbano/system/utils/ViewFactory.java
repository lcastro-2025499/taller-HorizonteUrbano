package org.horizonteurbano.system.utils;

import org.horizonteurbano.system.Main;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class ViewFactory {

    private final String PATH_VIEWS = "/org/horizonteurbano/system/view/";

    public ViewFactory() {
    }

    private Scene loadFileFXML(String nameFXML, int width, int height) {
        String pathOfFile = PATH_VIEWS + nameFXML;
        URL urlFile = Main.class.getResource(pathOfFile);

        if (urlFile == null) {
            throw new IllegalStateException("FXML file not found in classpath: " + pathOfFile);
        }

        try {
            FXMLLoader loaderFXML = new FXMLLoader();
            loaderFXML.setBuilderFactory(new JavaFXBuilderFactory());
            loaderFXML.setLocation(urlFile);
            return new Scene(loaderFXML.load(), width, height);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load FXML: " + pathOfFile, exception);
        }
    }

    public void showLoginWindow() {
        Stage stage = new Stage();
        stage.setTitle("Login - Horizonte Urbano");
        stage.setResizable(false);
        Scene scene = loadFileFXML("LoginView.fxml", 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    public void showDashboardWindow() {
        Stage stage = new Stage();
        stage.setTitle("Dashboard - Horizonte Urbano");
        stage.setResizable(true);
        Scene scene = loadFileFXML("DashboardView.fxml", 960, 680);
        stage.setScene(scene);
        stage.show();
    }

    public void showRegisterWindow() {
        Stage stage = new Stage();
        stage.setTitle("Register - Horizonte Urbano");
        stage.setResizable(false);
        Scene scene = loadFileFXML("RegisterView.fxml", 610, 545);
        stage.setScene(scene);
        stage.show();
    }

    public void showChangePasswordWindow() {
        Stage stage = new Stage();
        stage.setTitle("Change password - Horizonte Urbano");
        stage.setResizable(false);
        Scene scene = loadFileFXML("ChangePasswordView.fxml", 450, 550);
        stage.setScene(scene);
        stage.show();
    }

    public void closeStage(Stage stage) {
        if (stage != null) {
            stage.close();
        }
    }
}
