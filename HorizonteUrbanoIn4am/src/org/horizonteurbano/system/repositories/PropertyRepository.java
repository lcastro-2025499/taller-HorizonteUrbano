package org.horizonteurbano.system.repositories;

import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.models.PropertyType;
import org.horizonteurbano.system.models.State;
import org.horizonteurbano.system.config.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.CallableStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PropertyRepository {

    // --- MÉTODO HELPER PARA EVITAR REPETIR CÓDIGO DE STORED PROCEDURES ---
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

    // --- MÉTODOS DE MÉTRICAS (US4.1) ---
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

    // --- MÉTODOS DE GESTIÓN (Mantenidos, pero asegurando buenas prácticas) ---
    public boolean saveProperty(Property property) {
        String query = "INSERT INTO Properties (internal_code, address, area_m2, price, id_property_type, id_state, active, id_user, cover_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); 
             PreparedStatement preparedStmt = connection.prepareStatement(query)) {
            preparedStmt.setString(1, property.getInternalCode());
            preparedStmt.setString(2, property.getAddress());
            preparedStmt.setDouble(3, property.getArea());
            preparedStmt.setDouble(4, property.getPrice());
            preparedStmt.setInt(5, property.getType().getIdType());
            preparedStmt.setInt(6, property.getState().getIdState());
            preparedStmt.setBoolean(7, property.isActive());
            preparedStmt.setString(8, property.getIdUser());
            preparedStmt.setString(9, property.getCoverUrl());
            return preparedStmt.executeUpdate() > 0;
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

    // Nota: searchProperties se mantiene con SQL dinámico porque los SP de búsqueda con filtros opcionales 
    // son complejos de manejar solo con CALL, pero si deseas migrarlo a un SP, avísame.
    public List<Property> searchProperties(String searchText, Double minPrice, Double maxPrice,
            Double minArea, Double maxArea, Integer stateId, Integer propertyTypeId, boolean includeInactive) {
        // ... (Se mantiene tu implementación actual de searchProperties por ser lógica de negocio compleja de filtros)
        // Para no alargar el código, asumo que este método específico puede quedarse así o migrarlo luego.
        // Si quieres que lo migre a SP, dímelo.
        List<Property> properties = new ArrayList<>();
        StringBuilder query = new StringBuilder(
                "SELECT p.id_property, p.internal_code, p.address, p.area_m2, p.price, p.active, "
                + "p.date_register, p.update_date, p.cover_url, p.id_state, p.id_user, p.id_property_type, "
                + "p.inactive_reason, p.inactive_date, "
                + "t.name_type, s.name_state "
                + "FROM Properties p "
                + "LEFT JOIN PropertyType t ON p.id_property_type = t.id_type "
                + "LEFT JOIN State s ON p.id_state = s.id_state "
                + "WHERE 1 = 1");

        if (!includeInactive) query.append(" AND p.active = true");
        if (searchText != null && !searchText.isBlank()) query.append(" AND (p.address LIKE ? OR p.internal_code LIKE ?)");
        if (minPrice != null) query.append(" AND p.price >= ?");
        if (maxPrice != null) query.append(" AND p.price <= ?");
        if (minArea != null) query.append(" AND p.area_m2 >= ?");
        if (maxArea != null) query.append(" AND p.area_m2 <= ?");
        if (stateId != null) query.append(" AND p.id_state = ?");
        if (propertyTypeId != null) query.append(" AND p.id_property_type = ?");

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); 
             PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            int idx = 1;
            if (searchText != null && !searchText.isBlank()) {
                String likePattern = "%" + searchText + "%";
                stmt.setString(idx++, likePattern);
                stmt.setString(idx++, likePattern);
            }
            if (minPrice != null) stmt.setDouble(idx++, minPrice);
            if (maxPrice != null) stmt.setDouble(idx++, maxPrice);
            if (minArea != null) stmt.setDouble(idx++, minArea);
            if (maxArea != null) stmt.setDouble(idx++, maxArea);
            if (stateId != null) stmt.setInt(idx++, stateId);
            if (propertyTypeId != null) stmt.setInt(idx++, propertyTypeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) properties.add(mapResultSetToProperty(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching properties: " + e.getMessage());
        }
        return properties;
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
        String query = "UPDATE Properties SET address = ?, area_m2 = ?, price = ?, id_property_type = ?, id_state = ?, cover_url = ? WHERE internal_code = ?";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); 
             PreparedStatement preparedStmt = connection.prepareStatement(query)) {
            preparedStmt.setString(1, property.getAddress());
            preparedStmt.setDouble(2, property.getArea());
            preparedStmt.setDouble(3, property.getPrice());
            preparedStmt.setInt(4, property.getType().getIdType());
            preparedStmt.setInt(5, property.getState().getIdState());
            preparedStmt.setString(6, property.getCoverUrl());
            preparedStmt.setString(7, property.getInternalCode());
            return preparedStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating property: " + e.getMessage());
            return false;
        }
    }

    public boolean deactivateProperty(String internalCode, String reason) {
        String query = "UPDATE Properties SET active = false, inactive_reason = ?, inactive_date = NOW() WHERE internal_code = ?";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); 
             PreparedStatement preparedStmt = connection.prepareStatement(query)) {
            preparedStmt.setString(1, reason);
            preparedStmt.setString(2, internalCode);
            return preparedStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating property: " + e.getMessage());
            return false;
        }
    }

    public boolean changeStatus(String internalCode, int idState) {
        String query = "UPDATE Properties SET id_state = ? WHERE internal_code = ?";
        try (Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection(); 
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, idState);
            pstmt.setString(2, internalCode);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            return false;
        }
    }
}