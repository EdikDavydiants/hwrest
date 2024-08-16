package servlets;

import entities.Game;
import repository.DBUtils;
import servlets.ServletStrings;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;


@WebServlet("/getgames")
public class GetGamesServlet extends HttpServlet {
    public static final String HEADER = "id  |  white id  |  black id  |  result  |  tournament id  |  tour id<br><br>";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(ServletStrings.CONTENT_TYPE);
        PrintWriter printWriter = resp.getWriter();

        List<Game> gameList = null;
        try {
            String playerIdReq = req.getParameter(ServletStrings.PLAYER_ID);
            String tournamentIdReq = req.getParameter(ServletStrings.TOURNAMENT_ID);
            String tourIdReq = req.getParameter(ServletStrings.TOUR_ID);
            if (playerIdReq != null) {
                int playerId = ServletStrings.tryToParseInt(playerIdReq, printWriter);
                if (playerId != -1) {
                    gameList = DBUtils.getGamesDAO().getAllPlayerGames(playerId);
                }
            } else if (tournamentIdReq != null) {
                int tournamentId = ServletStrings.tryToParseInt(tournamentIdReq, printWriter);
                if (tournamentId != -1) {
                    if (tourIdReq == null) {
                        gameList = DBUtils.getGamesDAO().getAllTournamentGames(tournamentId);
                    } else {
                        int tourId = ServletStrings.tryToParseInt(tourIdReq, printWriter);
                        if (tourId != -1) {
                            gameList = DBUtils.getGamesDAO().getAllTourGamesOfTournament(tournamentId, tourId);
                        }
                    }
                }
            } else {
                printWriter.write(ServletStrings.INCORRECT_REQUEST);
            }

            if (gameList != null) {
                printWriter.write(HEADER);
                for(Game game: gameList) {
                    printWriter.write(game.htmlFormString());
                }
            }

            printWriter.close();
        }
        catch (SQLException e) {
            printWriter.write(ServletStrings.DB_ERROR);
            printWriter.close();
        }

    }


}
