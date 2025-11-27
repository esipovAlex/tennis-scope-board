package ru.tennis.service;

import ru.tennis.mapper.MapperMapstruct;
import ru.tennis.model.dto.Score;
import ru.tennis.model.response.MatchPlayerResponse;
import ru.tennis.model.response.MatchResponse;
import ru.tennis.model.response.ScoreResponse;
import ru.tennis.service.process.ScoreProcess;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreService {

    private final Map<UUID, Score> store = new ConcurrentHashMap<>();
    private final ScoreProcess scoreProcess;
    private final MatchService matchService;
    private final MapperMapstruct mapper;

    public ScoreService(ScoreProcess scoreProcess, MatchService matchService, MapperMapstruct mapper) {
        this.scoreProcess = scoreProcess;
        this.matchService = matchService;
        this.mapper = mapper;
    }

    private Score createOrProcess(UUID id, int pointFirstPlayer, int pointSecondPlayer) {
        return store.compute(id, (key, currentValue) -> {
            if (Objects.isNull(currentValue)) {
                return new Score(key);
            } else {
                return scoreProcess.addPoint(currentValue, pointFirstPlayer, pointSecondPlayer);
            }
        });
    }

    public MatchResponse addPoint(UUID id, int pointFirstPlayer, int pointSecondPlayer) {
        Score score = createOrProcess(id, pointFirstPlayer, pointSecondPlayer);
        ScoreResponse scoreResponse = mapper.responseFromScore(score);
        MatchPlayerResponse matchPlayerResponse = matchService.findById(id);
        return new MatchResponse(
                id,
                matchPlayerResponse.firstPlayer(),
                matchPlayerResponse.secondPlayer(),
                scoreResponse);
    }

    public void delete(UUID id) {
        store.remove(id);
    }
}
