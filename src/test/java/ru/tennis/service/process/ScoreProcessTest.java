package ru.tennis.service.process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.tennis.model.dto.Score;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreProcessTest {

    @Mock
    private TieBreakProcess tieBreakProcess;

    @Mock
    private CheckGameProcess checkGameProcess;

    @Mock
    private RegularProcess regularProcess;

    @Mock
    private GreatLessProcess greatLessProcess;

    @InjectMocks
    private ScoreProcess scoreProcess;

    private Score initialScore;
    private Score intermediateScore;
    private Score finalScore;

    @BeforeEach
    void setUp() {
        initialScore = mock(Score.class);
        intermediateScore = mock(Score.class);
        finalScore = mock(Score.class);
    }

    @Test
    void whenScoreIsInTieBreakAddPointThenUseTieBreakProcess() {
        when(initialScore.isTieBreak()).thenReturn(true);
        when(initialScore.isDeuce()).thenReturn(false);
        when(tieBreakProcess.process(initialScore, 1, 0)).thenReturn(intermediateScore);
        when(checkGameProcess.checkGame(intermediateScore)).thenReturn(finalScore);
        Score result = scoreProcess.addPoint(initialScore, 1, 0);
        assertEquals(finalScore, result, "Метод должен вернуть финальный счет от checkGameProcess");
        verify(tieBreakProcess).process(initialScore, 1, 0);
        verify(checkGameProcess).checkGame(intermediateScore);
        verifyNoInteractions(regularProcess, greatLessProcess);
    }

    @Test
    void whenScoreIsInDeuceAddPointThenUseRegularProcess() {
        when(initialScore.isTieBreak()).thenReturn(false);
        when(initialScore.isDeuce()).thenReturn(true);
        when(regularProcess.process(initialScore, 0, 1)).thenReturn(intermediateScore);
        when(checkGameProcess.checkGame(intermediateScore)).thenReturn(finalScore);
        Score result = scoreProcess.addPoint(initialScore, 0, 1);
        assertEquals(finalScore, result);
        verify(regularProcess).process(initialScore, 0, 1);
        verify(checkGameProcess).checkGame(intermediateScore);
        verifyNoInteractions(tieBreakProcess, greatLessProcess);
    }

    @Test
    void whenScoreIsInGreatLessStateAddPointThenUseGreatLessProcess() {
        when(initialScore.isTieBreak()).thenReturn(false);
        when(initialScore.isDeuce()).thenReturn(false);
        when(greatLessProcess.process(initialScore, 1, 1)).thenReturn(intermediateScore);
        when(checkGameProcess.checkGame(intermediateScore)).thenReturn(finalScore);
        Score result = scoreProcess.addPoint(initialScore, 1, 1);
        assertEquals(finalScore, result);
        verify(greatLessProcess).process(initialScore, 1, 1);
        verify(checkGameProcess).checkGame(intermediateScore);
        verifyNoInteractions(tieBreakProcess, regularProcess);
    }
}