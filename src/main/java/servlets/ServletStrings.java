package servlets;

import java.io.PrintWriter;

public class ServletStrings {
    public static final String CONTENT_TYPE = "text/html";
    public static final String INCORRECT_REQUEST = "Enter correct request!<br>";
    public static final String NO_SUCH_PLAYER = "No such player.<br>";
    public static final String INCORRECT_PARAMETERS = "Enter correct format of parameters!<br>";
    public static final String DB_ERROR = "Database error!<br>";
    public static final String PLAYER_HEADER = "id  |  first name  |  last name  |  elo<br><br>";

    public static final String ID = "id";
    public static final String FIRST_NAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String ELO = "elo";

    public static final String TOURNAMENT_TYPE = "type";
    public static final String TOURNAMENT_NAME = "name";

    public static final String TOURNAMENT_ID = "tournament_id";
    public static final String TOUR_ID = "tour_id";
    public static final String PLAYER_ID = "player_id";


    public static boolean areParamNamesCorrect (String... strings) {
        for(String string: strings) {
            if (string == null) {
                return false;
            }
        }
        return true;
    }

    public static int tryToParseInt(String paramStr, PrintWriter printWriter) {
        int playerId = -1;
        try {
            playerId = Integer.parseInt(paramStr);
        } catch (NumberFormatException e) {
            printWriter.write(INCORRECT_PARAMETERS);
        }
        return playerId;
    }

}
