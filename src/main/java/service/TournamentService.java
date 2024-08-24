package service;

import model.entities.Game;
import model.entities.Player;
import model.entities.Tournament;
import repository.GamesDAO;
import repository.TournamentsDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TournamentService {

    public static void saveCompetitions(List<Competition> competitions, TournamentsDAO tournamentsDAO,
                                        GamesDAO gamesDAO) throws SQLException {
        int tournamentMaxId = tournamentsDAO.getMaxId();
        int gameMaxId = gamesDAO.getMaxId();
        injectIds(competitions, tournamentMaxId, gameMaxId);
        List<Tournament> tournamentList = extractTournamentList(competitions);
        List<Game> gameList = extractGameList(competitions);
        tournamentsDAO.saveTournaments(tournamentList);
        gamesDAO.saveGames(gameList);
    }

    private static void injectIds(List<Competition> competitions, int tournamentMaxId, int gameMaxId) {
        for (Competition competition: competitions) {
            competition.getTournament().setId(++tournamentMaxId);
            for (Tour tour: competition.getTours()) {
                for (Game game: tour.getGames()) {
                    game.setTournamentId(tournamentMaxId);
                    game.setId(++gameMaxId);
                }
            }
        }
    }

    private static List<Tournament> extractTournamentList(List<Competition> competitionList) {
        return competitionList.stream().map(Competition::getTournament).toList();
    }

    private static List<Game> extractGameList(List<Competition> competitionList) {
        return competitionList.stream().
                flatMap(competition -> Arrays.stream(competition.getTours())).
                flatMap(tour -> tour.getGames().stream()).
                toList();
    }


    public static Competition runBigTournament(List<Player> playerList, String name, int numberOfTours) {
        List<TournamentPlayer> players = createBigPlayerSet(playerList);
        Competition competition = new Competition(players, numberOfTours, new Tournament(name));
        competition.runCompetition();
        return competition;
    }

    public static Competition runEliteTournament(List<Player> playerList, String name, int numberOfTours) {
        List<TournamentPlayer> players = createElitePlayerSet(playerList);
        Competition competition = new Competition(players, numberOfTours, new Tournament(name));
        competition.runCompetition();
        return competition;
    }

    public static Competition runBeginnersTournament(List<Player> playerList, String name, int numberOfTours) {
        List<TournamentPlayer> players = createBeginnersPlayerSet(playerList);
        Competition competition = new Competition(players, numberOfTours, new Tournament(name));
        competition.runCompetition();
        return competition;
    }

    public static Competition runGMTournament(List<Player> playerList, String name, int numberOfTours) {
        List<TournamentPlayer> players = createGMPlayerSet(playerList);
        Competition competition = new Competition(players, numberOfTours, new Tournament(name));
        competition.runCompetition();
        return competition;
    }



    private static List<TournamentPlayer> createElitePlayerSet(List<Player> playerList) {
        return createPlayerSet(playerList, 2700, 3000);
    }

    private static List<TournamentPlayer> createGMPlayerSet(List<Player> playerList) {
        return createPlayerSet(playerList, 2500, 2700);
    }

    private static List<TournamentPlayer> createBigPlayerSet(List<Player> playerList) {
        return createPlayerSet(playerList, 2000, 2500);
    }

    private static List<TournamentPlayer> createBeginnersPlayerSet(List<Player> playerList) {
        return createPlayerSet(playerList, 0, 2000);
    }


    private static List<TournamentPlayer> createPlayerSet(List<Player> playerList, int bottomBound, int topBound) {
        return playerList.stream().
                filter(player -> player.getElo() >= bottomBound && player.getElo() < topBound).
                map(TournamentPlayer::new).
                toList();
    }

    private static List<TournamentPlayer> sortPlayersByScore(List<TournamentPlayer> players) {
        List<TournamentPlayer> playerList = new ArrayList<>();
        players.stream().
                sorted((pl1, pl2) -> Integer.compare(pl2.getScore(), pl1.getScore())).
                forEach(playerList::add);
        return playerList;
    }


    public static class TournamentPlayer {
        private final Player player;
        private int score = 0;
        private int plus = 0;

        public TournamentPlayer(Player player) {
            this.player = player;
        }

        public void judgeWin() {
            score += 2;
        }

        public void judgeDraw() {
            score += 1;
        }

        public int getScore() {
            return score;
        }

        public Player getPlayer() {
            return player;
        }
    }


    public static class Tour {
        private final int tourNumber;
        private final List<Game> games = new ArrayList<>();

        public Tour(int tourNumber) {
            this.tourNumber = tourNumber;
        }

        public void makeTour(List<TournamentPlayer> players) {
            for (int i = 2; i <= players.size(); i += 2) {
                TournamentPlayer playerW = players.get(i-2);
                TournamentPlayer playerB = players.get(i-1);
                Game game = new Game(playerW, playerB, tourNumber);
                games.add(game.playGame());
            }
            if (players.size() % 2 != 0) {
                players.get(players.size() - 1).judgeWin();
                players.get(players.size() - 1).plus++;
            }
        }

        public List<Game> getGames() {
            return games;
        }
    }


    public static class Competition {
        private final Tournament tournament;
        private final Tour[] tours;
        private final List<TournamentPlayer> players;

        public Competition(List<TournamentPlayer> players, int numberOfTours, Tournament tournament) {
            this.tournament = tournament;
            this.players = players;
            tours = new Tour[numberOfTours];
        }

        public void runCompetition() {
            for (int i = 0; i < tours.length; i++) {
                tours[i] = new Tour(i + 1);
                tours[i].makeTour(sortPlayersByScore(players));
            }
        }

        public Tour[] getTours() {
            return tours;
        }

        public Tournament getTournament() {
            return tournament;
        }
    }
}
