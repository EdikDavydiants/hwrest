package repository;

import model.entities.Game;
import model.entities.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PlayersDAO {
    public final static String table_name = "players";
    public final static String id_str = "id";
    public final static String first_name_str = "first_name";
    public final static String last_name_str = "last_name";
    public final static String elo_str = "elo";

    private final static String CREATE_TABLE =
            "CREATE TABLE if NOT EXISTS " + table_name + " (" + id_str + " INTEGER, " + first_name_str +
            " VARCHAR(30), " + last_name_str + " VARCHAR(30), " + elo_str + " INTEGER)";
    private final static String GET_ALL_PLAYERS = "SELECT * FROM " + table_name;
    private final static String GET_PLAYER_BY_ID_QUERY = "SELECT * FROM " + table_name + " WHERE " + id_str + " = ?";
    private final static String GET_PLAYER_BY_LAST_NAME_QUERY = "SELECT * FROM " + table_name + " WHERE " + last_name_str + " = ?";
    private final static String GET_PLAYER_BY_FULL_NAME_QUERY = "SELECT * FROM " + table_name + " WHERE "
            + first_name_str + " = ? AND " + last_name_str + " = ?";
    private final static String CREATE_PLAYER_QUERY = "INSERT INTO " + table_name + " VALUES (?, ?, ?, ?)";
    private final static String UPDATE_PLAYER_QUERY = "UPDATE " + table_name + " SET " + elo_str + " = ? WHERE " + id_str + " = ?";
    private final static String MAX_ID_QUERY = "SELECT * FROM "+ table_name +" WHERE " + id_str + " = ( SELECT MAX(" + id_str + ") FROM "+ table_name +" )";

    private final Connection connection;


    public PlayersDAO(Connection connection) {
        this.connection = connection;
    }


    public void createTableIfNotExists() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TABLE)) {
            preparedStatement.execute();
        }
        if (getPlayerById(0) == null) {
            DBUtils.getPlayersDAO().createPlayers(FileUtils.createPlayerList());
        }
    }

    public List<Player> getAllPlayers() throws SQLException {
        List<Player> playerList;
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_PLAYERS)) {
            ResultSet rs = preparedStatement.executeQuery();
            playerList = extractPlayers(rs, false);
        }
        return playerList;
    }

    public Player getPlayerById(int id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_PLAYER_BY_ID_QUERY)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            List<Player> players = extractPlayers(rs, true);
            if (players.size() == 1) {
                return players.get(0);
            }
        }
        return null;
    }

    public List<Player> getPlayersByLastName(String lastName) throws SQLException {
        List<Player> players;
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_PLAYER_BY_LAST_NAME_QUERY)) {
            preparedStatement.setString(1, lastName);
            ResultSet rs = preparedStatement.executeQuery();
            players = extractPlayers(rs, false);
        }
        return players;
    }

    public List<Player> getPlayersByFullName(String firstName, String lastName) throws SQLException {
        List<Player> players;
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_PLAYER_BY_FULL_NAME_QUERY)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            ResultSet rs = preparedStatement.executeQuery();
            players = extractPlayers(rs, false);
        }
        return players;
    }

    private List<Player> extractPlayers(ResultSet rs, boolean withGames) throws SQLException{
        List<Player> players = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt(id_str);
            String firstName = rs.getString(first_name_str);
            String lastName = rs.getString(last_name_str);
            int elo = rs.getInt(elo_str);
            if (withGames) {
                List<Game> gameList = DBUtils.getGamesDAO().getAllPlayerGames(id);
                players.add(new Player(id, firstName, lastName, elo, gameList));
            } else {
                players.add(new Player(id, firstName, lastName, elo, null));
            }
        }
        return players;
    }

    private void savePlayer(Player player, int id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CREATE_PLAYER_QUERY)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, player.getFirstName());
            preparedStatement.setString(3, player.getLastName());
            preparedStatement.setInt(4, player.getElo());
            preparedStatement.executeUpdate();
        }
    }

    public void createPlayer(Player player) throws SQLException {
        int id = getMaxId() + 1;
        savePlayer(player, id);
    }

    public void createPlayers(List<Player> playerList) throws SQLException  {
        int id = getMaxId();
        for (Player player: playerList) {
            savePlayer(player, ++id);
        }
    }

    public void updatePlayer(int id, int newElo) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PLAYER_QUERY)) {
            preparedStatement.setInt(1, newElo);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
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
