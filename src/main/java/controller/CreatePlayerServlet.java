package controller;

import repository.DBUtils;
import model.entities.Player;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/createplayer")
public class CreatePlayerServlet extends HttpServlet {
    public static final String SUCCESS = "Player has been created.<br>";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(ServletStrings.CONTENT_TYPE);
        PrintWriter printWriter = resp.getWriter();

        String firstNameReq = req.getParameter(ServletStrings.FIRST_NAME);
        String lastNameReq = req.getParameter(ServletStrings.LAST_NAME);
        String eloReq = req.getParameter(ServletStrings.ELO);

        if(!ServletStrings.areParamNamesCorrect(firstNameReq, lastNameReq, eloReq)) {
            printWriter.write(ServletStrings.INCORRECT_REQUEST);
        } else {
            int elo = ServletStrings.tryToParseInt(eloReq, printWriter);
            if (elo == -1) {
                printWriter.close();
                return;
            }
            Player newPlayer = new Player(firstNameReq, lastNameReq, elo);
            try {
                DBUtils.getPlayersDAO().createPlayer(newPlayer);
            }
            catch (SQLException e) {
                printWriter.write(ServletStrings.DB_ERROR);
                printWriter.close();
                return;
            }
            printWriter.write(SUCCESS);
        }
        printWriter.close();
    }
}
