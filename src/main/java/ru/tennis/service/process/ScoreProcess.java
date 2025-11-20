package ru.tennis.service.process;

import ru.tennis.model.dto.Score;

public class ScoreProcess {

    private final TieBreakProcess tieBreakProcess;
    private final CheckGameProcess checkGameProcess;
    private final RegularProcess regularProcess;
    private final GreatLessProcess greatLessProcess;

    public ScoreProcess(TieBreakProcess tieBreakProcess,
                        CheckGameProcess checkGameProcess,
                        RegularProcess regularProcess,
                        GreatLessProcess greatLessProcess) {
        this.tieBreakProcess = tieBreakProcess;
        this.checkGameProcess = checkGameProcess;
        this.regularProcess = regularProcess;
        this.greatLessProcess = greatLessProcess;
    }

    public Score addPoint(Score old, int pointFirstPlayer, int pointSecondPlayer) {
        Score score;
        boolean onlyPoint = old.isDeuce();
        boolean tieBreak = old.isTieBreak();
        if (tieBreak) {
            score = tieBreakProcess.process(old, pointFirstPlayer, pointSecondPlayer);
            score = checkGameProcess.checkGame(score);
            return score;
        }
        if (onlyPoint ) {
            score = regularProcess.process(old, pointFirstPlayer, pointSecondPlayer);
            score = checkGameProcess.checkGame(score);
            return score;
        } else {
            score = greatLessProcess.process(old, pointFirstPlayer, pointSecondPlayer);
            score = checkGameProcess.checkGame(score);
            return score;
        }
    }
}
