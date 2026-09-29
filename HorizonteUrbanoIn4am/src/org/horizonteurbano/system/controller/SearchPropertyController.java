package org.horizonteurbano.system.controller;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import org.horizonteurbano.system.config.ConnectionDB;
import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.models.PropertyType;
import org.horizonteurbano.system.models.State;
import org.horizonteurbano.system.repositories.PropertyRepository;
import org.horizonteurbano.system.repositories.StateRepository;
import org.horizonteurbano.system.service.BrochureService;
import org.horizonteurbano.system.service.UserSession;
import org.horizonteurbano.system.utils.AlertInformation;
import org.horizonteurbano.system.utils.ViewFactory;

public class SearchPropertyController implements Initializable {

    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<PropertyType> cmbType;
    @FXML
    private ComboBox<State> cmbStatus;
    @FXML
    private TextField txtPriceMin;
    @FXML
    private TextField txtPriceMax;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnViewDetails;

    @FXML
    private TableView<Property> tblProperties;
    @FXML
    private TableColumn<Property, String> colCode;
    @FXML
    private TableColumn<Property, String> colAddress;
    @FXML
    private TableColumn<Property, String> colType;
    @FXML
    private TableColumn<Property, Double> colArea;
    @FXML
    private TableColumn<Property, Double> colPrice;
    @FXML
    private TableColumn<Property, String> colStatus;

    @FXML
    private Label lblResults;

    private final ObservableList<Property> propertyList = FXCollections.observableArrayList();
    private final AlertInformation alert = new AlertInformation();
    private final ViewFactory viewFactory = ViewFactory.getInstance();
    private PropertyRepository propertyRepository;
    private StateRepository stateRepository;

    public SearchPropertyController() {
        this.propertyRepository = new PropertyRepository();
        this.stateRepository = new StateRepository();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureComboConverters();
        setupTableColumns();
        loadComboBoxes();
        handleSearch();
    }

    private void setupTableColumns() {
        colCode.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getInternalCode()));
        colAddress.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        colArea.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getArea()));
        colPrice.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrice()));

        colType.setCellValueFactory(cellData -> {
            PropertyType type = cellData.getValue().getType();
            if (type != null && type.getNameType() != null && !type.getNameType().isEmpty()) {
                return new SimpleStringProperty(type.getNameType());
            }
            return new SimpleStringProperty("N/A");
        });

        colStatus.setCellValueFactory(cellData -> {
            State state = cellData.getValue().getState();
            if (state != null && state.getNameState() != null && !state.getNameState().isEmpty()) {
                return new SimpleStringProperty(state.getNameState());
            }
            return new SimpleStringProperty("N/A");
        });
    }

    private void loadComboBoxes() {
        cmbType.getItems().clear();
        PropertyType allTypes = new PropertyType();
        allTypes.setIdType(0);
        allTypes.setNameType("Todos");
        cmbType.getItems().add(allTypes);

        cmbStatus.getItems().clear();
        State allStates = new State();
        allStates.setIdState(0);
        allStates.setNameState("Todos");
        cmbStatus.getItems().add(allStates);

        try (Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection()) {

            try (CallableStatement stmt = conn.prepareCall("{call sp_read_propertytypes()}"); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PropertyType type = new PropertyType();
                    type.setIdType(rs.getInt("id_type"));
                    type.setNameType(rs.getString("name_type"));
                    cmbType.getItems().add(type);
                }
            }

            try (CallableStatement stmt = conn.prepareCall("{call sp_read_states()}"); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    State state = new State();
                    state.setIdState(rs.getInt("id_state"));
                    state.setNameState(rs.getString("name_state"));
                    cmbStatus.getItems().add(state);
                }
            }

            cmbType.getSelectionModel().selectFirst();
            cmbStatus.getSelectionModel().selectFirst();

        } catch (SQLException e) {
            alert.viewAlert(3, "Error de Base de Datos", "No se pudieron cargar los filtros: " + e.getMessage(), null);
        }
    }

    @FXML
    public void handleSearch() {
        try {
            String searchText = txtSearch.getText().trim().isEmpty() ? null : txtSearch.getText().trim();
            Double minPrice = txtPriceMin.getText().trim().isEmpty() ? null : Double.parseDouble(txtPriceMin.getText().trim());
            Double maxPrice = txtPriceMax.getText().trim().isEmpty() ? null : Double.parseDouble(txtPriceMax.getText().trim());

            PropertyType selectedType = cmbType.getSelectionModel().getSelectedItem();
            Integer typeId = (selectedType != null && selectedType.getIdType() != 0) ? selectedType.getIdType() : null;

            State selectedState = cmbStatus.getSelectionModel().getSelectedItem();
            Integer stateId = (selectedState != null && selectedState.getIdState() != 0) ? selectedState.getIdState() : null;

            loadProperties(searchText, minPrice, maxPrice, null, null, stateId, typeId);
        } catch (NumberFormatException e) {
            alert.viewAlert(2, "Error de Formato", "Por favor, ingresa números válidos en los campos de precio.", null);
        } catch (SQLException e) {
            alert.viewAlert(3, "Error de Búsqueda", e.getMessage(), null);
        }
    }

    private void loadProperties(String searchText, Double minPrice, Double maxPrice,
            Double minArea, Double maxArea, Integer stateId, Integer propertyTypeId) throws SQLException {
        propertyList.clear();

        try (Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection(); CallableStatement stmt = conn.prepareCall("{call sp_search_properties(?, ?, ?, ?, ?, ?, ?)}")) {

            stmt.setString(1, searchText);
            if (minPrice != null) {
                stmt.setDouble(2, minPrice);
            } else {
                stmt.setNull(2, Types.DECIMAL);
            }
            if (maxPrice != null) {
                stmt.setDouble(3, maxPrice);
            } else {
                stmt.setNull(3, Types.DECIMAL);
            }
            if (minArea != null) {
                stmt.setDouble(4, minArea);
            } else {
                stmt.setNull(4, Types.FLOAT);
            }
            if (maxArea != null) {
                stmt.setDouble(5, maxArea);
            } else {
                stmt.setNull(5, Types.FLOAT);
            }
            if (stateId != null) {
                stmt.setInt(6, stateId);
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            if (propertyTypeId != null) {
                stmt.setInt(7, propertyTypeId);
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Property prop = new Property();
                    prop.setIdProperty(rs.getInt("id_property"));
                    prop.setInternalCode(rs.getString("internal_code"));
                    prop.setAddress(rs.getString("address"));
                    prop.setArea(rs.getDouble("area_m2"));
                    prop.setPrice(rs.getDouble("price"));
                    prop.setActive(rs.getBoolean("active"));

                    Timestamp dateRegTs = rs.getTimestamp("date_register");
                    if (dateRegTs != null) {
                        prop.setDateRegister(dateRegTs.toLocalDateTime());
                    }

                    Timestamp updateDateTs = rs.getTimestamp("update_date");
                    if (updateDateTs != null) {
                        prop.setUpdateDate(updateDateTs.toLocalDateTime());
                    }

                    prop.setCoverUrl(rs.getString("cover_url"));
                    prop.setIdUser(rs.getString("id_user"));

                    PropertyType type = new PropertyType();
                    type.setIdType(rs.getInt("id_property_type"));

                    for (PropertyType pt : cmbType.getItems()) {
                        if (pt.getIdType() == type.getIdType()) {
                            type.setNameType(pt.getNameType());
                            break;
                        }
                    }
                    prop.setType(type);

                    State state = new State();
                    state.setIdState(rs.getInt("id_state"));

                    for (State st : cmbStatus.getItems()) {
                        if (st.getIdState() == state.getIdState()) {
                            state.setNameState(st.getNameState());
                            break;
                        }
                    }
                    prop.setState(state);

                    propertyList.add(prop);
                }
            }
        }

        tblProperties.setItems(propertyList);
        lblResults.setText("Resultados: " + propertyList.size() + " propiedades encontradas");
    }

    @FXML
    public void actionChangeStatus(ActionEvent event) {
        Property selectedProperty = tblProperties.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            alert.viewAlert(2, "Selección Requerida", "Por favor, selecciona una propiedad de la tabla primero.", null);
            return;
        }

        List<State> states = stateRepository.getAllStates();
        List<String> stateNames = new ArrayList<>();
        Map<String, Integer> stateMap = new HashMap<>();

        for (State state : states) {
            stateNames.add(state.getNameState());
            stateMap.put(state.getNameState(), state.getIdState());
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(stateNames.get(0), stateNames);
        dialog.setTitle("Cambiar Estado");
        dialog.setHeaderText("Propiedad seleccionada: " + selectedProperty.getInternalCode());
        dialog.setContentText("Selecciona el nuevo estado comercial:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            int newIdState = stateMap.get(result.get());
            if (propertyRepository.changeStatus(selectedProperty.getInternalCode(), newIdState)) {
                alert.viewAlert(1, "Éxito", "El estado se actualizó correctamente.", null);
                handleSearch();
            } else {
                alert.viewAlert(3, "Error", "No se pudo actualizar el estado en la base de datos.", null);
            }
        }
    }

    @FXML
    public void actionDelete(ActionEvent event) {
        Property selectedProperty = tblProperties.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            alert.viewAlert(2, "Selección Requerida", "Por favor, selecciona una propiedad de la tabla.", null);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Baja");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Estás seguro de que deseas dar de baja " + selectedProperty.getInternalCode() + "? (Borrado lógico)");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (propertyRepository.deactivateProperty(selectedProperty.getInternalCode())) {
                alert.viewAlert(1, "Éxito", "Propiedad dada de baja.", null);
                handleSearch();
            } else {
                alert.viewAlert(3, "Error", "No se pudo dar de baja la propiedad.", null);
            }
        }
    }

    @FXML
    public void actionEdit(ActionEvent event) {
        Property selectedProperty = tblProperties.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            alert.viewAlert(2, "Selección Requerida", "Por favor, selecciona una propiedad para editar.", null);
            return;
        }
        ViewFactory.getInstance().showEditPropertyWindow(selectedProperty);
    }

    @FXML
    public void actionExportPDF(ActionEvent event) {
        Property selectedProperty = tblProperties.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            alert.viewAlert(2, "Selección Requerida", "Por favor, selecciona una propiedad de la tabla para exportar su ficha.", null);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Brochure PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documento PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Ficha_" + selectedProperty.getInternalCode() + ".pdf");

        java.io.File file = fileChooser.showSaveDialog(tblProperties.getScene().getWindow());

        if (file != null) {
            BrochureService brochureService = new BrochureService();
            if (brochureService.exportBrochure(selectedProperty, file)) {
                alert.viewAlert(1, "Exportación Exitosa", "El PDF se generó correctamente en tu equipo.", null);
            } else {
                alert.viewAlert(3, "Error", "No se pudo generar el archivo PDF.", null);
            }
        }
    }

    @FXML
    private void handleViewDetails() {
        Property selected = tblProperties.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert.viewAlert(2, "Selección requerida", "Por favor, selecciona una propiedad de la tabla antes de ver los detalles.", null);
            return;
        }
        alert.viewAlert(1, "Detalles de la propiedad",
                selected.getInternalCode() + "\n" + selected.getAddress() + "\nÁrea: " + selected.getArea() + " m²\nPrecio: Q" + selected.getPrice(),
                null);
    }

    @FXML
    public void goBack() {
        if (UserSession.getInstance().isAsesor()) {
            viewFactory.showMainViewWindow();
        } else {
            viewFactory.showDashboardWindow();
        }
    }

    @FXML
    public void goToMainMenu() {
        viewFactory.showMainViewWindow();
    }

    private void configureComboConverters() {
        cmbType.setConverter(new StringConverter<PropertyType>() {
            @Override
            public String toString(PropertyType type) {
                return type == null ? "" : type.getNameType();
            }

            @Override
            public PropertyType fromString(String string) {
                return null;
            }
        });

        cmbStatus.setConverter(new StringConverter<State>() {
            @Override
            public String toString(State state) {
                return state == null ? "" : state.getNameState();
            }

            @Override
            public State fromString(String string) {
                return null;
            }
        });
    }
}
