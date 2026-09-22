package org.horizonteurbano.system.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    private static ConnectionDB instanceConnectionDB;
    private Connection connection;

    private ConnectionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + Environment.LOCATION_SERVICE + "/" + Environment.DATA_BASE,
                    Environment.USER,
                    Environment.PASSWORD);
        } catch (ClassNotFoundException classNotFound) {
            System.out.println("Error: Driver class not found");
        } catch (SQLException sqlException) {
            System.out.println("Error: SQL connection failed");
        } catch (Exception error) {
            System.out.println("Error: " + error.getMessage());
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed successfully");
            } catch (SQLException sqlException) {
                System.out.println("Error: Failed to close connection");
                sqlException.printStackTrace();
            }
        }
    }

    public static ConnectionDB getInstanceConnectionDB() {
        if (instanceConnectionDB == null) {
            instanceConnectionDB = new ConnectionDB();
        }
        return instanceConnectionDB;
    }

    public Connection getConnection() {
        return connection;
    }
}
