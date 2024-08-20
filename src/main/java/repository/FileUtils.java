package repository;

import entities.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FileUtils {

    private static final String FILENAME_PLAYERS = "players.txt";


    public static List<Player> createPlayerList() {
        List<Player> playerList = new ArrayList<>(200);
        Random random = new Random();

        try(InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(FILENAME_PLAYERS)) {
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                reader.lines().
                        forEach(line -> {
                            String[] strArr = line.split(" ");
                            if (strArr.length == 2) {
                                Player player = new Player(playerList.size(), strArr[0], strArr[1], 0, null);
                                player.generateElo(random);
                                playerList.add(player);
                            }
                        });
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return playerList;
    }
}
