package ru.tennis.service.process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tennis.model.dto.Score;

import static org.junit.jupiter.api.Assertions.*;

class GreatLessProcessTest {

    private GreatLessProcess process;
    private Score initialScore;

    @BeforeEach
    void setUp() {
        process = new GreatLessProcess();
        initialScore = new Score();
        initialScore.setFirstPlayerGames(0);
        initialScore.setSecondPlayerGames(0);
        initialScore.setFirstPlayerPoints(0);
        initialScore.setSecondPlayerPoints(0);
        initialScore.setDeuce(false);
        initialScore.setNeedGame(false);
    }

    @Test
    void whenFirstPlayerWinsPoint() {
        Score result = process.process(initialScore, 1, 0);

        assertEquals(1, result.getFirstPlayerPoints());
        assertEquals(0, result.getSecondPlayerPoints());
        assertFalse(result.isDeuce());
        assertFalse(result.isNeedGame());
    }

    @Test
    void whenSecondPlayerWinsPoint() {
        Score result = process.process(initialScore, 0, 1);

        assertEquals(0, result.getFirstPlayerPoints());
        assertEquals(1, result.getSecondPlayerPoints());
        assertFalse(result.isDeuce());
        assertFalse(result.isNeedGame());
    }

    @Test
    void whenPointsEqual1ThenEnterDeuce() {
        initialScore.setFirstPlayerPoints(1);
        initialScore.setSecondPlayerPoints(1);
        Score result = process.process(initialScore, 1, 0);
        assertEquals(2, result.getFirstPlayerPoints());
        assertEquals(1, result.getSecondPlayerPoints());
        assertFalse(result.isDeuce());
        assertFalse(result.isNeedGame());
    }

    @Test
    void whenFirstPlayerWinsGameFrom400() {
        initialScore.setFirstPlayerPoints(3);
        initialScore.setSecondPlayerPoints(0);
        Score result = process.process(initialScore, 1, 0);
        assertEquals(0, result.getFirstPlayerPoints());
        assertEquals(0, result.getSecondPlayerPoints());
        assertTrue(result.isNeedGame());
        assertEquals(1, result.getFirstPlayerGames());
        assertTrue(result.isDeuce());
    }

    @Test
    void whenSecondPlayerWinsGameFrom040() {
        initialScore.setFirstPlayerPoints(0);
        initialScore.setSecondPlayerPoints(3);
        Score result = process.process(initialScore, 0, 1);
        assertEquals(0, result.getFirstPlayerPoints());
        assertEquals(0, result.getSecondPlayerPoints());
        assertTrue(result.isNeedGame());
        assertEquals(1, result.getSecondPlayerGames());
        assertTrue(result.isDeuce());
    }

    @Test
    void whenDeuceScenario4040ToAdIn() {
        initialScore.setFirstPlayerPoints(3);
        initialScore.setSecondPlayerPoints(3);
        initialScore.setDeuce(true);
        Score result = process.process(initialScore, 1, 0);
        assertEquals(4, result.getFirstPlayerPoints());
        assertEquals(3, result.getSecondPlayerPoints());
        assertTrue(result.isDeuce());
        assertFalse(result.isNeedGame());
    }
}


