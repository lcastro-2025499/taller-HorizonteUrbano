package org.horizonteurbano.system.controller;

import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.repositories.PropertyRepository;
import org.horizonteurbano.system.service.UserSession;
import org.horizonteurbano.system.utils.AlertInformation;
import org.horizonteurbano.system.utils.ViewFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    @FXML private Button btnGoSearch;
    @FXML private Button btnLogin;
    @FXML private Button btnRegister;
    @FXML private Button btnLogout;

    @FXML private TextField txtSearch;
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
        loadCatalog("");
    }

    private void updateHeaderForSession() {
        boolean loggedIn = session.isLoggedIn();

        setVisible(btnLogin, !loggedIn);
        setVisible(btnRegister, !loggedIn);

        setVisible(btnLogout, loggedIn);

        // Solo los roles autorizados pueden acceder a la búsqueda avanzada.
        boolean canSearch = false;
        
        if (loggedIn && session.getCurrentUser() != null && session.getCurrentUser().getRole() != null) {
            int roleId = session.getCurrentUser().getRole().getIdRole();
            if (roleId == 1 || roleId == 2 || roleId == 3) {
                canSearch = true;
            }
        }
        
        setVisible(btnGoSearch, canSearch);
    }

    private void setVisible(Node node, boolean visible) {
        if (node != null) {
            node.setVisible(visible);
            node.setManaged(visible);
        }
    }

    private void loadCatalog(String filter) {
        if (flowPanePropertyCatalog == null) return;
        
        flowPanePropertyCatalog.getChildren().clear();
        List<Property> properties = propertyRepository.getAllActiveProperties();
        int matchCount = 0;

        for (Property property : properties) {
            boolean matchesFilter = filter.isEmpty()
                    || property.getAddress().toLowerCase().contains(filter.toLowerCase())
                    || property.getInternalCode().toLowerCase().contains(filter.toLowerCase());
            
            if (matchesFilter) {
                flowPanePropertyCatalog.getChildren().add(createPropertyCard(property));
                matchCount++;
            }
        }
        
        if (matchCount == 0) {
            Label emptyResultLabel = new Label("No se encontraron propiedades con la búsqueda: " + filter);
            emptyResultLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
            flowPanePropertyCatalog.getChildren().add(emptyResultLabel);
        }
        
        if (lblResults != null) {
            lblResults.setText("Mostrando " + matchCount + " propiedades");
        }
    }

    private VBox createPropertyCard(Property property) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        card.setPrefWidth(320);

        Label titleLabel = new Label("Código: " + property.getInternalCode());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2a3b4c;");
        
        Label addressLabel = new Label(property.getAddress());
        addressLabel.setStyle("-fx-text-fill: #666666;");
        addressLabel.setWrapText(true);
        
        Label areaAndPriceLabel = new Label("Área: " + property.getArea() + " m² | Precio: Q" + property.getPrice());
        areaAndPriceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #349626;");

        Button detailsButton = new Button("Solicitar Información");
        detailsButton.setMaxWidth(Double.MAX_VALUE);
        detailsButton.setStyle("-fx-background-color: #472820; -fx-text-fill: white; -fx-border-radius: 20;");
        
        detailsButton.setOnAction(e -> alert.viewAlert(1, "Detalles de la Propiedad",
            "Inmueble: " + property.getInternalCode() + "\nUbicación: " + property.getAddress() + "\n\nUn asesor se pondrá en contacto pronto.", null));

        card.getChildren().addAll(titleLabel, addressLabel, areaAndPriceLabel, detailsButton);
        return card;
    }

    @FXML
    public void actionSearchProperty(ActionEvent event) {
        String query = txtSearch.getText();
        if (query == null || query.trim().isEmpty()) {
            alert.viewAlert(2, "Búsqueda Vacía", "Por favor, ingresa una zona o palabra clave para buscar.", null);
            loadCatalog("");
            return;
        }
        loadCatalog(query.trim());
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