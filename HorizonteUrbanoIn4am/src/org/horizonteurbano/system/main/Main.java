package org.horizonteurbano.system.main;

import javafx.application.Application;
import javafx.stage.Stage;
import org.horizonteurbano.system.utils.SceneManager;
import org.horizonteurbano.system.utils.ViewFactory;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        SceneManager.getInstanceSceneManager().setStage(primaryStage);
        ViewFactory viewFactory = new ViewFactory();
        viewFactory.showLoginWindow();
    }
}
