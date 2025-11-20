package ru.tennis.service.process;

import ru.tennis.model.dto.Score;

public class CheckGameProcess {
    protected Score checkGame(Score old) {
        Score score = new Score(old);
        if (!score.isNeedGame()) {
            return score;
        }
        int firstPlayerGames = score.getFirstPlayerGames();
        int secondPlayerGames = score.getSecondPlayerGames();
        int games = Math.max(firstPlayerGames, secondPlayerGames);
        int abs = Math.abs(firstPlayerGames - secondPlayerGames);
        if (games == 6) {
            if (!score.isTieBreak() && abs == 0) {
                score.setTieBreak(true);
                return score;
            }
            if (!score.isTieBreak() &&  abs > 1) {
                score.setFirstPlayerGames(0);
                score.setSecondPlayerGames(0);
                if (firstPlayerGames > secondPlayerGames) {
                    score.setFirstPlayerSets(score.getFirstPlayerSets() + 1);
                } else {
                    score.setSecondPlayerSets(score.getSecondPlayerSets() + 1);
                }
                return score;
            }
        }
        if (games == 7) {
            score.setTieBreak(false);
            score.setFirstPlayerGames(0);
            score.setSecondPlayerGames(0);
            if (firstPlayerGames > secondPlayerGames) {
                score.setFirstPlayerSets(score.getFirstPlayerSets() + 1);
            } else {
                score.setSecondPlayerSets(score.getSecondPlayerSets() + 1);
            }
            return score;
        }
        return score;
    }
}
