package ru.tennis.service;

import ru.tennis.exceptions.PlayerAlreadyExistsException;
import ru.tennis.exceptions.PlayerNameNotFoundException;
import ru.tennis.mapper.MapperMapstruct;
import ru.tennis.model.Player;
import ru.tennis.model.dto.PlayerDtoRequest;
import ru.tennis.repository.PlayerRepository;

public class PlayerService {
    private final PlayerRepository playerRepository;
    private final MapperMapstruct mapper;

    public PlayerService(PlayerRepository playerRepository, MapperMapstruct mapper) {
        this.playerRepository = playerRepository;
        this.mapper = mapper;
    }

    public Player createOrGet(PlayerDtoRequest playerDto) {
        return createPlayerOrGet(playerDto);
    }

    private Player createPlayerOrGet(PlayerDtoRequest playerDto) {
        Player player = mapper.playerFromRequest(playerDto);
        try {
            playerRepository.add(player);
        } catch (PlayerAlreadyExistsException e) {
            try {
                player = playerRepository.findByName(playerDto.name());
            } catch (PlayerNameNotFoundException ex) {
                e.printStackTrace();
            }
        }
        return player;
    }

    public int findIdByName(String name) {
        return playerRepository.findByName(name).getId();
    }

    public Player findByName(String name) {
        return playerRepository.findByName(name);
    }
}
