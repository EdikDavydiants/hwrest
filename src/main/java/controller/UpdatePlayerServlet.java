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

@WebServlet("/updateplayer")
public class UpdatePlayerServlet extends HttpServlet {
    public static final String SUCCESS = "Player has been updated.<br><br>";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(ServletStrings.CONTENT_TYPE);
        PrintWriter printWriter = resp.getWriter();

        Player player = null;
        try {
            String idReq = req.getParameter(ServletStrings.ID);
            String eloReq = req.getParameter(ServletStrings.ELO);
            if(!ServletStrings.areParamNamesCorrect(idReq, eloReq)) {
                printWriter.write(ServletStrings.INCORRECT_REQUEST);
            } else {
                int id = ServletStrings.tryToParseInt(idReq, printWriter);
                int elo = ServletStrings.tryToParseInt(eloReq, printWriter);
                if (id == -1 || elo == -1) {
                    printWriter.close();
                    return;
                }
                DBUtils.getPlayersDAO().updatePlayer(id, elo);
                player = DBUtils.getPlayersDAO().getPlayerById(id);
            }

            if (player != null) {
                printWriter.write(SUCCESS);
                printWriter.write(ServletStrings.PLAYER_HEADER);
                printWriter.write(player.htmlFormString());
            }
            printWriter.close();
        }
        catch (SQLException e) {
            printWriter.write(ServletStrings.DB_ERROR);
            printWriter.close();
        }
    }
}
