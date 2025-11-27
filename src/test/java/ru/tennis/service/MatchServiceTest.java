package ru.tennis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tennis.exceptions.DuplicatePlayerNameException;
import ru.tennis.model.Match;
import ru.tennis.model.Player;
import ru.tennis.model.ScoreShort;
import ru.tennis.model.dto.PageParam;
import ru.tennis.model.dto.PlayerDtoRequest;
import ru.tennis.model.response.*;
import ru.tennis.repository.MatchRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private MatchService matchService;

    private Player firstPlayer;
    private Player secondPlayer;
    private Match match;
    private UUID matchId;
    private MatchResponse matchResponse;
    private ScoreResponse scoreResponse;

    @BeforeEach
    void setUp() {
        firstPlayer = new Player(1, "Player One");
        secondPlayer = new Player(2, "Player Two");
        matchId = UUID.randomUUID();
        match = new Match(matchId, firstPlayer, secondPlayer, new ScoreShort(), null);
        matchResponse = mock(MatchResponse.class);
        scoreResponse = mock(ScoreResponse.class);
    }

    @Test
    void whenPlayerNamesAreDifferentCreateNewMatchThenCreateMatchAndReturnUuid() {
        String firstPlayerName = "Player One";
        String secondPlayerName = "Player Two";
        when(playerService.createOrGet(any(PlayerDtoRequest.class)))
                .thenReturn(firstPlayer)
                .thenReturn(secondPlayer);
        when(matchRepository.save(any(Match.class))).thenReturn(match);

        UUID resultId = matchService.createNewMatch(firstPlayerName, secondPlayerName);

        assertNull(resultId);

        verify(playerService).createOrGet(new PlayerDtoRequest(firstPlayerName));
        verify(playerService).createOrGet(new PlayerDtoRequest(secondPlayerName));

        ArgumentCaptor<Match> matchCaptor = ArgumentCaptor.forClass(Match.class);
        verify(matchRepository).save(matchCaptor.capture());

        Match savedMatch = matchCaptor.getValue();
        assertEquals(firstPlayer, savedMatch.getFirstPlayer());
        assertEquals(secondPlayer, savedMatch.getSecondPlayer());
    }

    @Test
    void whenPlayerNamesAreSameCreateNewMatchThenThrowException() {
        String playerName = "Same Name";
        assertThrows(DuplicatePlayerNameException.class, () -> {
            matchService.createNewMatch(playerName, playerName);
        });
        verifyNoInteractions(playerService, matchRepository);
    }

    @Test
    void whenMatchFoundFindByIdThenReturnResponse() {
        MatchPlayerResponse expectedResponse = new MatchPlayerResponse(matchId, "Player One", "Player Two");
        when(matchRepository.findPlayersNamesByMatshId(matchId)).thenReturn(Optional.of(expectedResponse));
        MatchPlayerResponse result = matchService.findById(matchId);
        assertEquals(expectedResponse, result);
        verify(matchRepository).findPlayersNamesByMatshId(matchId);
    }

    @Test
    void whenMatchNotFoundFindByIdThenThrowException() {
        when(matchRepository.findPlayersNamesByMatshId(matchId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            matchService.findById(matchId);
        });
        verify(matchRepository).findPlayersNamesByMatshId(matchId);
    }

    @Test
    void whenWinnerExistsWinProcessThenUpdateMatchAndReturnWinner() {

        when(matchResponse.getId()).thenReturn(matchId);
        when(matchResponse.getScore()).thenReturn(scoreResponse);
        when(scoreResponse.getFirstPlayerSets()).thenReturn(2);
        when(scoreResponse.getSecondPlayerSets()).thenReturn(0);
        when(matchResponse.getFirstPlayer()).thenReturn(firstPlayer.getName());
        when(matchResponse.getSecondPlayer()).thenReturn(secondPlayer.getName());

        when(playerService.findIdByName(firstPlayer.getName())).thenReturn(firstPlayer.getId());

        WinnerResponse result = matchService.winProcess(matchResponse);

        assertNotNull(result);
        assertEquals(firstPlayer.getName(), result.getWinnerName());
        assertEquals("2 - 0", result.getScore());

        ArgumentCaptor<Player> winnerCaptor = ArgumentCaptor.forClass(Player.class);
        ArgumentCaptor<ScoreShort> scoreCaptor = ArgumentCaptor.forClass(ScoreShort.class);

        verify(matchRepository).updateMatchScoreAndWinner(eq(matchId), winnerCaptor.capture(), scoreCaptor.capture());

        assertEquals(firstPlayer, winnerCaptor.getValue());
        assertEquals(new ScoreShort(2, 0, "2 - 0"), scoreCaptor.getValue()); // Предполагаемый счет в гейме
    }

    @Test
    void whenNoWinnerYetWinProcessThenReturnNull() {
        when(matchResponse.getScore()).thenReturn(scoreResponse);
        when(scoreResponse.getFirstPlayerSets()).thenReturn(1);
        when(scoreResponse.getSecondPlayerSets()).thenReturn(1);

        WinnerResponse result = matchService.winProcess(matchResponse);

        assertNull(result);

        verify(matchRepository, never()).updateMatchScoreAndWinner(any(), any(), any());
        verifyNoInteractions(playerService);
    }

    @Test
    void whenGetTotalThenReturnTotalCount() {
        long expectedTotal = 100L;
        when(matchRepository.getTotalCount()).thenReturn(expectedTotal);

        long result = matchService.getTotal();

        assertEquals(expectedTotal, result);
        verify(matchRepository).getTotalCount();
    }

    @Test
    void whenPlayerNameIsNullGetPageThenCallGetPageMatch() {
        PageParam pageParam = new PageParam(1, 10);
        Page<Match> expectedPage = new Page<>(List.of(match), 1, 1);
        when(matchRepository.getTotalCount()).thenReturn(1L);
        when(matchRepository.findAllPage(anyInt(), anyInt())).thenReturn(List.of(match));

        Page<Match> result = matchService.getPage(null, pageParam);

        assertEquals(expectedPage.getPage(), result.getPage());
        assertEquals(expectedPage.getTotalPages(), result.getTotalPages());

        verify(matchRepository).findAllPage(0, 10); // (pageNumber - 1) * size
        verify(matchRepository, never()).findPageByPlayer(anyInt(), anyInt(), any());
    }

    @Test
    void whenPlayerNameIsProvidedGetPageThenCallGetPageMatchByPlayerName() {
        String playerName = "Player One";
        PageParam pageParam = new PageParam(1, 10);
        Page<Match> expectedPage = new Page<>(List.of(match), 1, 1);
        when(playerService.findByName(playerName)).thenReturn(firstPlayer);
        when(matchRepository.getCountByPlayer(firstPlayer)).thenReturn(1L);
        when(matchRepository.findPageByPlayer(anyInt(), anyInt(), eq(firstPlayer))).thenReturn(List.of(match));
        Page<Match> result = matchService.getPage(playerName, pageParam);

        assertEquals(expectedPage.getPage(), result.getPage());
        assertEquals(expectedPage.getTotalPages(), result.getTotalPages());

        verify(playerService).findByName(playerName);
        verify(matchRepository).findPageByPlayer(0, 10, firstPlayer);
        verify(matchRepository, never()).findAllPage(anyInt(), anyInt());
    }
}