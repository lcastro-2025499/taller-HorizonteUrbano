package org.horizonteurbano.system.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.models.PropertyType;
import org.horizonteurbano.system.models.State;
import org.horizonteurbano.system.repositories.PropertyRepository;
import org.horizonteurbano.system.repositories.PropertyTypeRepository;
import org.horizonteurbano.system.repositories.StateRepository;
import org.horizonteurbano.system.utils.AlertInformation;
import org.horizonteurbano.system.utils.ViewFactory; // Injected utility for navigation

public class DashboardController implements Initializable {

    @FXML private AnchorPane rootPane;
    @FXML private VBox mainPanel;
    @FXML private HBox headerBox;
    @FXML private ImageView imgLogo;
    @FXML private Label lblTitle;
    @FXML private ImageView imgProfile;
    @FXML private Label lblUser;
    
    @FXML private VBox formPanel;
    @FXML private Label lblFormTitle;
    @FXML private HBox formColumns;
    @FXML private VBox colLeft;
    @FXML private Label lblCode;
    @FXML private TextField txtCode;
    @FXML private Label lblType;
    @FXML private ComboBox<String> cmbType;
    @FXML private Label lblArea;
    @FXML private TextField txtArea;
    
    @FXML private VBox colRight;
    @FXML private Label lblAddress;
    @FXML private TextField txtAddress;
    @FXML private Label lblStatus;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private Label lblPrice;
    @FXML private TextField txtPrice;
    
    @FXML private VBox descriptionSection;
    @FXML private Label lblDescription;
    @FXML private TextArea txtDescription;
    
    @FXML private HBox buttonsBox;
    @FXML private Button btnClear;
    @FXML private Button btnCancel;
    @FXML private Button btnSave;
    
    @FXML private VBox recordsPanel;
    @FXML private Label lblRecords;
    @FXML private ListView<String> listRecords;
    
    @FXML private HBox footerBox;
    @FXML private Label lblFooterStatus;
    @FXML private ImageView imgClock;

    private PropertyRepository propertyRepository;
    private PropertyTypeRepository propertyTypeRepository;
    private StateRepository stateRepository;
    private AlertInformation alertInformation;
    private ViewFactory viewFactory;

    public DashboardController() {
        this.propertyRepository = new PropertyRepository();
        this.propertyTypeRepository = new PropertyTypeRepository();
        this.stateRepository = new StateRepository();
        this.alertInformation = new AlertInformation();
        this.viewFactory = new ViewFactory(); // Initialization for screen routing[cite: 6]
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadPropertyTypes();
        loadPropertyStates();
        setupButtonActions();
    }

    private void loadPropertyTypes() {
        cmbType.getItems().clear();
        List<PropertyType> types = propertyTypeRepository.getAllPropertyTypes();
        for (PropertyType type : types) {
            cmbType.getItems().add(type.getNameType());
        }
    }

    private void loadPropertyStates() {
        cmbStatus.getItems().clear();
        List<State> states = stateRepository.getAllStates();
        for (State state : states) {
            cmbStatus.getItems().add(state.getNameState());
        }
    }

    private void setupButtonActions() {
        btnSave.setOnAction(this::saveProperty);
        btnClear.setOnAction(this::clearForm);
        btnCancel.setOnAction(this::cancelAction);
    }

    private void saveProperty(ActionEvent event) {
        try {
            if (txtCode.getText().isEmpty() || txtAddress.getText().isEmpty() || 
                txtArea.getText().isEmpty() || txtPrice.getText().isEmpty() || 
                cmbType.getSelectionModel().isEmpty() || cmbStatus.getSelectionModel().isEmpty()) {
                
                alertInformation.viewAlert(2, "Datos Incompletos", "Por favor, llena todos los campos obligatorios del formulario.", null);
                return;
            }

            Property newProperty = new Property();
            newProperty.setInternalCode(txtCode.getText());
            newProperty.setAddress(txtAddress.getText());
            newProperty.setArea(Double.parseDouble(txtArea.getText()));
            newProperty.setPrice(Double.parseDouble(txtPrice.getText()));

            PropertyType type = new PropertyType();
            type.setIdType(cmbType.getSelectionModel().getSelectedIndex() + 1); 
            newProperty.setType(type);
            
            State state = new State();
            state.setIdState(cmbStatus.getSelectionModel().getSelectedIndex() + 1);
            newProperty.setState(state);
            
            newProperty.setActive(true);
            newProperty.setIdUser("CURRENT_USER_ID"); 

            if (propertyRepository.saveProperty(newProperty)) {
                alertInformation.viewAlert(1, "Guardado Exitoso", "La propiedad se ha registrado correctamente en el inventario.", null);
                listRecords.getItems().add(0, "Guardado: " + newProperty.getInternalCode() + " - " + newProperty.getAddress());
                clearForm(null);
            } else {
                alertInformation.viewAlert(3, "Error de Registro", "Ocurrió un problema al guardar en la base de datos.", null);
            }

        } catch (NumberFormatException e) {
            alertInformation.viewAlert(2, "Error de Formato", "El Área y el Precio deben contener únicamente números.", null);
        }
    }

    private void clearForm(ActionEvent event) {
        txtCode.clear();
        txtAddress.clear();
        txtArea.clear();
        txtPrice.clear();
        txtDescription.clear();
        cmbType.getSelectionModel().clearSelection();
        cmbStatus.getSelectionModel().clearSelection();
    }

    private void cancelAction(ActionEvent event) {
        clearForm(event);
        
        // Notification and dynamic routing via ViewFactory[cite: 6]
        alertInformation.viewAlert(1, "Cancelado", "Operación cancelada. Redirigiendo al inventario de búsqueda...", null);
        
        // Note: Change 'showSearchPropertyView()' to the exact method name mapped in your ViewFactory class
        // viewFactory.showSearchPropertyView(); 
    }
}
