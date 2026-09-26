package org.horizonteurbano.system.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.geometry.Insets;

import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.repositories.PropertyRepository;
import org.horizonteurbano.system.service.UserSession;
import org.horizonteurbano.system.utils.AlertInformation;
import org.horizonteurbano.system.utils.ViewFactory;

public class MainMenuController implements Initializable {

    // Controles de Sesión y Navegación
    @FXML private Button btnGoSearch;
    @FXML private Button btnLogin;
    @FXML private Button btnRegister;
    @FXML private Button btnLogout;
    
    // Controles de Búsqueda y Catálogo
    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;
    @FXML private FlowPane flowPanePropertyCatalog;
    @FXML private Label lblResults;

    private PropertyRepository propertyRepository;
    private AlertInformation alert;
    private ViewFactory viewFactory;
    private UserSession session;

    public MainMenuController() {
        this.propertyRepository = new PropertyRepository();
        this.alert = new AlertInformation();
        this.viewFactory = ViewFactory.getInstance();
        this.session = UserSession.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateHeaderForSession();
        cargarCatalogo(""); 
    }

    
    private void updateHeaderForSession() {
        boolean loggedIn = session.isLoggedIn();
        setVisible(btnLogin, !loggedIn);
        setVisible(btnRegister, !loggedIn);
        setVisible(btnLogout, loggedIn);
        setVisible(btnGoSearch, loggedIn);
    }

    private void setVisible(Node node, boolean visible) {
        if (node != null) {
            node.setVisible(visible);
            node.setManaged(visible);
        }
    }


    private void cargarCatalogo(String filtro) {
        flowPanePropertyCatalog.getChildren().clear();
        List<Property> propiedades = propertyRepository.getAllActiveProperties();
        int coincidencias = 0;

        for (Property p : propiedades) {
            boolean coincideFiltro = filtro.isEmpty() || 
                                     p.getAddress().toLowerCase().contains(filtro.toLowerCase()) || 
                                     p.getInternalCode().toLowerCase().contains(filtro.toLowerCase());
            
            if (coincideFiltro) {
                flowPanePropertyCatalog.getChildren().add(crearTarjeta(p));
                coincidencias++;
            }
        }
        
        if (coincidencias == 0) {
            Label lblVacio = new Label("No se encontraron propiedades con la búsqueda: " + filtro);
            lblVacio.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
            flowPanePropertyCatalog.getChildren().add(lblVacio);
        }
        
        if (lblResults != null) {
            lblResults.setText("Mostrando " + coincidencias + " propiedades");
        }
    }

    private VBox crearTarjeta(Property p) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        card.setPrefWidth(320);

        Label lblTitulo = new Label("Código: " + p.getInternalCode());
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2a3b4c;");
        
        Label lblDireccion = new Label(p.getAddress());
        lblDireccion.setStyle("-fx-text-fill: #666666;");
        lblDireccion.setWrapText(true);
        
        Label lblAreaPrecio = new Label("Área: " + p.getArea() + " m² | Precio: Q" + p.getPrice());
        lblAreaPrecio.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #349626;");

        Button btnDetalle = new Button("Solicitar Información");
        btnDetalle.setMaxWidth(Double.MAX_VALUE);
        btnDetalle.setStyle("-fx-background-color: #472820; -fx-text-fill: white; -fx-border-radius: 20;");
        
        btnDetalle.setOnAction(e -> alert.viewAlert(1, "Detalles de la Propiedad", 
            "Inmueble: " + p.getInternalCode() + "\nUbicación: " + p.getAddress() + "\n\nUn asesor se pondrá en contacto pronto.", null));

        card.getChildren().addAll(lblTitulo, lblDireccion, lblAreaPrecio, btnDetalle);
        return card;
    }

    @FXML
    public void actionSearchProperty(ActionEvent event) {
        String query = txtSearch.getText();
        if (query == null || query.trim().isEmpty()) {
            alert.viewAlert(2, "Búsqueda Vacía", "Por favor, ingresa una zona o palabra clave para buscar.", null);
            cargarCatalogo(""); 
            return;
        }
        cargarCatalogo(query.trim()); 
    }


    @FXML
    public void actionGoToSearch(ActionEvent event) {
        viewFactory.showSearchPropertyWindow();
    }

    @FXML
    public void actionLogin(ActionEvent event) {
        viewFactory.showLoginWindow();
    }

    @FXML
    public void actionRegister(ActionEvent event) {
        viewFactory.showRegisterWindow();
    }

    @FXML
    public void actionLogout(ActionEvent event) {
        session.logout();
        updateHeaderForSession();
        alert.viewAlert(1, "Sesión Cerrada", "Has cerrado sesión correctamente.", null);
    }
}