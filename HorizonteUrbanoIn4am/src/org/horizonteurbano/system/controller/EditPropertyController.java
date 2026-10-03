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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.models.PropertyType;
import org.horizonteurbano.system.models.State;
import org.horizonteurbano.system.repositories.PropertyRepository;
import org.horizonteurbano.system.repositories.PropertyTypeRepository;
import org.horizonteurbano.system.repositories.StateRepository;
import org.horizonteurbano.system.utils.AlertInformation;

public class EditPropertyController implements Initializable {

    @FXML private ComboBox<PropertyType> cmbType;
    @FXML private TextField txtArea;
    @FXML private TextField txtAddress;
    @FXML private ComboBox<State> cmbStatus;
    @FXML private TextField txtPrice;
    @FXML private Button btnSelectImage;
    @FXML private Label lblImagePath;
    @FXML private Button btnCancel;

    private static final String UPLOAD_DIR = "uploads/properties/";
    private String selectedImagePath;
    private String propertyCode; // Guarda el código interno sin depender de un TextField
    
    private PropertyRepository propertyRepository;
    private PropertyTypeRepository propertyTypeRepository;
    private StateRepository stateRepository;
    private AlertInformation alertInformation;

    private Runnable onSaved;

    public EditPropertyController() {
        this.propertyRepository = new PropertyRepository();
        this.propertyTypeRepository = new PropertyTypeRepository();
        this.stateRepository = new StateRepository();
        this.alertInformation = new AlertInformation();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureComboConverters();
        cargarTipos();
        cargarEstados();
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    private void configureComboConverters() {
        cmbType.setConverter(new StringConverter<PropertyType>() {
            @Override public String toString(PropertyType type) { return type == null ? "" : type.getNameType(); }
            @Override public PropertyType fromString(String string) { return null; }
        });

        cmbStatus.setConverter(new StringConverter<State>() {
            @Override public String toString(State state) { return state == null ? "" : state.getNameState(); }
            @Override public State fromString(String string) { return null; }
        });
    }

    private void cargarTipos() {
        List<PropertyType> types = propertyTypeRepository.getAllPropertyTypes();
        cmbType.getItems().setAll(types);
    }

    private void cargarEstados() {
        List<State> states = stateRepository.getAllStates();
        cmbStatus.getItems().setAll(states);
    }

    public void setPropertyData(Property property) {
        this.propertyCode = property.getInternalCode();
        txtAddress.setText(property.getAddress());
        txtArea.setText(String.valueOf(property.getArea()));
        txtPrice.setText(String.valueOf(property.getPrice()));

        if (property.getType() != null) {
            for (PropertyType t : cmbType.getItems()) {
                if (t.getIdType() == property.getType().getIdType()) {
                    cmbType.getSelectionModel().select(t);
                    break;
                }
            }
        }
        if (property.getState() != null) {
            for (State s : cmbStatus.getItems()) {
                if (s.getIdState() == property.getState().getIdState()) {
                    cmbStatus.getSelectionModel().select(s);
                    break;
                }
            }
        }
        selectedImagePath = property.getCoverUrl();
        lblImagePath.setText(selectedImagePath != null ? new File(selectedImagePath).getName() : "Ningún archivo seleccionado");
    }

    @FXML
    public void actionUpdateProperty(ActionEvent event) {
        try {
            PropertyType selectedType = cmbType.getSelectionModel().getSelectedItem();
            State selectedState = cmbStatus.getSelectionModel().getSelectedItem();

            if (txtAddress.getText().isEmpty() || txtArea.getText().isEmpty()
                    || txtPrice.getText().isEmpty() || selectedType == null
                    || selectedState == null) {
                alertInformation.viewAlert(2, "Datos Incompletos", "Por favor, llena todos los campos.", null);
                return;
            }

            Property updated = new Property();
            updated.setInternalCode(propertyCode); // Usamos la variable privada
            updated.setAddress(txtAddress.getText());
            updated.setArea(Double.parseDouble(txtArea.getText()));
            updated.setPrice(Double.parseDouble(txtPrice.getText()));
            updated.setType(selectedType);
            updated.setState(selectedState);
            updated.setCoverUrl(selectedImagePath);

            if (propertyRepository.updateProperty(updated)) {
                alertInformation.viewAlert(1, "Éxito", "Propiedad actualizada correctamente en el sistema.", null);
                cerrarVentana();
                if (onSaved != null) {
                    onSaved.run();
                }
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
        // Usamos btnCancel (que sí existe en el FXML) para obtener la ventana
        Stage stage = (Stage) btnCancel.getScene().getWindow();
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