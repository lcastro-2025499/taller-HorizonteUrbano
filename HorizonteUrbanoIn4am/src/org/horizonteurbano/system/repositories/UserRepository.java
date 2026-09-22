package org.horizonteurbano.system.repositories;

import org.horizonteurbano.system.models.User;
import org.horizonteurbano.system.models.Role;
import org.horizonteurbano.system.config.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

public class UserRepository {

    public boolean saveUser(User user) {
        String query = "INSERT INTO Users (id_user, name, last_name, user_name, email, password, active, id_role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {

            preparedStmt.setString(1, user.getIdUser());
            preparedStmt.setString(2, user.getName());
            preparedStmt.setString(3, user.getLastName());
            preparedStmt.setString(4, user.getUserName());
            preparedStmt.setString(5, user.getEmail());

            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
            preparedStmt.setString(6, hashedPassword);
            preparedStmt.setBoolean(7, user.isActive());
            preparedStmt.setInt(8, user.getRol().getIdRole());

            return preparedStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar el usuario: " + e.getMessage());
            return false;
        }
    }

    public User validateLogin(String email, String enteredPassword) {
        String query = "SELECT * FROM Users WHERE email = ? AND active = true";
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection(); PreparedStatement preparedStmt = connection.prepareStatement(query)) {

            preparedStmt.setString(1, email);
            ResultSet ResultSet = preparedStmt.executeQuery();

            if (ResultSet.next()) {
                String hashedPasswordDB = ResultSet.getString("password");

                //Verifica si la contraseña ingresada coincide con el hash guardado
                if (BCrypt.checkpw(enteredPassword, hashedPasswordDB)) {
                    User user = new User();
                    user.setIdUser(ResultSet.getString("id_user"));
                    user.setName(ResultSet.getString("name"));
                    user.setLastName(ResultSet.getString("last_name"));
                    user.setEmail(ResultSet.getString("email"));
                    user.setUserName(ResultSet.getString("user_name"));

                    Role rol = new Role();
                    rol.setIdRole(ResultSet.getInt("id_role"));
                    user.setRol(rol);

                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
        }
        return null;
    }

    // + getUserById(String idUser): User
    // + getAllActiveUsers(): List
    // + updateUser(User user): boolean
    // + deleteUser(String idUser): boolean
    // + getAllUsers(): List
    // + resetPassword()
}
