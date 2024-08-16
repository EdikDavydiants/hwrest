package entities;


import businesslogic.TournamentManager;

import java.util.Random;

public class Game {
    private int id;
    private TournamentManager.TournamentPlayer playerW;
    private TournamentManager.TournamentPlayer playerB;
    private int whiteId;
    private int blackId;
    private int result;
    private int tournamentId;
    private int tour;


    public Game(TournamentManager.TournamentPlayer playerW, TournamentManager.TournamentPlayer playerB, int tour) {
        this.playerW = playerW;
        this.playerB = playerB;
        this.tour = tour;
    }

    public Game(int id, int whiteId, int blackId, int result, int tournamentId, int tour) {
        this.id = id;
        this.whiteId = whiteId;
        this.blackId = blackId;
        this.result = result;
        this.tournamentId = tournamentId;
        this.tour = tour;
    }



    public Game playGame() {
        int result = createResult();
        if (result == 0) {
            playerW.judgeWin();
        } else if (result == 1) {
            playerW.judgeDraw();
            playerB.judgeDraw();
        } else {
            playerB.judgeWin();
        }
        this.result = result;
        return this;
    }


    private int createResult() {
        Random random = new Random();
        int d_elo = playerB.getPlayer().getElo() - playerW.getPlayer().getElo();
        int d_elo_abs = Math.abs(d_elo);
        float l1 = 0.25f;
        float r1 = 0.75f;
        float l2 = 0.15f;
        float r2 = 0.55f;
        float l3 = 0.05f;
        float r3 = 0.30f;
        float l4 = 0.02f;
        float r4 = 0.15f;
        if (d_elo_abs < 50) {
            return bounds(l1, r1, random);
        } else if (d_elo_abs < 150) {
            if (d_elo > 0) {
                return bounds(l2, r2, random);
            } else {
                return bounds(1f - r2, 1f - l2, random);
            }
        } else if (d_elo_abs < 350) {
            if (d_elo > 0) {
                return bounds(l3, r3, random);
            } else {
                return bounds(1f - r3, 1f - l3, random);
            }
        } else {
            if (d_elo > 0) {
                return bounds(l4, r4, random);
            } else {
                return bounds(1f - r4, 1f - l4, random);
            }
        }
    }


    public int bounds(float bound1, float bound2, Random random) {
        float rand = random.nextFloat();
        if (rand < bound1) {
            return 0;
        } else if (rand >= bound1 && rand < bound2) {
            return 1;
        } else {
            return 2;
        }
    }


    public int getId() {
        return id;
    }

    public int getWhiteId() {
        if (playerW == null) {
            return whiteId;
        } else {
            return playerW.getPlayer().getId();
        }
    }

    public int getBlackId() {
        if (playerB == null) {
            return blackId;
        } else {
            return playerB.getPlayer().getId();
        }
    }

    public int getResult() {
        return result;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public int getTour() {
        return tour;
    }

    public String htmlFormString() {
        return id + "   " + whiteId + " " + blackId + "  "
                + result + "  " + tournamentId + "  " + tour + "<br>";
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }
}
