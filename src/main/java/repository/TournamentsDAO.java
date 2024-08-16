package repository;

import entities.Tournament;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TournamentsDAO {
    public final static String table_name = "tournaments";
    public final static String id_str = "id";
    public final static String name_str = "name";

    private final static String CREATE_TABLE =
            "CREATE TABLE if NOT EXISTS " + table_name + " (" + id_str + " INTEGER, " + name_str + " VARCHAR(30))";
    private final static String GET_ALL_TOURNAMENTS_QUERY = "SELECT * FROM " + table_name;
    private final static String CREATE_TOURNAMENT_QUERY = "INSERT INTO " + table_name + " VALUES (?, ?)";
    private final static String MAX_ID_QUERY = "SELECT * FROM "+ table_name +" WHERE " + id_str + " = ( SELECT MAX(" + id_str + ") FROM "+ table_name +" )";

    private final Connection connection;


    public TournamentsDAO(Connection connection) {
        this.connection = connection;
    }


    public void createTableIfNotExists() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TABLE)) {
            preparedStatement.execute();
        }
    }

    public void saveTournaments(List<Tournament> tournamentList) throws SQLException {
        for (Tournament tournament: tournamentList) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TOURNAMENT_QUERY)) {
                preparedStatement.setInt(1, tournament.getId());
                preparedStatement.setString(2, tournament.getName());
                preparedStatement.executeUpdate();
            }
        }
    }

    public List<Tournament> getAllTournaments() throws SQLException {
        List<Tournament> tournamentList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_TOURNAMENTS_QUERY)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(id_str);
                String name = rs.getString(name_str);

                tournamentList.add(new Tournament(id, name));
            }
        }
        return tournamentList;
    }

    public int getMaxId() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(MAX_ID_QUERY)) {
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return rs.getInt(id_str);
            }
        }
        return -1;
    }


}
