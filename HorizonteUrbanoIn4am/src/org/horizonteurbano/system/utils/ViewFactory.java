package org.horizonteurbano.system.utils;

import org.horizonteurbano.system.main.Main;
import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.controller.EditPropertyController;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

public class ViewFactory {

    private final String PATH_VIEWS = "/org/horizonteurbano/system/view/";
    
    private static ViewFactory instance;

    private ViewFactory() {
    }

    public static ViewFactory getInstance() {
        if (instance == null) {
            instance = new ViewFactory();
        }
        return instance;
    }

    private Scene loadFileFXML(String nameFXML, int width, int height) {
        String pathOfFile = PATH_VIEWS + nameFXML;
        try {
            FXMLLoader loaderFXML = new FXMLLoader();
            URL urlFile = Main.class.getResource(pathOfFile);
            loaderFXML.setBuilderFactory(new JavaFXBuilderFactory());
            loaderFXML.setLocation(urlFile);
            return new Scene(loaderFXML.load(), width, height);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    
    public void showLoginWindow() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Login - Horizonte Urbano");
            stage.setResizable(false);
            Scene scene = loadFileFXML("LoginView.fxml", 600, 400);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading login window: " + e.getMessage());
        }
    }

    public void showMainViewWindow() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Catálogo Público - Horizonte Urbano");
            stage.setResizable(true);
            Scene scene = loadFileFXML("MainView.fxml", 1080, 720);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading main view window: " + e.getMessage());
        }
    }

    public void showDashboardWindow() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Dashboard - Horizonte Urbano");
            stage.setResizable(true);
            Scene scene = loadFileFXML("DashboardView.fxml", 960, 680);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading dashboard window: " + e.getMessage());
        }
    }

    public void showSearchPropertyWindow() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Búsqueda de Inventario - Horizonte Urbano");
            stage.setResizable(true);
            Scene scene = loadFileFXML("SearchPropertyView.fxml", 920, 540);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading search window: " + e.getMessage());
        }
    }

    public void showReportsWindow() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Reportes Gerenciales - Horizonte Urbano");
            stage.setResizable(true);
            Scene scene = loadFileFXML("ReportsView.fxml", 920, 540);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading reports window: " + e.getMessage());
        }
    }

    public void showRegisterWindow() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Registro - Horizonte Urbano");
            stage.setResizable(false);
            Scene scene = loadFileFXML("RegisterView.fxml", 610, 490);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading register window: " + e.getMessage());
        }
    }

    public void showChangePasswordWindow() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Cambiar Contraseña - Horizonte Urbano");
            stage.setResizable(false);
            Scene scene = loadFileFXML("ChangePasswordView.fxml", 450, 550);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading change password window: " + e.getMessage());
        }
    }
    
    public void showUsersWindow() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Gestión de Personal - Horizonte Urbano");
            stage.setResizable(true);
            Scene scene = loadFileFXML("UsersView.fxml", 1000, 650);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading users window: " + e.getMessage());
        }
    }

    
    public void showEditPropertyWindow(Property property) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Editar Propiedad - Horizonte Urbano");
            stage.setResizable(false);

            String pathOfFile = PATH_VIEWS + "EditPropertyView.fxml";
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(pathOfFile));
            Scene scene = new Scene(loader.load());

            EditPropertyController controller = loader.getController();
            controller.setPropertyData(property);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error loading edit property window: " + e.getMessage());
        }
    }


    public void closeStage(Stage stage) {
        if (stage != null) {
            stage.close();
        }
    }
}