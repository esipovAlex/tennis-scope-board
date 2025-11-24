package ru.tennis.service.process;

import ru.tennis.model.dto.Score;

import java.util.Objects;

public class GreatLessProcess {
    protected Score process(Score old, int pointFirstPlayer, int pointSecondPlayer) {
        Score score = new Score(old);
        int firstPlayerPoints = score.getFirstPlayerPoints();
        int secondPlayerPoints = score.getSecondPlayerPoints();
        if (pointFirstPlayer == 1) {
            firstPlayerPoints++;
            if ((firstPlayerPoints - secondPlayerPoints) >= 2) {
                score.setFirstPlayerGames(score.getFirstPlayerGames() + 1);
                return fillField(score, true, 0, 0, true);
            } else if (firstPlayerPoints == 1 && secondPlayerPoints == 1) {
                return fillField(score, null, 0, 0, null);
            } else {
                return fillField(score, null, firstPlayerPoints, null, null);
            }
        } else {
            secondPlayerPoints++;
            if ((secondPlayerPoints - firstPlayerPoints) >= 2) {
                score.setSecondPlayerGames(score.getSecondPlayerGames() + 1);
                return fillField(score, true, 0, 0, true);
            } else if (firstPlayerPoints == 1 && secondPlayerPoints == 1) {
                return fillField(score, null, 0, 0, null);
            } else {
                return fillField(score, null, null, secondPlayerPoints, null);
            }
        }
    }

    private Score fillField(Score old, Boolean deuce, Integer firstPoint, Integer secondPoint, Boolean needGame) {
        Score score = new Score(old);
        score.setDeuce(Objects.isNull(deuce) ? old.isDeuce() : deuce);
        score.setFirstPlayerPoints(Objects.isNull(firstPoint) ? old.getFirstPlayerPoints() : firstPoint);
        score.setSecondPlayerPoints(Objects.isNull(secondPoint) ? old.getSecondPlayerPoints() : secondPoint);
        score.setNeedGame(Objects.isNull(needGame) ? old.isNeedGame() : needGame);
        return score;
    }
}
