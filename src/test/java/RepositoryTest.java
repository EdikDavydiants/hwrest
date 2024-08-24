import model.entities.Game;
import model.entities.Player;
import model.entities.Tournament;
import service.TournamentService;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import repository.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


class RepositoryTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeAll
    static void beforeAll() {
        postgres.start();

        DBUtils.createTestConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        DBUtils.createDAOs();

        try {
            createTables();
            createTournaments();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }



    @Test
    void getAllTournamentsTest() {
        try {
            List<Tournament> tournaments = DBUtils.getTournamentsDAO().getAllTournaments();
            int lastElemIdx = tournaments.size() - 1;
            int lastId = tournaments.get(lastElemIdx).getId();
            assertEquals(lastId, lastElemIdx);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    void getAllPlayerGamesTest() {
        try {
            int playersNumber = DBUtils.getPlayersDAO().getAllPlayers().size();
            int playerId = new Random().nextInt(playersNumber);
            List<Game> games = DBUtils.getGamesDAO().getAllPlayerGames(playerId);
            assertNotNull(games);
            for (Game game: games) {
                assertTrue(game.getWhiteId() == playerId || game.getBlackId() == playerId);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    void getAllTournamentGamesTest() {
        try {
            int tournamentNumber = DBUtils.getTournamentsDAO().getAllTournaments().size();
            int tournamentId = new Random().nextInt(tournamentNumber);
            List<Game> games = DBUtils.getGamesDAO().getAllTournamentGames(tournamentId);
            for (Game game: games) {
                assertEquals(game.getTournamentId(), tournamentId);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    void getAllTourGamesOfTournamentTest() {
        try {
            int tournamentNumber = DBUtils.getTournamentsDAO().getAllTournaments().size();
            int tournamentId = new Random().nextInt(tournamentNumber);
            List<List<Game>> gameLists = new ArrayList<>();
            for (int i = 1; ; i++) {
                List<Game> games = DBUtils.getGamesDAO().getAllTourGamesOfTournament(tournamentId, i);
                if (!games.isEmpty()) {
                    gameLists.add(games);
                } else {
                    break;
                }
            }
            int gamesInATour = gameLists.get(0).size();

            for (List<Game> games: gameLists) {
                assertEquals(games.size(), gamesInATour);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    void getPlayerByIdTest() {
        try {
            Random random = new Random();
            int playersNumber = DBUtils.getPlayersDAO().getAllPlayers().size();
            for (int i = 0; i < 100; i++) {
                int playerId = random.nextInt(playersNumber);
                int playerId_ = DBUtils.getPlayersDAO().getPlayerById(playerId).getId();
                assertEquals(playerId, playerId_);
            }
            Player player = DBUtils.getPlayersDAO().getPlayerById(playersNumber);
            assertNull(player);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    void getPlayerByLastNameTest() {
        try {
            String lastName = "Bdrhykntnuydbdhm";
            int elo = 2000;
            DBUtils.getPlayersDAO().createPlayer(new Player("FirstName1", lastName, elo));
            DBUtils.getPlayersDAO().createPlayer(new Player("FirstName2", lastName, elo));
            DBUtils.getPlayersDAO().createPlayer(new Player("FirstName3", lastName, elo));
            List<Player> players = DBUtils.getPlayersDAO().getPlayersByLastName(lastName);
            assertEquals(3, players.size());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    void createAndGetByFullNamePlayerTest() {
        try {
            String firstName = "Fkmtumyudtnty";
            String lastName = "Bdrhykntnuydbdhm";
            int elo = 2000;
            DBUtils.getPlayersDAO().createPlayer(new Player(firstName, lastName, elo));
            List<Player> players = DBUtils.getPlayersDAO().getPlayersByFullName(firstName, lastName);
            assertEquals(1, players.size());
            Player player = players.get(0);
            assertEquals(firstName, player.getFirstName());
            assertEquals(lastName, player.getLastName());
            assertEquals(elo, player.getElo());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test
    void updatePlayerTest() {
        try {
            Random random = new Random();
            int playersNumber = DBUtils.getPlayersDAO().getAllPlayers().size();
            int playerId = random.nextInt(playersNumber);
            Player player = DBUtils.getPlayersDAO().getPlayerById(playerId);
            assertNotNull(player);
            int newElo = player.getElo() + 1;
            DBUtils.getPlayersDAO().updatePlayer(playerId, newElo);
            player = DBUtils.getPlayersDAO().getPlayerById(playerId);
            assertEquals(newElo, player.getElo());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void createTables() throws SQLException {
        DBUtils.getTournamentsDAO().createTableIfNotExists();
        DBUtils.getGamesDAO().createTableIfNotExists();
        DBUtils.getPlayersDAO().createTableIfNotExists();
    }

    private static void createTournaments() throws SQLException {
        List<Player> playerList = DBUtils.getPlayersDAO().getAllPlayers();

        List<TournamentService.Competition> competitions2010 = new ArrayList<>();
        List<TournamentService.Competition> competitions2015 = new ArrayList<>();
        List<TournamentService.Competition> competitions2020 = new ArrayList<>();

        competitions2010.add(TournamentService.runBigTournament(playerList, "Big Cup 2010", 9));
        competitions2010.add(TournamentService.runEliteTournament(playerList, "Elite Cup 2010", 7));
        competitions2010.add(TournamentService.runGMTournament(playerList, "GM Cup 2010", 7));
        competitions2010.add(TournamentService.runBeginnersTournament(playerList, "Beginners Cup 2010", 9));

        competitions2015.add(TournamentService.runBigTournament(playerList, "Big Cup 2015", 9));
        competitions2015.add(TournamentService.runEliteTournament(playerList, "Elite Cup 2015", 7));
        competitions2015.add(TournamentService.runGMTournament(playerList, "GM Cup 2015", 7));
        competitions2015.add(TournamentService.runBeginnersTournament(playerList, "Beginners Cup 2015", 9));

        competitions2020.add(TournamentService.runBigTournament(playerList, "Big Cup 2020", 9));
        competitions2020.add(TournamentService.runEliteTournament(playerList, "Elite Cup 2020", 7));
        competitions2020.add(TournamentService.runGMTournament(playerList, "GM Cup 2020", 7));
        competitions2020.add(TournamentService.runBeginnersTournament(playerList, "Beginners Cup 2020", 9));

        TournamentService.saveCompetitions(competitions2010, DBUtils.getTournamentsDAO(), DBUtils.getGamesDAO());
        TournamentService.saveCompetitions(competitions2015, DBUtils.getTournamentsDAO(), DBUtils.getGamesDAO());
        TournamentService.saveCompetitions(competitions2020, DBUtils.getTournamentsDAO(), DBUtils.getGamesDAO());
    }


    @Test
    void dbUtilsTest() {
        /* Ошибка подключения */
        boolean isConnected = DBUtils.createTestConnection("fhf", "jfe", "feo");
        assertFalse(isConnected);


        /* Ошибка подключения */
        isConnected = DBUtils.createConnection(
                "fhf", "jfe", "feo", "fgj");
        assertFalse(isConnected);


        /* Ошибка подключения */
        isConnected = DBUtils.createConnection(
                DBUtils.FILENAME_CONFIG, DBUtils.HOST_CNF, DBUtils.USERNAME_CNF, "fgj");
        assertFalse(isConnected);
    }

}