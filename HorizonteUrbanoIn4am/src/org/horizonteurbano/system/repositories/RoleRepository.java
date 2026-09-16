package org.horizonteurbano.system.repositories;

import org.horizonteurbano.system.models.Role;
import org.horizonteurbano.system.config.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleRepository {

    public List<Role> getAllRoles() {
        List<Role> roleList = new ArrayList<>();
        String query = "SELECT * FROM Role"; 
        
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             PreparedStatement preparedStmt = connection.prepareStatement(query);
             ResultSet resultSet = preparedStmt.executeQuery()) {
            
            while (resultSet.next()) {
                Role role = new Role();
                role.setIdRole(resultSet.getInt("id_role")); 
                role.setNameRole(resultSet.getString("name_role"));
                roleList.add(role);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar los roles desde la base de datos: " + e.getMessage());
        }
        return roleList;
    }
}