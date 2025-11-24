package ru.tennis.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Score {

    private UUID id;
    private int firstPlayerSets;
    private int firstPlayerGames;
    private int firstPlayerPoints;
    private int secondPlayerSets;
    private int secondPlayerGames;
    private int secondPlayerPoints;
    private boolean deuce = true;
    private boolean tieBreak;
    private boolean needGame = false;

    public Score(Score old) {
        this.id = old.getId();
        this.firstPlayerSets = old.getFirstPlayerSets();
        this.firstPlayerGames = old.getFirstPlayerGames();
        this.firstPlayerPoints = old.getFirstPlayerPoints();
        this.secondPlayerSets = old.getSecondPlayerSets();
        this.secondPlayerGames = old.getSecondPlayerGames();
        this.secondPlayerPoints = old.getSecondPlayerPoints();
        this.deuce = old.isDeuce();
        this.tieBreak = old.isTieBreak();
        this.needGame = old.isNeedGame();
    }

    public Score(UUID id) {
        this.id = id;
    }
}
