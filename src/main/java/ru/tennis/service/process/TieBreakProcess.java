package ru.tennis.service.process;

import ru.tennis.model.dto.Score;

public class TieBreakProcess {

    public Score process(Score old, int pointFirstPlayer, int pointSecondPlayer) {
        Score score = new Score(old);
        int firstPlayerPoints = old.getFirstPlayerPoints() + pointFirstPlayer;
        int secondPlayerPoints = old.getSecondPlayerPoints() + pointSecondPlayer;
        int max = Math.max(firstPlayerPoints, secondPlayerPoints);
        int abs = Math.abs(firstPlayerPoints - secondPlayerPoints);
        if (max >= 7 && abs > 1) {
            if (firstPlayerPoints > secondPlayerPoints) {
                score.setFirstPlayerGames(old.getFirstPlayerGames() + 1);
            } else {
                score.setSecondPlayerGames(old.getSecondPlayerGames() + 1);
            }
            score.setFirstPlayerPoints(0);
            score.setSecondPlayerPoints(0);
            score.setNeedGame(true);
        } else {
            score.setFirstPlayerPoints(firstPlayerPoints);
            score.setSecondPlayerPoints(secondPlayerPoints);
        }
        return score;
    }
}
