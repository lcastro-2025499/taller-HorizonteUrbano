package org.horizonteurbano.system.repositories;

import org.horizonteurbano.system.config.ConnectionDB;
import org.horizonteurbano.system.models.PropertyType;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PropertyTypeRepository {

    public List<PropertyType> getAllPropertyTypes() {
        List<PropertyType> types = new ArrayList<>();
        String procedure = "{call sp_read_propertytypes()}";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement statement = connection.prepareCall(procedure);
             ResultSet resultSet = statement.executeQuery()) {
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
