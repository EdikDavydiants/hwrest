package servlets;

import entities.Tournament;
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

@WebServlet("/gettournaments")
public class GetTournamentsServlet extends HttpServlet {
    public static final String HEADER = "id  |  name<br><br>";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(ServletStrings.CONTENT_TYPE);
        PrintWriter printWriter = resp.getWriter();

        try {
            List<Tournament> tournamentList = DBUtils.getTournamentsDAO().getAllTournaments();

            printWriter.write(HEADER);
            for (Tournament tournament: tournamentList) {
                printWriter.write(tournament.htmlFormString());
            }
        }
        catch (SQLException e) {
            printWriter.write(ServletStrings.DB_ERROR);
        }

        printWriter.close();
    }


}