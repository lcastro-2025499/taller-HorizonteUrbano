
package org.horizonteurbano.system.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;

import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.utils.AlertInformation;

public class MainMenuController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private ScrollPane scrollPaneCatalog;
    @FXML private FlowPane flowPanePropertyCatalog;
    
    @FXML private ImageView imageViewLogo;
    @FXML private TextField textFieldSearch;
    
    @FXML private Button buttonLogin;
    @FXML private Button buttonLogout;
    
    @FXML private ImageView imageViewProperty1;
    @FXML private ImageView imageViewIcon1;
    @FXML private Button buttonSaveProperty1;
    
    @FXML private ImageView imageViewProperty2;
    @FXML private ImageView imageViewIcon2;
    @FXML private Button buttonSaveProperty2;
    @FXML private Button buttonViewMore;

    private AlertInformation alerta;

    public MainMenuController() {
        this.alerta = new AlertInformation();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicialización
    }

    @FXML
    public void actionLogin(ActionEvent event) {
        System.out.println("Navegando a la pantalla de inicio de sesión...");
    }

    @FXML
    public void actionLogout(ActionEvent event) {
        alerta.viewAlert(1, "Sesión Cerrada", "Has cerrado sesión correctamente de Horizonte Urbano.", null);
    }

    @FXML
    public void actionSearchProperty(ActionEvent event) {
        String query = textFieldSearch.getText();
        if(query != null && !query.trim().isEmpty()) {
            System.out.println("Buscando en el catálogo: " + query);
        } else {
            alerta.viewAlert(2, "Búsqueda Vacía", "Por favor, ingresa una zona o palabra clave para buscar.", null);
        }
    }

    @FXML
    public void actionSaveProperty1(ActionEvent event) {
        alerta.viewAlert(1, "Propiedad Guardada", "Se ha guardado 'Estudio de oferta' en tus propiedades favoritas.", null);
    }

    @FXML
    public void actionSaveProperty2(ActionEvent event) {
        alerta.viewAlert(1, "Propiedad Guardada", "Se ha guardado 'Demanda Macro' en tus propiedades favoritas.", null);
    }
    
    @FXML
    public void actionViewMoreDetails(ActionEvent event) {
        System.out.println("Abriendo la ficha detallada de la propiedad...");
    }
}