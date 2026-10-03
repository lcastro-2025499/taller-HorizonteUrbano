package org.horizonteurbano.system.repositories;

import org.horizonteurbano.system.config.ConnectionDB;
import org.horizonteurbano.system.models.Role;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleRepository {

    public List<Role> getAllRoles() {
        List<Role> roleList = new ArrayList<>();
        String procedure = "{call sp_read_all_roles()}";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement statement = connection.prepareCall(procedure);
             ResultSet resultSet = statement.executeQuery()) {
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