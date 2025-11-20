package ru.tennis.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Getter
@Setter
public class ScoreShort {
    private int firstPlayerSets;
    private int secondPlayerSets;
    private String score;
}
