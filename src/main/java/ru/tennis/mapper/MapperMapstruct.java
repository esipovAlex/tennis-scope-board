package ru.tennis.mapper;

import org.mapstruct.Mapper;
import ru.tennis.model.Player;
import ru.tennis.model.dto.Score;
import ru.tennis.model.dto.PlayerDtoRequest;
import ru.tennis.model.response.PlayerDtoResponse;
import ru.tennis.model.response.ScoreResponse;

@Mapper
public interface MapperMapstruct {

    Player playerFromRequest(PlayerDtoRequest reguest);

    ScoreResponse responseFromScore(Score score);
}
