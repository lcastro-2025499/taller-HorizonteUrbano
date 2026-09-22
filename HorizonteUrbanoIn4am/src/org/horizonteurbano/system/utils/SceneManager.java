package org.horizonteurbano.system.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class SceneManager {
    private static SceneManager instanceSceneManager;
    private Stage mainStage;

    private Scene searchScene;
    private Scene formScene;

    private static final String SEARCH_FXML_PATH = "/org/horizonteurbano/system/view/SearchProperties.fxml";
    private static final String FORM_FXML_PATH = "/org/horizonteurbano/system/view/PropertyForm.fxml";
    private static final String CSS_PATH = "/org/horizonteurbano/system/styles/styles.css";

    private SceneManager() {

    }

    public static SceneManager getInstanceSceneManager() {
        if (instanceSceneManager == null) {
            instanceSceneManager = new SceneManager();
        }
        return instanceSceneManager;
    }

    public void showSearchView() {
        try {
            if (searchScene == null) {
                searchScene = loadScene(SEARCH_FXML_PATH);
            }
            changeScene(searchScene);
        } catch (Exception ExceptionFather) {
            System.err.println("Error loading Search View: " + ExceptionFather.getMessage());
            ExceptionFather.printStackTrace();
        }
    }

    public void showFormView() {
        try {
            if (formScene == null) {
                formScene = loadScene(FORM_FXML_PATH);
            }
            changeScene(formScene);
        } catch (Exception ExceptionFather) {
            System.err.println("Error loading Form View: " + ExceptionFather.getMessage());
            ExceptionFather.printStackTrace();
        }
    }

    private Scene loadScene(String fxmlPath) throws Exception {
        URL fxmlLocation = getClass().getResource(fxmlPath);
        if (fxmlLocation == null) {
            throw new IllegalArgumentException("FXML file not found: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent root = loader.load();
        Scene scene = new Scene(root);

        URL cssLocation = getClass().getResource(CSS_PATH);
        if (cssLocation != null) {
            scene.getStylesheets().add(cssLocation.toExternalForm());
        } else {
            System.err.println("Warning: CSS file not found at " + CSS_PATH);
        }

        return scene;
    }

    public void changeScene(Scene scene) {
        try {
            if (mainStage != null) {
                mainStage.setScene(scene);
                mainStage.sizeToScene();
                mainStage.centerOnScreen();
                mainStage.show();
            } else {
                System.err.println("Error: Main stage has not been initialized.");
            }
        } catch (NullPointerException NullObject) {
            System.err.println("Error: Null object while changing scene.");
            NullObject.printStackTrace();
        }
    }

    public void changeScene(Scene scene, double width, double height) {
        try {
            if (mainStage != null) {
                mainStage.setScene(scene);
                mainStage.setWidth(width);
                mainStage.setHeight(height);
                mainStage.centerOnScreen();
                mainStage.show();
            }
        } catch (NullPointerException NullObject) {
            System.err.println("Error: Null object while changing scene.");
            NullObject.printStackTrace();
        }
    }

    public void reloadScene(String sceneType) {
        try {
            if ("SEARCH".equalsIgnoreCase(sceneType)) {
                searchScene = loadScene(SEARCH_FXML_PATH);
                changeScene(searchScene);
            } else if ("FORM".equalsIgnoreCase(sceneType)) {
                formScene = loadScene(FORM_FXML_PATH);
                changeScene(formScene);
            }
        } catch (Exception FXMLException) {
            System.err.println("Error reloading scene: " + FXMLException.getMessage());
            FXMLException.printStackTrace();
        }
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public Scene getSearchScene() {
        return searchScene;
    }

    public Scene getFormScene() {
        return formScene;
    }

    public void clearScenes() {
        searchScene = null;
        formScene = null;
    }
}
