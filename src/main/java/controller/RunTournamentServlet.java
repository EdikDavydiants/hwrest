package controller;

import model.entities.Player;
import service.TournamentService;
import repository.DBUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/runtournament")
public class RunTournamentServlet extends HttpServlet {
    public static final String ELITE_PARAM = "elite";
    public static final String GM_PARAM = "gm";
    public static final String BIG_PARAM = "big";
    public static final String BEGINNERS_PARAM = "beginners";
    public static final String SUCCESS = "Tournament has been run.";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(ServletStrings.CONTENT_TYPE);
        PrintWriter printWriter = resp.getWriter();

        String tournamentNameReq = req.getParameter(ServletStrings.TOURNAMENT_NAME);
        String tournamentTypeReq = req.getParameter(ServletStrings.TOURNAMENT_TYPE);
        if(ServletStrings.areParamNamesCorrect(tournamentNameReq, tournamentTypeReq)) {
            try {
                List<Player> allPlayerList = DBUtils.getPlayersDAO().getAllPlayers();
                TournamentService.Competition competition;
                switch (tournamentTypeReq) {
                    case ELITE_PARAM:
                        competition = TournamentService.runEliteTournament(allPlayerList, tournamentNameReq, 7);
                        break;
                    case GM_PARAM:
                        competition = TournamentService.runGMTournament(allPlayerList, tournamentNameReq, 7);
                        break;
                    case BIG_PARAM:
                        competition = TournamentService.runBigTournament(allPlayerList, tournamentNameReq, 9);
                        break;
                    case BEGINNERS_PARAM:
                        competition = TournamentService.runBeginnersTournament(allPlayerList, tournamentNameReq, 9);
                        break;
                    default:
                        printWriter.write(ServletStrings.INCORRECT_PARAMETERS);
                        printWriter.close();
                        return;
                }
                TournamentService.saveCompetitions(List.of(competition), DBUtils.getTournamentsDAO(), DBUtils.getGamesDAO());
                printWriter.write(RunTournamentServlet.SUCCESS);
            }
            catch (SQLException e) {
                printWriter.write(ServletStrings.DB_ERROR);
            }
        } else {
            printWriter.write(ServletStrings.INCORRECT_REQUEST);
        }
    }
}
