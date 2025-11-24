package ru.tennis.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScoreResponse {
        private int firstPlayerSets;
        private int firstPlayerGames;
        private int firstPlayerPoints;
        private int secondPlayerSets;
        private int secondPlayerGames;
        private int secondPlayerPoints;
}
