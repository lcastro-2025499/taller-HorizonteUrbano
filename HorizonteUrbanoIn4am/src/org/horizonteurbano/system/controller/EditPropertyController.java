package org.horizonteurbano.system.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.horizonteurbano.system.models.*;
import org.horizonteurbano.system.repositories.*;
import org.horizonteurbano.system.utils.AlertInformation;

public class EditPropertyController implements Initializable {

    @FXML
    private TextField txtCode;
    @FXML
    private ComboBox<String> cmbType;
    @FXML
    private TextField txtArea;
    @FXML
    private TextField txtAddress;
    @FXML
    private ComboBox<String> cmbStatus;
    @FXML
    private TextField txtPrice;
    @FXML
    private Button btnSelectImage;
    @FXML
    private Label lblImagePath;

    private static final String UPLOAD_DIR = "uploads/properties/";
    private String selectedImagePath;
    private PropertyRepository propertyRepository;
    private PropertyTypeRepository propertyTypeRepository;
    private StateRepository stateRepository;
    private AlertInformation alertInformation;

    public EditPropertyController() {
        this.propertyRepository = new PropertyRepository();
        this.propertyTypeRepository = new PropertyTypeRepository();
        this.stateRepository = new StateRepository();
        this.alertInformation = new AlertInformation();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarTipos();
        cargarEstados();

        txtCode.setDisable(true);
    }

    private void cargarTipos() {
        List<PropertyType> types = propertyTypeRepository.getAllPropertyTypes();
        for (PropertyType type : types) {
            cmbType.getItems().add(type.getNameType());
        }
    }

    private void cargarEstados() {
        List<State> states = stateRepository.getAllStates();
        for (State state : states) {
            cmbStatus.getItems().add(state.getNameState());
        }
    }

    public void setPropertyData(Property property) {
        txtCode.setText(property.getInternalCode());
        txtAddress.setText(property.getAddress());
        txtArea.setText(String.valueOf(property.getArea()));
        txtPrice.setText(String.valueOf(property.getPrice()));

        if (property.getType() != null) {
            cmbType.getSelectionModel().select(property.getType().getNameType());
        }
        if (property.getState() != null) {
            cmbStatus.getSelectionModel().select(property.getState().getNameState());
        }
        selectedImagePath = property.getCoverUrl();
        lblImagePath.setText(selectedImagePath != null ? new File(selectedImagePath).getName() : "Ningún archivo seleccionado");
    }

    @FXML
    public void actionUpdateProperty(ActionEvent event) {
        try {
            if (txtAddress.getText().isEmpty() || txtArea.getText().isEmpty()
                    || txtPrice.getText().isEmpty() || cmbType.getSelectionModel().isEmpty()
                    || cmbStatus.getSelectionModel().isEmpty()) {

                alertInformation.viewAlert(2, "Datos Incompletos", "Por favor, llena todos los campos.", null);
                return;
            }

            Property updated = new Property();
            updated.setInternalCode(txtCode.getText());
            updated.setAddress(txtAddress.getText());
            updated.setArea(Double.parseDouble(txtArea.getText()));
            updated.setPrice(Double.parseDouble(txtPrice.getText()));

            PropertyType type = new PropertyType();
            type.setIdType(cmbType.getSelectionModel().getSelectedIndex() + 1);
            updated.setType(type);

            State state = new State();
            state.setIdState(cmbStatus.getSelectionModel().getSelectedIndex() + 1);
            updated.setState(state);
            updated.setCoverUrl(selectedImagePath);

            if (propertyRepository.updateProperty(updated)) {
                alertInformation.viewAlert(1, "Éxito", "Propiedad actualizada correctamente en el sistema.", null);
                cerrarVentana();
            } else {
                alertInformation.viewAlert(3, "Error", "No se pudo actualizar la propiedad en la base de datos.", null);
            }
        } catch (NumberFormatException e) {
            alertInformation.viewAlert(2, "Error de Formato", "El Área y el Precio deben ser números válidos.", null);
        }
    }

    @FXML
    public void actionCancel(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtCode.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void actionSelectImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen de Portada");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagenes (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(btnSelectImage.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf('.'));
            String newFileName = UUID.randomUUID().toString() + extension;
            Path destination = uploadPath.resolve(newFileName);

            Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

            selectedImagePath = destination.toString();
            lblImagePath.setText(selectedFile.getName());
        } catch (IOException e) {
            alertInformation.viewAlert(3, "Error", "No se pudo guardar la imagen seleccionada.", null);
            System.err.println("Error copying image: " + e.getMessage());
        }
    }
}
