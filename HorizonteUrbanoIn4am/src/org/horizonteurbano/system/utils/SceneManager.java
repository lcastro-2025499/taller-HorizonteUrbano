package org.horizonteurbano.system.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneManager {

    private static SceneManager instanceSceneManager;
    private Stage mainStage;

    private static final String SEARCH_VIEW_PATH = "/org/horizonteurbano/system/view/SearchPropertyView.fxml";

    private SceneManager() {
    }

    public static SceneManager getInstanceSceneManager() {
        if (instanceSceneManager == null) {
            instanceSceneManager = new SceneManager();
        }
        return instanceSceneManager;
    }

    // Opens SearchPropertyView in a new Stage
    public void showSearchProperty() {
        try {
            Stage stage = new Stage();
            stage.setTitle("Buscar Propiedades - Horizonte Urbano");
            stage.setResizable(false);
            Scene scene = loadScene(SEARCH_VIEW_PATH);
            stage.setScene(scene);
            stage.show();
        } catch (IOException ioException) {
            System.err.println("Error loading Search Property view: " + ioException.getMessage());
        }
    }

    private Scene loadScene(String fxmlPath) throws IOException {
        URL fxmlLocation = getClass().getResource(fxmlPath);
        if (fxmlLocation == null) {
            throw new IOException("FXML file not found: " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();
        return new Scene(root);
    }

    public void changeScene(Scene scene) {
        if (mainStage == null) {
            System.err.println("Error: Main stage has not been initialized.");
            return;
        }
        mainStage.setScene(scene);
        mainStage.sizeToScene();
        mainStage.centerOnScreen();
        mainStage.show();
    }

    public void changeScene(Scene scene, double width, double height) {
        if (mainStage == null) {
            System.err.println("Error: Main stage has not been initialized.");
            return;
        }
        mainStage.setScene(scene);
        mainStage.setWidth(width);
        mainStage.setHeight(height);
        mainStage.centerOnScreen();
        mainStage.show();
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
}
