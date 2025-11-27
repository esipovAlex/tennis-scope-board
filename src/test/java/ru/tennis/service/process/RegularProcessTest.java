package ru.tennis.service.process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tennis.model.dto.Score;

import static org.junit.jupiter.api.Assertions.*;

class RegularProcessTest {

    private RegularProcess process;
    private Score initialScore;

    @BeforeEach
    void setUp() {
        process = new RegularProcess();
        initialScore = new Score();
        initialScore.setFirstPlayerGames(0);
        initialScore.setSecondPlayerGames(0);
        initialScore.setFirstPlayerPoints(0);
        initialScore.setSecondPlayerPoints(0);
        initialScore.setDeuce(false);
        initialScore.setNeedGame(false);
    }

    @Test
    void whenFirstPlayerWinsFirstPoint() {
        Score result = process.process(initialScore, 1, 0);
        assertEquals(15, result.getFirstPlayerPoints());
        assertEquals(0, result.getSecondPlayerPoints());
        assertFalse(result.isDeuce());
        assertFalse(result.isNeedGame());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
    }

    @Test
    void whenSecondPlayerWinsFirstPoint() {
        Score result = process.process(initialScore, 0, 1);
        assertEquals(0, result.getFirstPlayerPoints());
        assertEquals(15, result.getSecondPlayerPoints());
        assertFalse(result.isDeuce());
        assertFalse(result.isNeedGame());
    }

    @Test
    void whenPointProgressionFirstPlayer() {
        Score score15 = new Score(initialScore);
        score15.setFirstPlayerPoints(15);
        Score result30 = process.process(score15, 1, 0);
        assertEquals(30, result30.getFirstPlayerPoints());
        Score result40 = process.process(result30, 1, 0);
        assertEquals(40, result40.getFirstPlayerPoints());
    }

    @Test
    void whenFirstPlayerWinsGame() {
        Score score40 = new Score(initialScore);
        score40.setFirstPlayerPoints(40);
        score40.setSecondPlayerPoints(0);
        Score result = process.process(score40, 1, 0);
        assertEquals(0, result.getFirstPlayerPoints());
        assertEquals(0, result.getSecondPlayerPoints());
        assertTrue(result.isNeedGame());
        assertEquals(1, result.getFirstPlayerGames());
        assertTrue(result.isDeuce());
    }

    @Test
    void whenSecondPlayerWinsGame() {
        Score score40 = new Score(initialScore);
        score40.setFirstPlayerPoints(0);
        score40.setSecondPlayerPoints(40);
        Score result = process.process(score40, 0, 1);
        assertEquals(0, result.getFirstPlayerPoints());
        assertEquals(0, result.getSecondPlayerPoints());
        assertTrue(result.isNeedGame());
        assertEquals(1, result.getSecondPlayerGames());
        assertTrue(result.isDeuce());
    }

    @Test
    void whenDeuceScenario4040() {
        Score score4040 = new Score(initialScore);
        score4040.setFirstPlayerPoints(40);
        score4040.setSecondPlayerPoints(40);

        Score result = process.process(score4040, 1, 0); // первый выигрывает очко

        assertEquals(100, result.getFirstPlayerPoints());
        assertEquals(40, result.getSecondPlayerPoints());
        assertFalse(result.isDeuce());  // явно установлено false
        assertFalse(result.isNeedGame());
    }

    @Test
    void whenSecondPlayerReaches40First30() {
        Score score300 = new Score(initialScore);
        score300.setFirstPlayerPoints(30);
        score300.setSecondPlayerPoints(0);

        Score result3015 = process.process(score300, 0, 1);
        assertEquals(30, result3015.getFirstPlayerPoints());
        assertEquals(15, result3015.getSecondPlayerPoints());

        Score result3030 = process.process(result3015, 0, 1);
        assertEquals(30, result3030.getFirstPlayerPoints());
    }
}