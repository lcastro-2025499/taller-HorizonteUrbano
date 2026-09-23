package org.horizonteurbano.system.repositories;

import org.horizonteurbano.system.models.Property;
import org.horizonteurbano.system.models.PropertyType;
import org.horizonteurbano.system.models.State;
import org.horizonteurbano.system.config.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PropertyRepository {

    public boolean saveProperty(Property property) {
        String query = "INSERT INTO Properties (internal_code, address, area_m2, price, id_property_type, id_state, active, id_user) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {

            preparedStmt.setString(1, property.getInternalCode());
            preparedStmt.setString(2, property.getAddress());
            preparedStmt.setDouble(3, property.getArea());
            preparedStmt.setDouble(4, property.getPrice());
            preparedStmt.setInt(5, property.getType().getIdType());
            preparedStmt.setInt(6, property.getState().getIdState());
            preparedStmt.setBoolean(7, property.isActive());
            preparedStmt.setString(8, property.getIdUser());

            return preparedStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error saving property: " + e.getMessage());
            return false;
        }
    }

    // Reads all columns of the table so the UI can display and edit every field
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

    public boolean updateProperty(Property property) {
        String query = "UPDATE Properties SET address = ?, area_m2 = ?, price = ?, id_property_type = ?, id_state = ? WHERE internal_code = ?";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {

            preparedStmt.setString(1, property.getAddress());
            preparedStmt.setDouble(2, property.getArea());
            preparedStmt.setDouble(3, property.getPrice());
            preparedStmt.setInt(4, property.getType().getIdType());
            preparedStmt.setInt(5, property.getState().getIdState());
            preparedStmt.setString(6, property.getInternalCode());

            return preparedStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating property: " + e.getMessage());
            return false;
        }
    }

    public boolean deactivateProperty(String internalCode) {
        String query = "UPDATE Properties SET active = false WHERE internal_code = ?";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {
            preparedStmt.setString(1, internalCode);
            return preparedStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deactivating property: " + e.getMessage());
            return false;
        }
    }
}
