package org.horizonteurbano.system;

import org.horizonteurbano.system.utils.SceneManager;
import org.horizonteurbano.system.utils.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        SceneManager.getInstanceSceneManager().setMainStage(primaryStage);
        ViewFactory viewFactory = new ViewFactory();
        viewFactory.showLoginWindow();
    }
}
