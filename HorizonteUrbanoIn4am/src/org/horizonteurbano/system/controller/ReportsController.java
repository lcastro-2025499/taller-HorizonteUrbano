package org.horizonteurbano.system.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.horizonteurbano.system.repositories.PropertyRepository;
import org.horizonteurbano.system.utils.ViewFactory;

public class ReportsController implements Initializable {

    @FXML private Label lblTotalProperties;
    @FXML private Label lblTotalValue;
    
    // Nuevas etiquetas para US4.1
    @FXML private Label lblSoldCount;
    @FXML private Label lblSoldValue;
    @FXML private Label lblRentedCount;
    @FXML private Label lblRentedValue;
    
    @FXML private ListView<String> listStateStats;
    @FXML private ListView<String> listTypeStats;

    private PropertyRepository propertyRepository;

    public ReportsController() {
        this.propertyRepository = new PropertyRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarMetricas();
    }

    private void cargarMetricas() {
        // 1. Métricas Generales
        int total = propertyRepository.countActiveProperties();
        lblTotalProperties.setText(String.valueOf(total));

        double totalValue = propertyRepository.getTotalInventoryValue();
        lblTotalValue.setText(String.format("Q %,.2f", totalValue));

        // 2. Métricas US4.1: Vendidas
        int soldCount = propertyRepository.countSoldProperties();
        double soldValue = propertyRepository.getSoldValue();
        lblSoldCount.setText(String.valueOf(soldCount));
        lblSoldValue.setText(String.format("Q %,.2f", soldValue));

        // 3. Métricas US4.1: Arrendadas
        int rentedCount = propertyRepository.countRentedProperties();
        double rentedValue = propertyRepository.getRentedValue();
        lblRentedCount.setText(String.valueOf(rentedCount));
        lblRentedValue.setText(String.format("Q %,.2f", rentedValue));

        // 4. Desgloses
        Map<String, Integer> states = propertyRepository.countByState();
        listStateStats.getItems().clear();
        for (Map.Entry<String, Integer> entry : states.entrySet()) {
            listStateStats.getItems().add(entry.getKey() + ": " + entry.getValue() + " propiedades");
        }

        Map<String, Integer> types = propertyRepository.countByType();
        listTypeStats.getItems().clear();
        for (Map.Entry<String, Integer> entry : types.entrySet()) {
            listTypeStats.getItems().add(entry.getKey() + ": " + entry.getValue() + " propiedades");
        }
    }

    @FXML
    public void actionGoBack(ActionEvent event) {
        ViewFactory.getInstance().showSearchPropertyWindow();
    }
}