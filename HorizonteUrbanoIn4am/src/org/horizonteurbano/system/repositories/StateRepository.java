package org.horizonteurbano.system.repositories;

import org.horizonteurbano.system.config.ConnectionDB;
import org.horizonteurbano.system.models.State;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StateRepository {

    public List<State> getAllStates() {
        List<State> states = new ArrayList<>();
        String procedure = "{call sp_read_states()}";

        try (Connection connection = ConnectionDB.getInstanceConnectionDB().getConnection();
             CallableStatement statement = connection.prepareCall(procedure);
             ResultSet resultSet = statement.executeQuery()) {
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
