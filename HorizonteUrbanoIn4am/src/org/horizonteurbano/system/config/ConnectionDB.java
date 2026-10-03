package org.horizonteurbano.system.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {

    private static ConnectionDB instanceConnectionDB;

    private ConnectionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException classNotFound) {
            System.err.println("Error: Driver class not found");
        }
    }

    public static ConnectionDB getInstanceConnectionDB() {
        if (instanceConnectionDB == null) {
            instanceConnectionDB = new ConnectionDB();
        }
        return instanceConnectionDB;
    }

    // Returns a fresh connection each time; caller is responsible for closing it
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mysql://" + Environment.LOCATION_SERVICE + "/" + Environment.DATA_BASE,
                Environment.USER,
                Environment.PASSWORD
        );
    }
}
