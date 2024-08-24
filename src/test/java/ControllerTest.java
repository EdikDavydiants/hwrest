import service.TournamentService;
import model.entities.Player;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.testcontainers.containers.PostgreSQLContainer;
import repository.DBUtils;
import controller.ServletStrings;
import controller.StartServlet;
import controller.CreatePlayerServlet;
import controller.GetGamesServlet;
import controller.GetPlayerServlet;
import controller.UpdatePlayerServlet;
import controller.GetTournamentsServlet;
import controller.RunTournamentServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ControllerTest {

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
    public void startServletTest() throws ServletException, IOException {
        PrintWriterTesting printWriterTest = new PrintWriterTesting(new StringWriter());

        StartServlet startServlet = new StartServlet();
        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);

        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest);

        startServlet.doGet(requestMock, responseMock);


        String outStr = printWriterTest.getStringWriter().toString();

        assertEquals(StartServlet.ALREADY_CONNECTED, outStr);
    }


    @Test
    public void getPlayerServletTest() throws ServletException, IOException, SQLException {
        GetPlayerServlet getPlayerServlet = new GetPlayerServlet();
        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);


        // Получаем игрока по индексу
        PrintWriterTesting printWriterTest1 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest1);

        Mockito.when(requestMock.getParameter(ServletStrings.ID)).thenReturn("30");
        Mockito.when(requestMock.getParameter(ServletStrings.FIRST_NAME)).thenReturn(null);
        Mockito.when(requestMock.getParameter(ServletStrings.LAST_NAME)).thenReturn(null);

        getPlayerServlet.doGet(requestMock, responseMock);
        Mockito.verify(printWriterTest1, Mockito.times(2)).write(Mockito.anyString());


        // Получаем несуществующего игрока
        PrintWriterTesting printWriterTest2 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest2);

        Mockito.when(requestMock.getParameter(ServletStrings.ID)).thenReturn("1000");
        Mockito.when(requestMock.getParameter(ServletStrings.FIRST_NAME)).thenReturn(null);
        Mockito.when(requestMock.getParameter(ServletStrings.LAST_NAME)).thenReturn(null);

        getPlayerServlet.doGet(requestMock, responseMock);
        String outStr = printWriterTest2.getStringWriter().toString();
        assertEquals(ServletStrings.NO_SUCH_PLAYER + ServletStrings.PLAYER_HEADER, outStr);


        // Получаем игрока по фамилии
        String lastName = "Hoivrvjyfsh";
        DBUtils.getPlayersDAO().createPlayer(new Player("Alex", lastName, 2000));
        DBUtils.getPlayersDAO().createPlayer(new Player("Michael", lastName, 2050));
        DBUtils.getPlayersDAO().createPlayer(new Player("Igor", lastName, 2100));

        PrintWriterTesting printWriterTest3 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest3);

        Mockito.when(requestMock.getParameter(ServletStrings.ID)).thenReturn(null);
        Mockito.when(requestMock.getParameter(ServletStrings.FIRST_NAME)).thenReturn(null);
        Mockito.when(requestMock.getParameter(ServletStrings.LAST_NAME)).thenReturn(lastName);

        getPlayerServlet.doGet(requestMock, responseMock);
        Mockito.verify(printWriterTest3, Mockito.times(4)).write(Mockito.anyString());


        // Получаем игрока по фамилии и имени
        String firstName = "Alex";
        lastName = "Fnviwotnctgd";
        DBUtils.getPlayersDAO().createPlayer(new Player(firstName, lastName, 2000));
        DBUtils.getPlayersDAO().createPlayer(new Player(firstName, lastName, 2150));
        DBUtils.getPlayersDAO().createPlayer(new Player("Michael", lastName, 2050));
        DBUtils.getPlayersDAO().createPlayer(new Player(firstName, "Kfioyi", 2100));

        PrintWriterTesting printWriterTest4 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest4);

        Mockito.when(requestMock.getParameter(ServletStrings.ID)).thenReturn(null);
        Mockito.when(requestMock.getParameter(ServletStrings.FIRST_NAME)).thenReturn("Alex");
        Mockito.when(requestMock.getParameter(ServletStrings.LAST_NAME)).thenReturn(lastName);

        getPlayerServlet.doGet(requestMock, responseMock);
        Mockito.verify(printWriterTest4, Mockito.times(3)).write(Mockito.anyString());


        // Некорректный запрос
        PrintWriterTesting printWriterTest5 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest5);

        Mockito.when(requestMock.getParameter(ServletStrings.ID)).thenReturn(null);
        Mockito.when(requestMock.getParameter(ServletStrings.FIRST_NAME)).thenReturn(null);
        Mockito.when(requestMock.getParameter(ServletStrings.LAST_NAME)).thenReturn(null);

        getPlayerServlet.doGet(requestMock, responseMock);
        outStr = printWriterTest5.getStringWriter().toString();
        assertEquals(ServletStrings.INCORRECT_REQUEST + ServletStrings.PLAYER_HEADER, outStr);


        // Некорректные параметры
        PrintWriterTesting printWriterTest6 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest6);

        Mockito.when(requestMock.getParameter(ServletStrings.ID)).thenReturn("44r");
        Mockito.when(requestMock.getParameter(ServletStrings.FIRST_NAME)).thenReturn(null);
        Mockito.when(requestMock.getParameter(ServletStrings.LAST_NAME)).thenReturn(null);

        getPlayerServlet.doGet(requestMock, responseMock);
        outStr = printWriterTest6.getStringWriter().toString();
        assertEquals(ServletStrings.INCORRECT_PARAMETERS, outStr);
    }


    @Test
    public void createPlayerServletTest() throws ServletException, IOException, SQLException {
        CreatePlayerServlet createPlayerServlet = new CreatePlayerServlet();
        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);


        // Создаем игрока и проверяем наличие
        PrintWriterTesting printWriterTest1 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest1);

        String firstName = "Kfhrutfhtf";
        String lastName = "Mhdtjkorjs";
        int elo = 1000 + new Random().nextInt(2000);
        Mockito.when(requestMock.getParameter(ServletStrings.FIRST_NAME)).thenReturn(firstName);
        Mockito.when(requestMock.getParameter(ServletStrings.LAST_NAME)).thenReturn(lastName);
        Mockito.when(requestMock.getParameter(ServletStrings.ELO)).thenReturn(String.valueOf(elo));

        createPlayerServlet.doGet(requestMock, responseMock);

        List<Player> players = DBUtils.getPlayersDAO().getPlayersByFullName(firstName, lastName);
        assertEquals(1, players.size());
        Player player = players.get(0);
        assertEquals(elo, player.getElo());
        assertEquals(firstName, player.getFirstName());
        assertEquals(lastName, player.getLastName());
        Mockito.verify(printWriterTest1, Mockito.times(1)).write(Mockito.anyString());


        // Некорректный запрос
        PrintWriterTesting printWriterTest2 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest2);

        Mockito.when(requestMock.getParameter(ServletStrings.FIRST_NAME)).thenReturn(firstName);
        Mockito.when(requestMock.getParameter(ServletStrings.LAST_NAME)).thenReturn(lastName);
        Mockito.when(requestMock.getParameter(ServletStrings.ELO)).thenReturn(null);

        createPlayerServlet.doGet(requestMock, responseMock);

        String outStr = printWriterTest2.getStringWriter().toString();
        assertEquals(ServletStrings.INCORRECT_REQUEST, outStr);


        // Некорректные параметры
        PrintWriterTesting printWriterTest3 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest3);

        Mockito.when(requestMock.getParameter(ServletStrings.FIRST_NAME)).thenReturn(firstName);
        Mockito.when(requestMock.getParameter(ServletStrings.LAST_NAME)).thenReturn(lastName);
        Mockito.when(requestMock.getParameter(ServletStrings.ELO)).thenReturn("21o7");

        createPlayerServlet.doGet(requestMock, responseMock);

        outStr = printWriterTest3.getStringWriter().toString();
        assertEquals(ServletStrings.INCORRECT_PARAMETERS, outStr);
    }


    @Test
    public void updatePlayerServletTest() throws ServletException, IOException, SQLException {
        UpdatePlayerServlet updatePlayerServlet = new UpdatePlayerServlet();
        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);


        /* Создаем игрока, обновляем и проверяем */
        PrintWriterTesting printWriterTest1 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest1);

        String firstName = "Jhdyrrieis";
        String lastName = "Lkfjtuste";
        int elo = 1000 + new Random().nextInt(2000);
        int newElo = 1000 + new Random().nextInt(2000);

        DBUtils.getPlayersDAO().createPlayer(new Player(firstName, lastName, elo));

        List<Player> players = DBUtils.getPlayersDAO().getPlayersByFullName(firstName, lastName);
        assertEquals(1, players.size());
        Player player = players.get(0);

        Mockito.when(requestMock.getParameter(ServletStrings.ID)).thenReturn(String.valueOf(player.getId()));
        Mockito.when(requestMock.getParameter(ServletStrings.ELO)).thenReturn(String.valueOf(newElo));

        updatePlayerServlet.doGet(requestMock, responseMock);

        players = DBUtils.getPlayersDAO().getPlayersByFullName(firstName, lastName);
        assertEquals(1, players.size());
        player = players.get(0);
        assertEquals(newElo, player.getElo());
        assertEquals(firstName, player.getFirstName());
        assertEquals(lastName, player.getLastName());
        Mockito.verify(printWriterTest1, Mockito.times(3)).write(Mockito.anyString());
        String outStr = printWriterTest1.getStringWriter().toString();
        assertEquals(UpdatePlayerServlet.SUCCESS + ServletStrings.PLAYER_HEADER +
                player.htmlFormString(), outStr);


        // Некорректный запрос
        PrintWriterTesting printWriterTest2 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest2);

        firstName = "Hifurlsj";
        lastName = "Kodjrnsd";
        elo = 1000 + new Random().nextInt(2000);
        newElo = 1000 + new Random().nextInt(2000);

        DBUtils.getPlayersDAO().createPlayer(new Player(firstName, lastName, elo));

        Mockito.when(requestMock.getParameter(ServletStrings.ID)).thenReturn(null);
        Mockito.when(requestMock.getParameter(ServletStrings.ELO)).thenReturn(String.valueOf(newElo));

        updatePlayerServlet.doGet(requestMock, responseMock);

        Mockito.verify(printWriterTest2, Mockito.times(1)).write(Mockito.anyString());
        outStr = printWriterTest2.getStringWriter().toString();
        assertEquals(ServletStrings.INCORRECT_REQUEST, outStr);


        // Некорректные параметры
        PrintWriterTesting printWriterTest3 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest3);

        Mockito.when(requestMock.getParameter(ServletStrings.ID)).thenReturn("0");
        Mockito.when(requestMock.getParameter(ServletStrings.ELO)).thenReturn("201o");

        updatePlayerServlet.doGet(requestMock, responseMock);

        Mockito.verify(printWriterTest3, Mockito.times(1)).write(Mockito.anyString());
        outStr = printWriterTest3.getStringWriter().toString();
        assertEquals(ServletStrings.INCORRECT_PARAMETERS, outStr);
    }


    @Test
    public void getTournamentsServletTest() throws ServletException, IOException, SQLException {
        GetTournamentsServlet getTournamentsServlet = new GetTournamentsServlet();
        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);


        PrintWriterTesting printWriterTest1 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest1);

        int tournamentNumber = DBUtils.getTournamentsDAO().getAllTournaments().size();

        getTournamentsServlet.doGet(requestMock, responseMock);

        Mockito.verify(printWriterTest1, Mockito.times(1 + tournamentNumber)).write(Mockito.anyString());
    }


    @Test
    public void runTournamentServletTest() throws ServletException, IOException, SQLException {
        RunTournamentServlet runTournamentServlet = new RunTournamentServlet();
        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);


        String name = "Ufjdhryfuk";

        List<String> typeList = List.of(RunTournamentServlet.ELITE_PARAM, RunTournamentServlet.GM_PARAM,
                RunTournamentServlet.BEGINNERS_PARAM, RunTournamentServlet.BIG_PARAM);
        for (String type: typeList) {
            PrintWriterTesting printWriterTest = Mockito.spy(new PrintWriterTesting(new StringWriter()));
            Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest);

            Mockito.when(requestMock.getParameter(ServletStrings.TOURNAMENT_NAME)).thenReturn(name);
            Mockito.when(requestMock.getParameter(ServletStrings.TOURNAMENT_TYPE)).thenReturn(type);

            runTournamentServlet.doGet(requestMock, responseMock);

            Mockito.verify(printWriterTest, Mockito.times(1)).write(Mockito.anyString());
            String outStr = printWriterTest.getStringWriter().toString();
            assertEquals(RunTournamentServlet.SUCCESS, outStr);
        }


        /* Некорректный запрос */
        PrintWriterTesting printWriterTest1 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest1);

        Mockito.when(requestMock.getParameter(ServletStrings.TOURNAMENT_NAME)).thenReturn(name);
        Mockito.when(requestMock.getParameter(ServletStrings.TOURNAMENT_TYPE)).thenReturn(null);

        runTournamentServlet.doGet(requestMock, responseMock);

        Mockito.verify(printWriterTest1, Mockito.times(1)).write(Mockito.anyString());
        String outStr = printWriterTest1.getStringWriter().toString();
        assertEquals(ServletStrings.INCORRECT_REQUEST, outStr);


        /* Некорректные параметры */
        PrintWriterTesting printWriterTest2 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest2);

        Mockito.when(requestMock.getParameter(ServletStrings.TOURNAMENT_NAME)).thenReturn(name);
        Mockito.when(requestMock.getParameter(ServletStrings.TOURNAMENT_TYPE)).thenReturn("khkh");

        runTournamentServlet.doGet(requestMock, responseMock);

        Mockito.verify(printWriterTest2, Mockito.times(1)).write(Mockito.anyString());
        outStr = printWriterTest2.getStringWriter().toString();
        assertEquals(ServletStrings.INCORRECT_PARAMETERS, outStr);
    }


    @Test
    public void getGamesServletTest() throws ServletException, IOException, SQLException {
        GetGamesServlet getGamesServlet = new GetGamesServlet();
        HttpServletRequest requestMock = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse responseMock = Mockito.mock(HttpServletResponse.class);


        /* Получение всех игр игрока */
        PrintWriterTesting printWriterTest1 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest1);

        Mockito.when(requestMock.getParameter(ServletStrings.PLAYER_ID)).thenReturn("30");
        Mockito.when(requestMock.getParameter(ServletStrings.TOURNAMENT_ID)).thenReturn(null);
        Mockito.when(requestMock.getParameter(ServletStrings.TOUR_ID)).thenReturn(null);

        getGamesServlet.doGet(requestMock, responseMock);

        int gameSize = DBUtils.getGamesDAO().getAllPlayerGames(30).size();
        Mockito.verify(printWriterTest1, Mockito.times(1 + gameSize)).write(Mockito.anyString());


        /* Получение всехх игр турнира */
        PrintWriterTesting printWriterTest2 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest2);

        Mockito.when(requestMock.getParameter(ServletStrings.PLAYER_ID)).thenReturn(null);
        Mockito.when(requestMock.getParameter(ServletStrings.TOURNAMENT_ID)).thenReturn("0");
        Mockito.when(requestMock.getParameter(ServletStrings.TOUR_ID)).thenReturn(null);

        getGamesServlet.doGet(requestMock, responseMock);

        gameSize = DBUtils.getGamesDAO().getAllTournamentGames(0).size();
        Mockito.verify(printWriterTest2, Mockito.times(1 + gameSize)).write(Mockito.anyString());


        /* Получение всех игр тура турнира */
        PrintWriterTesting printWriterTest3 = Mockito.spy(new PrintWriterTesting(new StringWriter()));
        Mockito.when(responseMock.getWriter()).thenReturn(printWriterTest3);

        Mockito.when(requestMock.getParameter(ServletStrings.PLAYER_ID)).thenReturn(null);
        Mockito.when(requestMock.getParameter(ServletStrings.TOURNAMENT_ID)).thenReturn("0");
        Mockito.when(requestMock.getParameter(ServletStrings.TOUR_ID)).thenReturn("0");

        getGamesServlet.doGet(requestMock, responseMock);

        gameSize = DBUtils.getGamesDAO().getAllTourGamesOfTournament(0, 0).size();
        Mockito.verify(printWriterTest3, Mockito.times(1 + gameSize)).write(Mockito.anyString());
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


    public static class PrintWriterTesting extends PrintWriter {
        private final StringWriter stringWriter;

        public PrintWriterTesting(@NotNull StringWriter stringWriter) {
            super(stringWriter);
            this.stringWriter = stringWriter;
        }

        public StringWriter getStringWriter() {
            return stringWriter;
        }
    }

}
