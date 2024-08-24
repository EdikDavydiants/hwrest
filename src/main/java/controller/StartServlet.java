package controller;

import repository.DBUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/start")
public class StartServlet extends HttpServlet {

    public static final String GREETING = "Welcome to the PlayerDataBase!<br>";
    public static final String ALREADY_CONNECTED = "You are in the PlayerDataBase!<br>";
    public static final String FAIL_TO_CONNECT = "Connection has been failed!<br>";
    public static final String PROBLEMS_WITH_DB = "Problems with DataBase!<br>";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType(ServletStrings.CONTENT_TYPE);
        PrintWriter printWriter = resp.getWriter();

        if (DBUtils.getConnection() == null) {
            if(DBUtils.createConnection(DBUtils.FILENAME_CONFIG, DBUtils.HOST_CNF,
                    DBUtils.USERNAME_CNF, DBUtils.PASSWORD_CNF)) {
                DBUtils.createDAOs();
                try {
                    DBUtils.getPlayersDAO().createTableIfNotExists();
                    DBUtils.getGamesDAO().createTableIfNotExists();
                    DBUtils.getTournamentsDAO().createTableIfNotExists();
                    printWriter.write(GREETING);
                }
                catch (SQLException e) {
                    printWriter.write(PROBLEMS_WITH_DB);
                }
            } else {
                printWriter.write(FAIL_TO_CONNECT);
            }
        } else {
            printWriter.write(ALREADY_CONNECTED);
        }
        printWriter.close();
    }
}
