package org.horizonteurbano.system.utils;

import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.controller.EditPropertyController;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class ViewFactory {

    private static final String PATH_VIEWS = "/org/horizonteurbano/system/view/";
    private static ViewFactory instance;
    private Stage mainStage;

    private ViewFactory() {
    }

    public static ViewFactory getInstance() {
        if (instance == null) {
            instance = new ViewFactory();
        }
        return instance;
    }

    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    private Scene loadFileFXML(String nameFXML, int width, int height) {
        String pathOfFile = PATH_VIEWS + nameFXML;
        URL urlFile = ViewFactory.class.getResource(pathOfFile);

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

    // Reemplaza la escena en la ventana principal (Arquitectura de develop)
    private void showView(String title, String fxml, int width, int height) {
        if (mainStage == null) {
            throw new IllegalStateException("Main stage not set. Call setMainStage() first.");
        }
        Scene scene = loadFileFXML(fxml, width, height);
        mainStage.setTitle(title);
        mainStage.setScene(scene);
        mainStage.setWidth(width);
        mainStage.setHeight(height);
        mainStage.centerOnScreen();
        if (!mainStage.isShowing()) {
            mainStage.show();
        }
    }

    public void showLoginWindow() {
        showView("Login - Horizonte Urbano", "LoginView.fxml", 600, 400);
    }

    public void showDashboardWindow() {
        showView("Dashboard - Horizonte Urbano", "DashboardView.fxml", 960, 680);
    }

    public void showRegisterWindow() {
        showView("Register - Horizonte Urbano", "RegisterView.fxml", 610, 545);
    }

    public void showChangePasswordWindow() {
        showView("Change Password - Horizonte Urbano", "ChangePasswordView.fxml", 450, 550);
    }

    public void showSearchPropertyWindow() {
        showView("Search Properties - Horizonte Urbano", "SearchPropertyView.fxml", 920, 540);
    }

    public void showMainViewWindow() {
        showView("Catalog - Horizonte Urbano", "MainView.fxml", 900, 600);
    }

    // Rutas exclusivas de tu rama local
    public void showReportsWindow() {
        showView("Reportes Gerenciales - Horizonte Urbano", "ReportsView.fxml", 920, 540);
    }

    public void showUsersWindow() {
        showView("Gestión de Personal - Horizonte Urbano", "UsersView.fxml", 1000, 650);
    }

    // Este se mantiene como Pop-up independiente (inyecta los datos al controlador)
    public void showEditPropertyWindow(Property property) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Editar Propiedad - Horizonte Urbano");
            stage.setResizable(false);

            String pathOfFile = PATH_VIEWS + "EditPropertyView.fxml";
            
            // CORRECCIÓN DEL ERROR: Usamos ViewFactory.class en lugar de Main.class
            FXMLLoader loader = new FXMLLoader(ViewFactory.class.getResource(pathOfFile));
            Scene scene = new Scene(loader.load());

            EditPropertyController controller = loader.getController();
            controller.setPropertyData(property);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading edit property window: " + e.getMessage());
        }
    }

    // Utilidad para cerrar ventanas modales
    public void closeStage(Stage stage) {
        if (stage != null) {
            stage.close();
        }
    }
}