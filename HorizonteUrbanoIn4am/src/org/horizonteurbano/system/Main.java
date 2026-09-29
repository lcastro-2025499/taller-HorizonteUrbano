package org.horizonteurbano.system;

import org.horizonteurbano.system.utils.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;
import org.horizonteurbano.system.utils.SceneManager;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        SceneManager.getInstanceSceneManager().setMainStage(primaryStage);
        primaryStage.setResizable(true);
        ViewFactory.getInstance().setMainStage(primaryStage);
        ViewFactory.getInstance().showMainViewWindow();
    }
}
