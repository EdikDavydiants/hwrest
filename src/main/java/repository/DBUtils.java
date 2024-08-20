package repository;

import com.mysql.cj.jdbc.Driver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtils {
    public static final String HOST_CNF = "db_host";
    public static final String USERNAME_CNF = "db_username";
    public static final String PASSWORD_CNF = "db_password";
    public static final String FILENAME_CONFIG = "config.properties";

    public static Connection connection = null;
    private static TournamentsDAO tournamentsDAO;
    private static GamesDAO gamesDAO;
    private static PlayersDAO playersDAO;


    public static boolean createConnection(String filename_config, String host_cnf,
                                              String username_cnf, String password_cnf) {
        String url = null;
        String username = null;
        String password = null;

        Properties properties = new Properties();

        try(InputStream fis = DBUtils.class.getClassLoader().getResourceAsStream(filename_config)) {
            properties.load(fis);
            url = properties.getProperty(host_cnf);
            username = properties.getProperty(username_cnf);
            password = properties.getProperty(password_cnf);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }

        try {
            DriverManager.registerDriver(new Driver());
            Class.forName("com.mysql.cj.jdbc.Driver");
            //Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public static boolean createTestConnection(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static void createDAOs() {
        tournamentsDAO = new TournamentsDAO(connection);
        gamesDAO = new GamesDAO(connection);
        playersDAO = new PlayersDAO(connection);
    }


    public static Connection getConnection() {
        return connection;
    }


    public static TournamentsDAO getTournamentsDAO() {
        return tournamentsDAO;
    }

    public static GamesDAO getGamesDAO() {
        return gamesDAO;
    }

    public static PlayersDAO getPlayersDAO() {
        return playersDAO;
    }


}
