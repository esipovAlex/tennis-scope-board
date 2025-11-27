package ru.tennis.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WinnerResponse {
    private String firstPlayerName;
    private String secondPlayerName;
    private String score;
    private String winnerName;
}
