package ru.tennis.service.process;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.tennis.model.dto.Score;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CheckGameProcessTest {

    private CheckGameProcess checkGameProcess;

    @BeforeEach
    void setUp() {
        checkGameProcess = new CheckGameProcess();
    }

    @Test
    void whenNeedGameIsFalseThenCheckGame() {
        Score old = new Score(UUID.randomUUID());
        old.setNeedGame(false);
        Score result = checkGameProcess.checkGame(old);
        assertEquals(old.getFirstPlayerSets(), result.getFirstPlayerSets());
        assertEquals(old.getSecondPlayerSets(), result.getSecondPlayerSets());
        assertFalse(result.isNeedGame());
        assertNotSame(old, result); // это копия
    }

    @Test
    void whenGamesAre66AndTieBreakOffThenCheckGame() {
        Score old = new Score(UUID.randomUUID());
        old.setFirstPlayerGames(6);
        old.setSecondPlayerGames(6);
        old.setTieBreak(false);
        old.setNeedGame(true);
        Score result = checkGameProcess.checkGame(old);
        assertTrue(result.isTieBreak());
        assertEquals(6, result.getFirstPlayerGames());
        assertEquals(6, result.getSecondPlayerGames());
        assertEquals(0, result.getFirstPlayerSets());
        assertEquals(0, result.getSecondPlayerSets());
    }

    @Test
    void whenGamesAre75AndTieBreakOffThenCheckGame() {
        Score old = new Score(UUID.randomUUID());
        old.setFirstPlayerGames(7);
        old.setSecondPlayerGames(5);
        old.setTieBreak(false);
        old.setNeedGame(true);
        old.setFirstPlayerSets(1);
        old.setSecondPlayerSets(2);
        Score result = checkGameProcess.checkGame(old);
        assertFalse(result.isTieBreak());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
        assertEquals(2, result.getFirstPlayerSets());  // +1
        assertEquals(2, result.getSecondPlayerSets());
    }

    @Test
    void whenGamesAre57AndTieBreakOffThenCheckGame() {
        Score old = new Score(UUID.randomUUID());
        old.setFirstPlayerGames(5);
        old.setSecondPlayerGames(7);
        old.setTieBreak(false);
        old.setNeedGame(true);
        old.setFirstPlayerSets(3);
        old.setSecondPlayerSets(1);

        Score result = checkGameProcess.checkGame(old);

        assertFalse(result.isTieBreak());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
        assertEquals(3, result.getFirstPlayerSets());
        assertEquals(2, result.getSecondPlayerSets());  // +1
    }

    @Test
    void whenGamesAre76DifferenceIs1ThenCheckGame() {
        Score old = new Score(UUID.randomUUID());
        old.setFirstPlayerGames(7);
        old.setSecondPlayerGames(6);
        old.setTieBreak(false);
        old.setNeedGame(true);

        Score result = checkGameProcess.checkGame(old);

        assertFalse(result.isTieBreak());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
        assertEquals(1, result.getFirstPlayerSets());
        assertEquals(0, result.getSecondPlayerSets());
    }

    @Test
    void whenTieBreakIsAlreadyOnThenCheckGame() {
        Score old = new Score(UUID.randomUUID());
        old.setFirstPlayerGames(7);
        old.setSecondPlayerGames(7);
        old.setTieBreak(true);
        old.setNeedGame(true);

        Score result = checkGameProcess.checkGame(old);

        assertFalse(result.isTieBreak());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
    }

    @Test
    void whenGamesAre70ThenCheckGame() {
        Score old = new Score(UUID.randomUUID());
        old.setFirstPlayerGames(7);
        old.setSecondPlayerGames(0);
        old.setTieBreak(false);
        old.setNeedGame(true);
        old.setFirstPlayerSets(0);
        old.setSecondPlayerSets(0);

        Score result = checkGameProcess.checkGame(old);

        assertFalse(result.isTieBreak());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
        assertEquals(1, result.getFirstPlayerSets());
        assertEquals(0, result.getSecondPlayerSets());
    }

    @Test
    void whenGamesAre64ThenCheckGame() {
        Score old = new Score(UUID.randomUUID());
        old.setFirstPlayerGames(6);
        old.setSecondPlayerGames(4);
        old.setTieBreak(false);
        old.setNeedGame(true);
        old.setFirstPlayerSets(1);
        old.setSecondPlayerSets(1);

        Score result = checkGameProcess.checkGame(old);

        assertFalse(result.isTieBreak());
        assertEquals(0, result.getFirstPlayerGames());
        assertEquals(0, result.getSecondPlayerGames());
        assertEquals(2, result.getFirstPlayerSets());  // +1
        assertEquals(1, result.getSecondPlayerSets());
    }
}
