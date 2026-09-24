package org.horizonteurbano.system.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.horizonteurbano.system.config.ConnectionDB;
import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.models.PropertyType;
import org.horizonteurbano.system.models.State;
import org.horizonteurbano.system.utils.AlertInformation;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ResourceBundle;
import org.horizonteurbano.system.service.UserSession;
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
    @FXML
    private Button btnViewDetails;

    private final ObservableList<Property> propertyList = FXCollections.observableArrayList();
    private final AlertInformation alert = new AlertInformation();
    private final ViewFactory viewFactory = ViewFactory.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadComboBoxes();
        loadProperties();
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

    private void loadProperties() {
        try {
            loadProperties(null, null, null, null, null, null, null);
        } catch (SQLException e) {
            alert.viewAlert(3, "Error al cargar propiedades", e.getMessage(), null);
        }
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
    private void handleSearch() {
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
            alert.viewAlert(2, "Error de Formato", "Por favor, ingresa números válidos en los campos de precio (Min y Max).", null);
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
                    prop.setType(type);

                    State state = new State();
                    state.setIdState(rs.getInt("id_state"));
                    prop.setState(state);

                    propertyList.add(prop);
                }
            }
        }

        tblProperties.setItems(propertyList);
        lblResults.setText("Resultados: " + propertyList.size() + " propiedades encontradas");
    }

    @FXML
    private void handleViewDetails() {
        Property selected = tblProperties.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert.viewAlert(2, "Selección requerida", "Por favor, selecciona una propiedad de la tabla antes de ver los detalles.", null);
            return;
        }
        System.out.println("Opening details for property ID: " + selected.getIdProperty());
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
}
