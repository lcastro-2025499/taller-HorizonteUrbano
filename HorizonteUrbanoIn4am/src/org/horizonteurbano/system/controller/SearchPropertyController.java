package org.horizonteurbano.system.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.horizonteurbano.system.config.ConnectionDB;
import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.models.PropertyType;
import org.horizonteurbano.system.models.State;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

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

    private ObservableList<Property> propertyList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadComboBoxes();
        loadProperties();
    }

    private void loadProperties() {
        try {
            loadProperties(null, null, null, null, null, null, null);
        } catch (SQLException e) {
            showError("Error al cargar propiedades", e.getMessage());
        }
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
            } else if (type != null) {
                return new SimpleStringProperty("ID: " + type.getIdType());
            }
            return new SimpleStringProperty("N/A");
        });

        colStatus.setCellValueFactory(cellData -> {
            State state = cellData.getValue().getState();
            if (state != null && state.getNameState() != null && !state.getNameState().isEmpty()) {
                return new SimpleStringProperty(state.getNameState());
            } else if (state != null) {
                return new SimpleStringProperty("ID: " + state.getIdState());
            }
            return new SimpleStringProperty("N/A");
        });
    }

    private void loadComboBoxes() {
        Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection();
        if (conn == null) {
            showError("Error de Conexión", "No se pudo establecer conexión con la base de datos.");
            return;
        }

        try {
            cmbType.getItems().clear();
            PropertyType allTypes = new PropertyType();
            allTypes.setIdType(0);
            allTypes.setNameType("Todos");
            cmbType.getItems().add(allTypes);

            try (CallableStatement stmt = conn.prepareCall("{call sp_read_propertytypes()}"); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PropertyType type = new PropertyType();
                    type.setIdType(rs.getInt("id_property_type"));
                    type.setNameType(rs.getString("type_name"));
                    cmbType.getItems().add(type);
                }
            }
            cmbType.getSelectionModel().selectFirst();

            cmbStatus.getItems().clear();
            State allStates = new State();
            allStates.setIdState(0);
            allStates.setNameState("Todos");
            cmbStatus.getItems().add(allStates);

            try (CallableStatement stmt = conn.prepareCall("{call sp_read_states()}"); ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    State state = new State();
                    state.setIdState(rs.getInt("id_state"));
                    state.setNameState(rs.getString("state_name"));
                    cmbStatus.getItems().add(state);
                }
            }
            
            cmbStatus.getSelectionModel().selectFirst();

        } catch (SQLException e) {
            showError("Error de Base de Datos", "No se pudieron cargar los filtros: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        try {
            String searchText = txtSearch.getText().trim().isEmpty() ? null : txtSearch.getText().trim();

            Double minPrice = txtPriceMin.getText().trim().isEmpty() ? null : Double.parseDouble(txtPriceMin.getText().trim());
            Double maxPrice = txtPriceMax.getText().trim().isEmpty() ? null : Double.parseDouble(txtPriceMax.getText().trim());

            PropertyType selectedType = cmbType.getSelectionModel().getSelectedItem();
            // CORREGIDO: Usa getIdType() en lugar de getIdPropertyType()
            Integer typeId = (selectedType != null && selectedType.getIdType() != 0) ? selectedType.getIdType() : null;

            State selectedState = cmbStatus.getSelectionModel().getSelectedItem();
            Integer stateId = (selectedState != null && selectedState.getIdState() != 0) ? selectedState.getIdState() : null;

            loadProperties(searchText, minPrice, maxPrice, null, null, stateId, typeId);

        } catch (NumberFormatException e) {
            showError("Error de Formato", "Por favor, ingresa números válidos en los campos de precio (Min y Max).");
        } catch (SQLException e) {
            showError("Error de Búsqueda", e.getMessage());
        }
    }

    private void loadProperties(String searchText, Double minPrice, Double maxPrice,
            Double minArea, Double maxArea, Integer stateId, Integer propertyTypeId) throws SQLException {
        propertyList.clear();

        Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection();
        if (conn == null) {
            showError("Error de Conexión", "No hay conexión a la base de datos.");
            return;
        }

        try (CallableStatement stmt = conn.prepareCall("{call sp_search_properties(?, ?, ?, ?, ?, ?, ?)}")) {

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

                    prop.setImageUrl(rs.getString("cover_url"));
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

            tblProperties.setItems(propertyList);
            lblResults.setText("Resultados: " + propertyList.size() + " propiedades encontradas");

        } catch (SQLException e) {
            showError("Error al cargar propiedades", e.getMessage());
            throw e;
        }
    }

    @FXML
    private void handleViewDetails() {
        Property selected = tblProperties.getSelectionModel().getSelectedItem();
        if (selected != null) {
            System.out.println("Abriendo detalles de la propiedad ID: " + selected.getIdProperty());
            // Aquí puedes integrar tu SceneManager si lo deseas
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selección requerida");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecciona una propiedad de la tabla antes de ver los detalles.");
            alert.showAndWait();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
