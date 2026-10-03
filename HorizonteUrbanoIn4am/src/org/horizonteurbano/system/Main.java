package org.horizonteurbano.system;

import org.horizonteurbano.system.utils.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setResizable(true);
        ViewFactory.getInstance().setMainStage(primaryStage);
        ViewFactory.getInstance().showMainViewWindow();
    }
}
