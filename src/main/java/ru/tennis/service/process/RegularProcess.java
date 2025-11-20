package ru.tennis.service.process;

import ru.tennis.model.dto.Score;

import java.util.Objects;

public class RegularProcess {
    protected Score process(Score old, int pointFirstPlayer, int pointSecondPlayer) {
        Score score = new Score(old);
        int firstPlayerPoints = old.getFirstPlayerPoints();
        int secondPlayerPoints = old.getSecondPlayerPoints();
        int pointsFirstNew;
        int pointsSecondNew;
        if (pointFirstPlayer == 1) {
            pointsFirstNew = getPoint(firstPlayerPoints);
            if ((pointsFirstNew - secondPlayerPoints) > 60) {
                score.setFirstPlayerGames(score.getFirstPlayerGames() + 1);
                score = fillField(score, true, 0,0, true);
            } else {
                score.setFirstPlayerPoints(pointsFirstNew);
            }
        } else {
            pointsSecondNew = getPoint(secondPlayerPoints);
            if ((pointsSecondNew - firstPlayerPoints) > 60) {
                score.setSecondPlayerGames(score.getSecondPlayerGames() + 1);
                score = fillField(score, true, 0,0, true);
            } else {
                score.setSecondPlayerPoints(pointsSecondNew);
            }
        }
        if (score.getFirstPlayerPoints() == 40 && score.getSecondPlayerPoints() == 40) {
            score = fillField(score, false, 0,0, false);
            score.setDeuce(false); // переходим на больше-меньше
        }
        return score;
    }

    private int getPoint(int point) {
        return switch (point) {
            case 0 -> 15;
            case 15 -> 30;
            case 30 -> 40;
            case 40 -> 100;
            default -> point;
        };
    }

    private Score fillField(Score old, Boolean deuce, Integer firstPoint, Integer secondPoint, Boolean needGame) {
        Score score = new Score(old);
        score.setDeuce(Objects.isNull(deuce) ? old.isDeuce(): deuce);
        score.setFirstPlayerPoints(Objects.isNull(firstPoint) ? old.getFirstPlayerPoints() : firstPoint);
        score.setSecondPlayerPoints(Objects.isNull(secondPoint) ? old.getSecondPlayerPoints() : secondPoint);
        score.setNeedGame(Objects.isNull(needGame) ? old.isNeedGame() : needGame);
        return score;
    }
}
