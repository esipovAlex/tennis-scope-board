package ru.tennis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tennis.mapper.MapperMapstruct;
import ru.tennis.model.dto.Score;
import ru.tennis.model.response.MatchPlayerResponse;
import ru.tennis.model.response.MatchResponse;
import ru.tennis.model.response.ScoreResponse;
import ru.tennis.service.process.ScoreProcess;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreServiceTest {

    @Mock
    private ScoreProcess scoreProcess;

    @Mock
    private MatchService matchService;

    @Mock
    private MapperMapstruct mapper;

    @InjectMocks
    private ScoreService scoreService;

    private UUID matchId;
    private Score updatedScore;
    private ScoreResponse scoreResponse;
    private MatchPlayerResponse matchPlayerResponse;

    @BeforeEach
    void setUp() {
        matchId = UUID.randomUUID();
        updatedScore = new Score(matchId);
        scoreResponse = mock(ScoreResponse.class);
        matchPlayerResponse = mock(MatchPlayerResponse.class);
    }

    @Test
    void whenAddPointShouldCreateNewScoreThenFirstCallForId() {
        when(mapper.responseFromScore(any(Score.class))).thenReturn(scoreResponse);
        when(matchService.findById(matchId)).thenReturn(matchPlayerResponse);

        MatchResponse result = scoreService.addPoint(matchId, 1, 0);

        assertNotNull(result);
        verify(mapper).responseFromScore(any(Score.class));
        verify(matchService).findById(matchId);
        verify(scoreProcess, never()).addPoint(any(), anyInt(), anyInt());
    }

    @Test
    void whenAddPointShouldUpdateScoreThenSubsequentCallForId() {
        when(mapper.responseFromScore(any(Score.class))).thenReturn(scoreResponse);
        when(matchService.findById(matchId)).thenReturn(matchPlayerResponse);
        scoreService.addPoint(matchId, 1, 0);
        reset(scoreProcess, matchService, mapper);
        when(scoreProcess.addPoint(any(Score.class), eq(0), eq(1))).thenReturn(updatedScore);
        when(mapper.responseFromScore(updatedScore)).thenReturn(scoreResponse);
        when(matchService.findById(matchId)).thenReturn(matchPlayerResponse);

        MatchResponse result = scoreService.addPoint(matchId, 0, 1);

        assertNotNull(result);
        verify(scoreProcess, times(1)).addPoint(any(Score.class), eq(0), eq(1));
        verify(mapper).responseFromScore(updatedScore);
        verify(matchService).findById(matchId);
    }

    @Test
    void whenDeleteShouldRemoveScoreFromStore() {
        when(mapper.responseFromScore(any(Score.class))).thenReturn(scoreResponse);
        when(matchService.findById(matchId)).thenReturn(matchPlayerResponse);
        scoreService.addPoint(matchId, 1, 0);
        reset(scoreProcess, matchService, mapper);
        when(mapper.responseFromScore(any(Score.class))).thenReturn(scoreResponse);
        when(matchService.findById(matchId)).thenReturn(matchPlayerResponse);
        scoreService.delete(matchId);
        scoreService.addPoint(matchId, 1, 0);
        verify(scoreProcess, never()).addPoint(any(), anyInt(), anyInt());
    }
}