package org.horizonteurbano.system.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneManager {

    private static SceneManager instanceSceneManager;
    private Stage currentStage;

    private SceneManager() {
    }

    public static SceneManager getInstanceSceneManager() {
        if (instanceSceneManager == null) {
            instanceSceneManager = new SceneManager();
        }
        return instanceSceneManager;
    }

    public void switchScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane root = (AnchorPane) loader.load();
            Scene scene = new Scene(root);
            currentStage.setScene(scene);
            currentStage.sizeToScene();
            currentStage.show();
        } catch (IOException exception) {
            System.out.println("Error loading scene: " + fxmlPath);
            exception.printStackTrace();
        }
    }

    public Stage getStage() {
        return currentStage;
    }

    public void setStage(Stage currentStage) {
        this.currentStage = currentStage;
    }
}
