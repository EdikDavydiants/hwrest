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
import java.util.ArrayList;
import java.util.List;


@WebServlet("/getplayer")
public class GetPlayerServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(ServletStrings.CONTENT_TYPE);
        PrintWriter printWriter = resp.getWriter();

        Player player = null;
        List<Player> players = new ArrayList<>();
        try {
            String idReq = req.getParameter(ServletStrings.ID);
            if (idReq != null) {
                int id;
                try {
                    id = Integer.parseInt(idReq);
                } catch (NumberFormatException e) {
                    printWriter.write(ServletStrings.INCORRECT_PARAMETERS);
                    printWriter.close();
                    return;
                }
                player = DBUtils.getPlayersDAO().getPlayerById(id);
                if (player == null) {
                    printWriter.write(ServletStrings.NO_SUCH_PLAYER);
                }
            } else {
                String firstNameReq = req.getParameter(ServletStrings.FIRST_NAME);
                String lastNameReq = req.getParameter(ServletStrings.LAST_NAME);
                if (firstNameReq == null && lastNameReq == null) {
                    printWriter.write(ServletStrings.INCORRECT_REQUEST);
                } else if (firstNameReq == null) {
                    players = DBUtils.getPlayersDAO().getPlayersByLastName(lastNameReq);
                } else {
                    players = DBUtils.getPlayersDAO().getPlayersByFullName(firstNameReq, lastNameReq);
                }
            }
            printWriter.write(ServletStrings.PLAYER_HEADER);
            if (player != null) {
                printWriter.write(player.htmlFormString());
            } else {
                for (Player player_: players) {
                    printWriter.write(player_.htmlFormString());
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
