package org.horizonteurbano.system.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carga la vista de registro FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/horizonteurbano/system/view/Register.View.fxml"));
        Parent root = loader.load();

        // Define la escena fijando el ancho en 600px y el alto en 400px
        Scene scene = new Scene(root, 600, 700);
        
        // Carga la hoja de estilos CSS
        String css = getClass().getResource("/org/horizonteurbano/system/styles/RegisterStyles.css").toExternalForm();
        scene.getStylesheets().add(css);

        // Configuración de la ventana principal
        primaryStage.setTitle("Horizonte Urbano - Registro");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Mantiene el tamaño fijo de 600x400
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}