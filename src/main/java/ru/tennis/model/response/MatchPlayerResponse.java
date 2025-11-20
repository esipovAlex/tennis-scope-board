package ru.tennis.model.response;

import java.util.UUID;

public record MatchPlayerResponse(UUID id, String firstPlayer, String secondPlayer) {
}
