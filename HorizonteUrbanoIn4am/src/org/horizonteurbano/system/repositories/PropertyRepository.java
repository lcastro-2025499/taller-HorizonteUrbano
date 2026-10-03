package org.horizonteurbano.system.repositories;

import org.horizonteurbano.system.config.ConnectionDB;
import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.models.PropertyType;
import org.horizonteurbano.system.models.State;

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

public class PropertyRepository {

    private int executeCountSP(String spCall) {
        try (Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement stmt = conn.prepareCall(spCall);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error ejecutando SP: " + e.getMessage());
        }
        return 0;
    }

    private double executeSumSP(String spCall) {
        try (Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement stmt = conn.prepareCall(spCall);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("Error ejecutando SP: " + e.getMessage());
        }
        return 0.0;
    }

    private Map<String, Integer> executeMapSP(String spCall) {
        Map<String, Integer> stats = new HashMap<>();
        try (Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement stmt = conn.prepareCall(spCall);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            System.err.println("Error ejecutando SP: " + e.getMessage());
        }
        return stats;
    }

    public int countActiveProperties() {
        return executeCountSP("{call sp_count_active_properties()}");
    }

    public double getTotalInventoryValue() {
        return executeSumSP("{call sp_get_total_inventory_value()}");
    }

    public int countSoldProperties() {
        return executeCountSP("{call sp_count_sold_properties()}");
    }

    public int countRentedProperties() {
        return executeCountSP("{call sp_count_rented_properties()}");
    }

    public double getSoldValue() {
        return executeSumSP("{call sp_get_sold_value()}");
    }

    public double getRentedValue() {
        return executeSumSP("{call sp_get_rented_value()}");
    }

    public Map<String, Integer> countByState() {
        return executeMapSP("{call sp_count_by_state()}");
    }

    public Map<String, Integer> countByType() {
        return executeMapSP("{call sp_count_by_type()}");
    }

    public boolean saveProperty(Property property) {
        String procedure = "{call sp_create_property(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement statement = connection.prepareCall(procedure)) {
            statement.setString(1, property.getInternalCode());
            statement.setString(2, property.getAddress());
            statement.setDouble(3, property.getArea());
            statement.setDouble(4, property.getPrice());
            statement.setBoolean(5, property.isActive());
            statement.setString(6, property.getCoverUrl());
            statement.setInt(7, property.getState().getIdState());
            statement.setString(8, property.getIdUser());
            statement.setInt(9, property.getType().getIdType());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving property: " + e.getMessage());
            return false;
        }
    }

    public List<Property> getAllActiveProperties() {
        List<Property> properties = new ArrayList<>();
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement stmt = connection.prepareCall("{call sp_read_properties()}");
             ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                properties.add(mapResultSetToProperty(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error loading properties: " + e.getMessage());
        }
        return properties;
    }

    public List<Property> searchProperties(String searchText, Double minPrice, Double maxPrice,
            Double minArea, Double maxArea, Integer stateId, Integer propertyTypeId, boolean includeInactive) {
        List<Property> properties = new ArrayList<>();
        String procedure = "{call sp_search_properties(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement statement = connection.prepareCall(procedure)) {
            statement.setString(1, searchText);
            setNullableDouble(statement, 2, minPrice);
            setNullableDouble(statement, 3, maxPrice);
            setNullableDouble(statement, 4, minArea);
            setNullableDouble(statement, 5, maxArea);
            setNullableInteger(statement, 6, stateId);
            setNullableInteger(statement, 7, propertyTypeId);
            statement.setBoolean(8, includeInactive);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) properties.add(mapResultSetToProperty(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching properties: " + e.getMessage());
        }
        return properties;
    }

    private void setNullableDouble(CallableStatement statement, int index, Double value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.DECIMAL);
        } else {
            statement.setDouble(index, value);
        }
    }

    private void setNullableInteger(CallableStatement statement, int index, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }

    private Property mapResultSetToProperty(ResultSet rs) throws SQLException {
        Property prop = new Property();
        prop.setIdProperty(rs.getInt("id_property"));
        prop.setInternalCode(rs.getString("internal_code"));
        prop.setAddress(rs.getString("address"));
        prop.setArea(rs.getDouble("area_m2"));
        prop.setPrice(rs.getDouble("price"));
        prop.setActive(rs.getBoolean("active"));
        prop.setCoverUrl(rs.getString("cover_url"));
        prop.setIdUser(rs.getString("id_user"));

        Timestamp dateRegTs = rs.getTimestamp("date_register");
        if (dateRegTs != null) prop.setDateRegister(dateRegTs.toLocalDateTime());

        Timestamp updateDateTs = rs.getTimestamp("update_date");
        if (updateDateTs != null) prop.setUpdateDate(updateDateTs.toLocalDateTime());

        PropertyType type = new PropertyType();
        type.setIdType(rs.getInt("id_property_type"));
        if (rs.getString("name_type") != null) type.setNameType(rs.getString("name_type"));
        prop.setType(type);

        State state = new State();
        state.setIdState(rs.getInt("id_state"));
        if (rs.getString("name_state") != null) state.setNameState(rs.getString("name_state"));
        prop.setState(state);

        prop.setInactiveReason(rs.getString("inactive_reason"));
        Timestamp inactiveTs = rs.getTimestamp("inactive_date");
        if (inactiveTs != null) prop.setInactiveDate(inactiveTs.toLocalDateTime());

        return prop;
    }

    public boolean updateProperty(Property property) {
        String procedure = "{call sp_update_property_by_code(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement statement = connection.prepareCall(procedure)) {
            statement.setString(1, property.getInternalCode());
            statement.setString(2, property.getAddress());
            statement.setDouble(3, property.getArea());
            statement.setDouble(4, property.getPrice());
            statement.setInt(5, property.getType().getIdType());
            statement.setInt(6, property.getState().getIdState());
            statement.setString(7, property.getCoverUrl());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating property: " + e.getMessage());
            return false;
        }
    }

    public boolean deactivateProperty(String internalCode, String reason) {
        String procedure = "{call sp_deactivate_property_by_code(?, ?)}";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement statement = connection.prepareCall(procedure)) {
            statement.setString(1, internalCode);
            statement.setString(2, reason);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating property: " + e.getMessage());
            return false;
        }
    }

    public boolean changeStatus(String internalCode, int idState) {
        String procedure = "{call sp_change_property_state_by_code(?, ?)}";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement statement = connection.prepareCall(procedure)) {
            statement.setString(1, internalCode);
            statement.setInt(2, idState);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            return false;
        }
    }
}