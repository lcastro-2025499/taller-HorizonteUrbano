package org.horizonteurbano.system.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertInformation {

    public AlertInformation() {
    }

    public void viewAlert(int alertType, String title, String message, String header) {

        AlertType alertTypeToShow = AlertType.NONE;

        switch (alertType) {
            case 1:
                alertTypeToShow = AlertType.INFORMATION;
                break;
            case 2:
                alertTypeToShow = AlertType.WARNING;
                break;
            case 3:
                alertTypeToShow = AlertType.ERROR;
                break;
            case 4:
                alertTypeToShow = AlertType.CONFIRMATION;
                break;
            default:
                alertTypeToShow = AlertType.NONE;
                System.out.println("Tipo de alerta no reconocido");
                break;
        }

        Alert alert = new Alert(alertTypeToShow);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
