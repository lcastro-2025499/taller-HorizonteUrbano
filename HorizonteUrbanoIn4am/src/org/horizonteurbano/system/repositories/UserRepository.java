package org.horizonteurbano.system.repositories;

import org.horizonteurbano.system.models.Role;
import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.config.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    // Stores the user as-is; the password must already be hashed by UserService
    public boolean saveUser(User user) {
        String query = "INSERT INTO Users (id_user, name, last_name, password, email, user_name, active, id_role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {

            preparedStmt.setString(1, user.getIdUser());
            preparedStmt.setString(2, user.getName());
            preparedStmt.setString(3, user.getLastName());
            preparedStmt.setString(4, user.getPassword());
            preparedStmt.setString(5, user.getEmail());
            preparedStmt.setString(6, user.getUserName());
            preparedStmt.setBoolean(7, user.isActive());
            preparedStmt.setInt(8, user.getRol().getIdRole());

            return preparedStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    // Returns the user (with hashed password) matching email OR user_name
    public User getUserByIdentifier(String identifier) {
        String query = "SELECT id_user, name, last_name, password, email, user_name, active, id_role FROM Users WHERE (email = ? OR user_name = ?) AND active = true";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {

            preparedStmt.setString(1, identifier);
            preparedStmt.setString(2, identifier);

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
        String query = "SELECT id_user, name, last_name, password, email, user_name, active, id_role FROM Users WHERE email = ? AND active = true";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {

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

    public User getUserById(String idUser) {
        String query = "SELECT id_user, name, last_name, password, email, user_name, active, id_role FROM Users WHERE id_user = ?";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {

            preparedStmt.setString(1, idUser);

            try (ResultSet resultSet = preparedStmt.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user by id: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllActiveUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT id_user, name, last_name, password, email, user_name, active, id_role FROM Users WHERE active = true";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query); ResultSet resultSet = preparedStmt.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public boolean updateUser(User user) {
        String query = "UPDATE Users SET name = ?, last_name = ?, email = ?, user_name = ?, active = ?, id_role = ? WHERE id_user = ?";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {

            preparedStmt.setString(1, user.getName());
            preparedStmt.setString(2, user.getLastName());
            preparedStmt.setString(3, user.getEmail());
            preparedStmt.setString(4, user.getUserName());
            preparedStmt.setBoolean(5, user.isActive());
            preparedStmt.setInt(6, user.getRol().getIdRole());
            preparedStmt.setString(7, user.getIdUser());

            return preparedStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    // Password must already be hashed by UserService
    public boolean updatePassword(String idUser, String hashedPassword) {
        String query = "UPDATE Users SET password = ? WHERE id_user = ?";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {

            preparedStmt.setString(1, hashedPassword);
            preparedStmt.setString(2, idUser);

            return preparedStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(String idUser) {
        String query = "UPDATE Users SET active = false WHERE id_user = ?";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {

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
        user.setRol(role);

        return user;
    }
}
