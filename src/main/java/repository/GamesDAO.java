package repository;

import entities.Game;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class GamesDAO {
    public final static String table_name = "games";
    public final static String id_str = "id";
    public final static String white_id_str = "white_id";
    public final static String black_id_str = "black_id";
    public final static String result_str = "result";
    public final static String tournament_id_str = "tournament_id";
    public final static String tour_str = "tour";

    private final static String CREATE_TABLE =
            "CREATE TABLE if NOT EXISTS " + table_name +
            " (" + id_str + " INTEGER, " + white_id_str + " INTEGER, " + black_id_str + " INTEGER, " +
                    result_str + " INTEGER, " + tournament_id_str + " INTEGER, " + tour_str + " INTEGER)";
    private final static String GET_ALL_PLAYER_GAMES_QUERY = "SELECT * FROM " + table_name +
            " WHERE " + white_id_str + " = ? OR " + black_id_str + " = ?";
    private final static String GET_ALL_TOURNAMENT_GAMES_QUERY = "SELECT * FROM " + table_name +
            " WHERE " + tournament_id_str + " = ?";
    private final static String GET_ALL_TOUR_GAMES_OF_TOURNAMENT_QUERY = "SELECT * FROM " + table_name +
            " WHERE " + tournament_id_str + " = ? AND " + tour_str + " = ?";
    private final static String CREATE_GAME_QUERY = "INSERT INTO " + table_name + " VALUES (?, ?, ?, ?, ?, ?)";
    private final static String MAX_ID_QUERY = "SELECT * FROM "+ table_name +" WHERE " + id_str + " = ( SELECT MAX(" + id_str + ") FROM "+ table_name +" )";

    private final Connection connection;


    public GamesDAO(Connection connection) {
        this.connection = connection;
    }


    public void createTableIfNotExists() throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TABLE)) {
            preparedStatement.execute();
        }
    }


    public void saveGames(List<Game> gameList) throws SQLException {
        for (Game game: gameList) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(CREATE_GAME_QUERY)) {
                preparedStatement.setInt(1, game.getId());
                preparedStatement.setInt(2, game.getWhiteId());
                preparedStatement.setInt(3, game.getBlackId());
                preparedStatement.setInt(4, game.getResult());
                preparedStatement.setInt(5, game.getTournamentId());
                preparedStatement.setInt(6, game.getTour());
                preparedStatement.executeUpdate();
            }
        }
    }


    private List<Game> extractGames(ResultSet rs) throws SQLException {
        List<Game> games = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt(id_str);
            int white_id = rs.getInt(white_id_str);
            int black_id = rs.getInt(black_id_str);
            int result =  rs.getInt(result_str);
            int tournament_id = rs.getInt(tournament_id_str);
            int tour = rs.getInt(tour_str);
            games.add(new Game(id, white_id, black_id, result, tournament_id, tour));
        }
        return games;
    }


    public List<Game> getAllPlayerGames(int playerId) throws SQLException {
        List<Game> gameList;
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_PLAYER_GAMES_QUERY)) {
            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, playerId);
            ResultSet rs = preparedStatement.executeQuery();
            gameList = extractGames(rs);
        }
        return gameList;
    }


    public List<Game> getAllTournamentGames(int tournamentId) throws SQLException {
        List<Game> gameList;
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_TOURNAMENT_GAMES_QUERY)) {
            preparedStatement.setInt(1, tournamentId);
            ResultSet rs = preparedStatement.executeQuery();
            gameList = extractGames(rs);
        }
        return gameList;
    }


    public List<Game> getAllTourGamesOfTournament(int tournamentId, int tourNumber) throws SQLException {
        List<Game> gameList;
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_TOUR_GAMES_OF_TOURNAMENT_QUERY)) {
            preparedStatement.setInt(1, tournamentId);
            preparedStatement.setInt(2, tourNumber);
            ResultSet rs = preparedStatement.executeQuery();
            gameList = extractGames(rs);
        }
        return gameList;
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
