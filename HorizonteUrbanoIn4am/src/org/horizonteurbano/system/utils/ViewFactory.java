package org.horizonteurbano.system.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.horizonteurbano.system.main.Main;

public class ViewFactory {
    
    private final String PATH_VIEWS = "/org/horizonteurbano/system/view/";
    
    public ViewFactory() {
        
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
            stage.setTitle("LOGIN - Horizonte Urbano");
            stage.setResizable(false);
            Scene scene = loadFileFXML("LoginView.fxml", 500, 550);
            stage.setScene(scene);
            stage.show();
        } catch (NullPointerException nullObject) {
            System.out.println("Error loading login window");
        }
    }

    public void showDashboardWindow() {
        try {
            Stage stage = new Stage();
            stage.setTitle("DASHBOARD - Horizonte Urbano");
            stage.setResizable(true);
            Scene scene = loadFileFXML("DashboardView.fxml", 800, 600);
            stage.setScene(scene);
            stage.show();
        } catch (NullPointerException nullObject) {
            System.out.println("Error loading dashboard window");
        }
    }

    public void closeStage(Stage stage) {
        if (stage != null) {
            stage.close();
        }
    }
}