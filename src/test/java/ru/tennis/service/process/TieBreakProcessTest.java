package ru.tennis.service.process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tennis.model.dto.Score;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TieBreakProcessTest {

    private TieBreakProcess tieBreakProcess;
    private Score baseScore;

    @BeforeEach
    void setUp() {
        tieBreakProcess = new TieBreakProcess();
        baseScore = new Score();
        baseScore.setId(UUID.randomUUID());
        baseScore.setFirstPlayerSets(0);
        baseScore.setSecondPlayerSets(0);
        baseScore.setFirstPlayerGames(0);
        baseScore.setSecondPlayerGames(0);
        baseScore.setFirstPlayerPoints(0);
        baseScore.setSecondPlayerPoints(0);
        baseScore.setDeuce(true);
        baseScore.setTieBreak(true);
        baseScore.setNeedGame(false);
    }

    @Test
    void whenAddPointsThenNormalAccumulation() {
        Score result = tieBreakProcess.process(baseScore, 1, 0);
        assertEquals(1, result.getFirstPlayerPoints());
        assertEquals(0, result.getSecondPlayerPoints());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
        assertFalse(result.isNeedGame());
    }

    @Test
    void whenWinConditionFirstPlayer75() {
        baseScore.setFirstPlayerPoints(1);
        baseScore.setSecondPlayerPoints(0);
        Score result = tieBreakProcess.process(baseScore, 1, 0);
        assertEquals(2, result.getFirstPlayerPoints());
        assertEquals(0, result.getSecondPlayerPoints());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
        assertEquals(2, result.getFirstPlayerPoints());
        assertEquals(0, result.getSecondPlayerPoints());
        assertFalse(result.isNeedGame());
    }

    @Test
    void whenWinConditionSecondPlayer02() {
        baseScore.setFirstPlayerPoints(0);
        baseScore.setSecondPlayerPoints(2);
        Score result = tieBreakProcess.process(baseScore, 0, 1);
        assertEquals(0, result.getFirstPlayerPoints());
        assertEquals(3, result.getSecondPlayerPoints());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
        assertEquals(0, result.getFirstPlayerPoints());
        assertEquals(3, result.getSecondPlayerPoints());
        assertFalse(result.isNeedGame());
    }

    @Test
    void whenNoWin66() {
        baseScore.setFirstPlayerPoints(6);
        baseScore.setSecondPlayerPoints(6);
        Score result = tieBreakProcess.process(baseScore, 0, 0);
        assertEquals(6, result.getFirstPlayerPoints());
        assertEquals(6, result.getSecondPlayerPoints());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
        assertFalse(result.isNeedGame());
    }

    @Test
    void whenNoWin76() {
        baseScore.setFirstPlayerPoints(6);
        baseScore.setSecondPlayerPoints(6);
        Score result = tieBreakProcess.process(baseScore, 1, 0);
        assertEquals(7, result.getFirstPlayerPoints());
        assertEquals(6, result.getSecondPlayerPoints());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
        assertFalse(result.isNeedGame());
    }

    @Test
    void whenWin108() {
        baseScore.setFirstPlayerPoints(9);
        baseScore.setSecondPlayerPoints(8);
        Score result = tieBreakProcess.process(baseScore, 1, 0);
        assertEquals(0, result.getFirstPlayerPoints());
        assertEquals(0, result.getSecondPlayerPoints());
        assertEquals(1, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
        assertTrue(result.isNeedGame());
    }

    @Test
    void whenMultiplePointsAddition() {
        Score result = tieBreakProcess.process(baseScore, 2, 3);
        assertEquals(2, result.getFirstPlayerPoints());
        assertEquals(3, result.getSecondPlayerPoints());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
        assertFalse(result.isNeedGame());
    }

    @Test
    void whenInitialStateZeroPoints() {
        Score result = tieBreakProcess.process(baseScore, 0, 0);
        assertEquals(0, result.getFirstPlayerPoints());
        assertEquals(0, result.getSecondPlayerPoints());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
        assertFalse(result.isNeedGame());
    }
}
