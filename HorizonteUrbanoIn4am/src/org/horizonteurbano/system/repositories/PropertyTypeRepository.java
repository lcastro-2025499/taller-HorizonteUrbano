package org.horizonteurbano.system.repositories;

import org.horizonteurbano.system.models.PropertyType;
import org.horizonteurbano.system.config.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PropertyTypeRepository {

    public List<PropertyType> getAllPropertyTypes() {
        List<PropertyType> types = new ArrayList<>();
        String query = "SELECT * FROM PropertyType";
        
        try (Connection conn = ConnectionDB.getInstanceConnectionDB().getConnection();
             PreparedStatement preparedStmt = conn.prepareStatement(query);
             ResultSet resultSet = preparedStmt.executeQuery()) {
            
            while (resultSet.next()) {
                PropertyType type = new PropertyType();
                type.setIdType(resultSet.getInt("id_type")); 
                type.setNameType(resultSet.getString("name_type"));
                types.add(type);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar los tipos de propiedad: " + e.getMessage());
        }
        return types;
    }
}