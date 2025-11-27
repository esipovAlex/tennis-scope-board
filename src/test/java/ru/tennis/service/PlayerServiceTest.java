package ru.tennis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tennis.exceptions.PlayerAlreadyExistsException;
import ru.tennis.exceptions.PlayerNameNotFoundException;
import ru.tennis.mapper.MapperMapstruct;
import ru.tennis.model.Player;
import ru.tennis.model.dto.PlayerDtoRequest;
import ru.tennis.repository.PlayerRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private MapperMapstruct mapper;

    @InjectMocks
    private PlayerService playerService;

    private PlayerDtoRequest playerDtoRequest;
    private Player newPlayer;
    private Player existingPlayer;

    @BeforeEach
    void setUp() {
        playerDtoRequest = new PlayerDtoRequest("Roger Federer");
        newPlayer = new Player(null, "Roger Federer");
        existingPlayer = new Player(1, "Roger Federer");
    }

    @Test
    void whenPlayerDoesNotExistCreateOrGetThenCreateAndReturnNewPlayer() {
        when(mapper.playerFromRequest(playerDtoRequest)).thenReturn(newPlayer);
        when(playerRepository.add(newPlayer)).thenReturn(newPlayer);
        Player result = playerService.createOrGet(playerDtoRequest);
        assertEquals(newPlayer, result);
        verify(mapper, times(1)).playerFromRequest(playerDtoRequest);
        verify(playerRepository, times(1)).add(newPlayer);
        verify(playerRepository, never()).findByName(anyString());
    }

    @Test
    void whenPlayerAlreadyExistsCreateOrGetThenReturnExistingPlayer() {
        when(mapper.playerFromRequest(playerDtoRequest)).thenReturn(newPlayer);
        doThrow(new PlayerAlreadyExistsException()).when(playerRepository).add(newPlayer);
        when(playerRepository.findByName(playerDtoRequest.name())).thenReturn(existingPlayer);
        Player result = playerService.createOrGet(playerDtoRequest);
        assertEquals(existingPlayer, result);
        verify(mapper).playerFromRequest(playerDtoRequest);
        verify(playerRepository).add(newPlayer);
        verify(playerRepository).findByName(playerDtoRequest.name());
    }

    @Test
    void whenAddFailsAndFindAlsoFailsCreateOrGetThenReturnMappedPlayer() {
        when(mapper.playerFromRequest(playerDtoRequest)).thenReturn(newPlayer);
        doThrow(new PlayerAlreadyExistsException()).when(playerRepository).add(newPlayer);
        when(playerRepository.findByName(playerDtoRequest.name())).thenThrow(new PlayerNameNotFoundException());
        Player result = playerService.createOrGet(playerDtoRequest);
        assertEquals(newPlayer, result);
        verify(mapper).playerFromRequest(playerDtoRequest);
        verify(playerRepository).add(newPlayer);
        verify(playerRepository).findByName(playerDtoRequest.name());
    }

    @Test
    void whenPlayerFoundFindIdByNameThenReturnId() {
        when(playerRepository.findByName("Roger Federer")).thenReturn(existingPlayer);
        int resultId = playerService.findIdByName("Roger Federer");
        assertEquals(existingPlayer.getId(), resultId);
        verify(playerRepository).findByName("Roger Federer");
    }

    @Test
    void whenPlayerNotFoundFindIdByNameThenThrowException() {
        String playerName = "Rafael Nadal";
        when(playerRepository.findByName(playerName)).thenThrow(new PlayerNameNotFoundException());
        assertThrows(PlayerNameNotFoundException.class, () -> {
            playerService.findIdByName(playerName);
        });
        verify(playerRepository).findByName(playerName);
    }

    @Test
    @DisplayName("findByName: должен вернуть игрока, когда он найден")
    void whenPlayerFoundFindByNameThenReturnPlayer() {
        when(playerRepository.findByName("Roger Federer")).thenReturn(existingPlayer);
        Player result = playerService.findByName("Roger Federer");
        assertEquals(existingPlayer, result);
        verify(playerRepository).findByName("Roger Federer");
    }

    @Test
    @DisplayName("findByName: должен выбросить исключение, когда игрок не найден")
    void whenPlayerNotFoundFindByNameThenThrowException() {
        String playerName = "Novak Djokovic";
        when(playerRepository.findByName(playerName)).thenThrow(new PlayerNameNotFoundException());
        assertThrows(PlayerNameNotFoundException.class, () -> {
            playerService.findByName(playerName);
        });
        verify(playerRepository).findByName(playerName);
    }
}