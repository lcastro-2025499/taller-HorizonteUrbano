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
    @FXML private ListView<String> listStateStats;

    private PropertyRepository propertyRepository;

    public ReportsController() {
        this.propertyRepository = new PropertyRepository();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarMetricas();
    }

    private void cargarMetricas() {
        int total = propertyRepository.countActiveProperties();
        lblTotalProperties.setText(String.valueOf(total));

        double value = propertyRepository.getTotalInventoryValue();
        lblTotalValue.setText(String.format("$ %,.2f", value));

        Map<String, Integer> states = propertyRepository.countByState();
        listStateStats.getItems().clear();
        for (Map.Entry<String, Integer> entry : states.entrySet()) {
            listStateStats.getItems().add(entry.getKey() + ": " + entry.getValue() + " propiedades registradas");
        }
    }

    @FXML
    public void actionGoBack(ActionEvent event) {
        ViewFactory.getInstance().showSearchPropertyWindow(); 
    }
}