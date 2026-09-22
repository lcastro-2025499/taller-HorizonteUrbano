package org.horizonteurbano.system.repositories;

import org.horizonteurbano.system.models.State;
import org.horizonteurbano.system.config.ConnectionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StateRepository {

    public List<State> getAllStates() {
        List<State> states = new ArrayList<>();
        String query = "SELECT * FROM State";
        
        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             PreparedStatement preparedStmt = connection.prepareStatement(query);
             ResultSet resultSet = preparedStmt.executeQuery()) {
            
            while (resultSet.next()) {
                State state = new State();
                state.setIdState(resultSet.getInt("id_state"));
                state.setNameState(resultSet.getString("name_state"));
                states.add(state);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar los estados: " + e.getMessage());
        }
        return states;
    }
}