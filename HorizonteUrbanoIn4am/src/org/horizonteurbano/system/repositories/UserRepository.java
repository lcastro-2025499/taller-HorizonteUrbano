package org.horizonteurbano.system.repositories;

import org.horizonteurbano.system.config.ConnectionDB;
import org.horizonteurbano.system.models.Role;
import org.horizonteurbano.system.models.User;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    public boolean saveUser(User user) {
        String query = "{call sp_create_user(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement preparedStmt = connection.prepareCall(query)) {
            
            preparedStmt.setString(1, user.getIdUser());
            preparedStmt.setString(2, user.getName());
            preparedStmt.setString(3, user.getLastName());
            preparedStmt.setString(4, user.getPassword());
            preparedStmt.setString(5, user.getEmail());
            preparedStmt.setString(6, user.getUserName());
            preparedStmt.setBoolean(7, user.isActive());
            preparedStmt.setInt(8, user.getRole().getIdRole());
            
            return preparedStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    public User getUserByIdentifier(String identifier) {
        String query = "{call sp_login_user(?)}";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement preparedStmt = connection.prepareCall(query)) {
            
            preparedStmt.setString(1, identifier);
            
            try (ResultSet resultSet = preparedStmt.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by identifier: " + e.getMessage());
        }
        return null;
    }

    public User getUserByEmail(String email) {
        String query = "{call sp_read_user_by_email(?)}";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement preparedStmt = connection.prepareCall(query)) {
            
            preparedStmt.setString(1, email);
            
            try (ResultSet resultSet = preparedStmt.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by email: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllActiveUsers() {
        List<User> users = new ArrayList<>();
        String query = "{call sp_read_users()}";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement preparedStmt = connection.prepareCall(query);
             ResultSet resultSet = preparedStmt.executeQuery()) {
            
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error loading active users: " + e.getMessage());
        }
        return users;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "{call sp_read_all_users()}";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement preparedStmt = connection.prepareCall(query);
             ResultSet resultSet = preparedStmt.executeQuery()) {
            
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all users: " + e.getMessage());
        }
        return users;
    }

    public boolean updateUser(User user) {
        String query = "{call sp_update_user(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement preparedStmt = connection.prepareCall(query)) {
            
            preparedStmt.setString(1, user.getIdUser());
            preparedStmt.setString(2, user.getName());
            preparedStmt.setString(3, user.getLastName());
            preparedStmt.setString(4, user.getEmail());
            preparedStmt.setString(5, user.getUserName());
            preparedStmt.setString(6, user.getPhone() != null ? user.getPhone() : "");
            preparedStmt.setBoolean(7, user.isActive());
            preparedStmt.setInt(8, user.getRole().getIdRole());
            
            return preparedStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(String idUser, String hashedPassword) {
        String query = "{call sp_update_user_password(?, ?)}";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement preparedStmt = connection.prepareCall(query)) {
            
            preparedStmt.setString(1, idUser);
            preparedStmt.setString(2, hashedPassword);
            
            return preparedStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(String idUser) {
        String query = "{call sp_delete_user(?)}";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement preparedStmt = connection.prepareCall(query)) {
            
            preparedStmt.setString(1, idUser);
            return preparedStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            return false;
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setIdUser(resultSet.getString("id_user"));
        user.setName(resultSet.getString("name"));
        user.setLastName(resultSet.getString("last_name"));
        user.setPassword(resultSet.getString("password"));
        user.setEmail(resultSet.getString("email"));
        user.setUserName(resultSet.getString("user_name"));
        user.setActive(resultSet.getBoolean("active"));

        Role role = new Role();
        role.setIdRole(resultSet.getInt("id_role"));
        user.setRole(role);

        return user;
    }
}