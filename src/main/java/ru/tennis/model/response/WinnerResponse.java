package ru.tennis.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WinnerResponse {
    private String firstPlayerName;
    private String secondPlayerName;
    private String score;
    private String winnerName;
}
