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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PropertyRepository {

    public boolean saveProperty(Property property) {
        String query = "INSERT INTO Properties (internal_code, address, area_m2, price, id_property_type, id_state, active, id_user, cover_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {

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
        String query = "SELECT id_property, internal_code, address, area_m2, price, active, "
                + "date_register, update_date, cover_url, id_state, id_user, id_property_type "
                + "FROM Properties WHERE active = true";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query); ResultSet resultSet = preparedStmt.executeQuery()) {

            while (resultSet.next()) {
                Property property = new Property();
                property.setIdProperty(resultSet.getInt("id_property"));
                property.setInternalCode(resultSet.getString("internal_code"));
                property.setAddress(resultSet.getString("address"));
                property.setArea(resultSet.getDouble("area_m2"));
                property.setPrice(resultSet.getDouble("price"));
                property.setActive(resultSet.getBoolean("active"));
                property.setCoverUrl(resultSet.getString("cover_url"));
                property.setIdUser(resultSet.getString("id_user"));

                State state = new State();
                state.setIdState(resultSet.getInt("id_state"));
                property.setState(state);

                PropertyType type = new PropertyType();
                type.setIdType(resultSet.getInt("id_property_type"));
                property.setType(type);

                properties.add(property);
            }
        } catch (SQLException e) {
            System.err.println("Error loading properties: " + e.getMessage());
        }
        return properties;
    }

    /**
     * Búsqueda flexible con filtros opcionales. Si {@code includeInactive} es
     * true, también devuelve propiedades dadas de baja (necesario para el
     * historial de bajas). Los campos de texto vacíos o nulos se ignoran. Trae
     * los nombres de tipo y estado resueltos vía JOIN.
     */
    public List<Property> searchProperties(String searchText, Double minPrice, Double maxPrice,
            Double minArea, Double maxArea, Integer stateId, Integer propertyTypeId,
            boolean includeInactive) {

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

        if (!includeInactive) {
            query.append(" AND p.active = true");
        }
        if (searchText != null && !searchText.isBlank()) {
            query.append(" AND (p.address LIKE ? OR p.internal_code LIKE ?)");
        }
        if (minPrice != null) {
            query.append(" AND p.price >= ?");
        }
        if (maxPrice != null) {
            query.append(" AND p.price <= ?");
        }
        if (minArea != null) {
            query.append(" AND p.area_m2 >= ?");
        }
        if (maxArea != null) {
            query.append(" AND p.area_m2 <= ?");
        }
        if (stateId != null) {
            query.append(" AND p.id_state = ?");
        }
        if (propertyTypeId != null) {
            query.append(" AND p.id_property_type = ?");
        }

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement stmt = connection.prepareStatement(query.toString())) {

            int idx = 1;
            if (searchText != null && !searchText.isBlank()) {
                String likePattern = "%" + searchText + "%";
                stmt.setString(idx++, likePattern);
                stmt.setString(idx++, likePattern);
            }
            if (minPrice != null) {
                stmt.setDouble(idx++, minPrice);
            }
            if (maxPrice != null) {
                stmt.setDouble(idx++, maxPrice);
            }
            if (minArea != null) {
                stmt.setDouble(idx++, minArea);
            }
            if (maxArea != null) {
                stmt.setDouble(idx++, maxArea);
            }
            if (stateId != null) {
                stmt.setInt(idx++, stateId);
            }
            if (propertyTypeId != null) {
                stmt.setInt(idx++, propertyTypeId);
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
                    type.setNameType(rs.getString("name_type"));
                    prop.setType(type);

                    State state = new State();
                    state.setIdState(rs.getInt("id_state"));
                    state.setNameState(rs.getString("name_state"));
                    prop.setState(state);

                    prop.setInactiveReason(rs.getString("inactive_reason"));
                    Timestamp inactiveTs = rs.getTimestamp("inactive_date");
                    if (inactiveTs != null) {
                        prop.setInactiveDate(inactiveTs.toLocalDateTime());
                    }

                    properties.add(prop);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching properties: " + e.getMessage());
        }
        return properties;
    }

    public boolean updateProperty(Property property) {
        String query = "UPDATE Properties SET address = ?, area_m2 = ?, price = ?, id_property_type = ?, id_state = ?, cover_url = ? WHERE internal_code = ?";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {

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
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {
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

        try (Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, idState);
            pstmt.setString(2, internalCode);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            return false;
        }
    }

    public int countActiveProperties() {
        String query = "SELECT COUNT(*) FROM Properties WHERE active = 1";
        try (Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error contando propiedades: " + e.getMessage());
        }
        return 0;
    }

    public double getTotalInventoryValue() {
        String query = "SELECT SUM(price) FROM Properties WHERE active = 1";
        try (Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error sumando valor del inventario: " + e.getMessage());
        }
        return 0.0;
    }

    public Map<String, Integer> countByState() {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT s.name_state, COUNT(p.id_property) "
                + "FROM Properties p "
                + "JOIN State s ON p.id_state = s.id_state "
                + "WHERE p.active = 1 "
                + "GROUP BY s.name_state";
        try (Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            System.err.println("Error agrupando por estado: " + e.getMessage());
        }
        return stats;
    }

    public Map<String, Integer> countByType() {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT t.name_type, COUNT(p.id_property) "
                + "FROM Properties p "
                + "JOIN PropertyType t ON p.id_property_type = t.id_type "
                + "WHERE p.active = 1 "
                + "GROUP BY t.name_type";
        try (Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            System.err.println("Error agrupando por tipo: " + e.getMessage());
        }
        return stats;
    }
}
