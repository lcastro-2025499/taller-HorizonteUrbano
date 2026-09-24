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

import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.repositories.PropertyRepository;
import org.horizonteurbano.system.service.UserSession;
import org.horizonteurbano.system.utils.AlertInformation;
import org.horizonteurbano.system.utils.ViewFactory;

public class MainMenuController implements Initializable {

    @FXML
    private Button btnGoSearch;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnRegister;
    @FXML
    private Button btnLogout;

    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnSearch;

    @FXML
    private VBox card1;
    @FXML
    private VBox card2;
    @FXML
    private VBox card3;
    @FXML
    private VBox card4;

    @FXML
    private Label lblTitle1;
    @FXML
    private Label lblTitle2;
    @FXML
    private Label lblTitle3;
    @FXML
    private Label lblTitle4;

    @FXML
    private Label lblDesc1;
    @FXML
    private Label lblDesc2;
    @FXML
    private Label lblDesc3;
    @FXML
    private Label lblDesc4;

    @FXML
    private Label lblResults;

    private PropertyRepository propertyRepository;
    private AlertInformation alert;
    private ViewFactory viewFactory;
    private UserSession session;

    private List<Property> displayedProperties;

    public MainMenuController() {
        this.propertyRepository = new PropertyRepository();
        this.alert = new AlertInformation();
        this.viewFactory = ViewFactory.getInstance();
        this.session = UserSession.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateHeaderForSession();
        loadFeaturedProperties();
    }

    // Shows/hides header buttons depending on whether a user is logged in
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

    private void loadFeaturedProperties() {
        displayedProperties = propertyRepository.getAllActiveProperties();
        fillCard(card1, lblTitle1, lblDesc1, 0);
        fillCard(card2, lblTitle2, lblDesc2, 1);
        fillCard(card3, lblTitle3, lblDesc3, 2);
        fillCard(card4, lblTitle4, lblDesc4, 3);

        int count = Math.min(displayedProperties.size(), 4);
        lblResults.setText("Mostrando " + count + " de " + displayedProperties.size() + " propiedades");
    }

    private void fillCard(VBox card, Label titleLabel, Label descLabel, int index) {
        if (index >= displayedProperties.size()) {
            card.setVisible(false);
            card.setManaged(false);
            return;
        }
        Property p = displayedProperties.get(index);
        titleLabel.setText(p.getInternalCode() + " — " + p.getAddress());
        descLabel.setText("Área: " + p.getArea() + " m²   |   Precio: Q" + p.getPrice());
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
        viewFactory.showMainViewWindow();
    }

    @FXML
    public void actionSearchProperty(ActionEvent event) {
        String query = txtSearch.getText();
        if (query == null || query.trim().isEmpty()) {
            alert.viewAlert(2, "Búsqueda Vacía", "Por favor, ingresa una zona o palabra clave para buscar.", null);
            return;
        }
        // TODO: filter cards by query in a future iteration
        alert.viewAlert(1, "Búsqueda", "Buscando: " + query, null);
    }

    @FXML
    public void actionDetails1(ActionEvent event) {
        openDetails(0);
    }

    @FXML
    public void actionDetails2(ActionEvent event) {
        openDetails(1);
    }

    @FXML
    public void actionDetails3(ActionEvent event) {
        openDetails(2);
    }

    @FXML
    public void actionDetails4(ActionEvent event) {
        openDetails(3);
    }

    private void openDetails(int index) {
        if (displayedProperties == null || index >= displayedProperties.size()) {
            return;
        }
        Property p = displayedProperties.get(index);
        alert.viewAlert(1, "Detalles de la propiedad",
                p.getInternalCode() + "\n" + p.getAddress() + "\nÁrea: " + p.getArea() + " m²\nPrecio: Q" + p.getPrice(),
                null);
    }
}
