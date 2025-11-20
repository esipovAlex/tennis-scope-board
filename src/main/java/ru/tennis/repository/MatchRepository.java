package ru.tennis.repository;

import ru.tennis.hibernate.CrudRepository;
import ru.tennis.model.Match;
import ru.tennis.model.Player;
import ru.tennis.model.ScoreShort;
import ru.tennis.model.response.MatchPlayerResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MatchRepository {
    private final CrudRepository crudRepository;

    public MatchRepository(CrudRepository crudRepository) {
        this.crudRepository = crudRepository;
    }

    public Match save(Match math) {
        crudRepository.run(session -> {
            session.persist(math);
            session.flush();
        });
        return math;
    }

    public Optional<MatchPlayerResponse> findPlayersNamesByMatshId(UUID id) {
        String jpql = """
                SELECT NEW ru.esipov.model.response.MatchPlayerResponse(m.id, m.firstPlayer.name, m.secondPlayer.name)
                FROM Match m WHERE m.id = :fId
                """;
        return crudRepository.optional(jpql, MatchPlayerResponse.class, Map.of("fId", id));
    }

    public void updateMatchScoreAndWinner(UUID matchId, Player newWinner, ScoreShort newScore) {
        String jpql = """
                UPDATE Match m
                SET m.score.firstPlayerSets = :p1s,
                    m.score.secondPlayerSets = :p2s,
                    m.score.score = :p3s,
                    m.winner.id = :winnerId
                WHERE m.id = :matchId
                """;
        Map<String, Object> params = Map.of(
                "p1s", newScore.getFirstPlayerSets(),
                "p2s", newScore.getSecondPlayerSets(),
                "p3s", newScore.getScore(),
                "winnerId", newWinner.getId(),
                "matchId", matchId
        );
        crudRepository.run(jpql, params);
    }

    public List<Match> findAllPage(int firstResult, int maxResult) {
        return crudRepository.query(
                "FROM Match m Select m",
                Match.class,
                Map.of("firstResult", firstResult,
                        "maxResult", maxResult
                )
        );
    }

    public Long getTotalCount() {
        List<Long> query = crudRepository.query(
                "FROM Match m SELECT COUNT(m.id)", Long.class);
        return query.get(0);
    }

    public Long getCountByPlayer(Player player) {
        List<Long> query = crudRepository.query(
                    " SELECT COUNT(m.id) FROM Match m WHERE m.firstPlayer = :fPlayer OR m.secondPlayer = :fPlayer",
                    Long.class,
                    Map.of("fPlayer", player)
                );
        return query.get(0);
    }

    public List<Match> findPageByPlayer(int firstResult, int maxResult, Player player) {
        return crudRepository.query(
                " Select m FROM Match m WHERE m.firstPlayer = :fPlayer OR m.secondPlayer = :fPlayer",
                Match.class,
                Map.of("firstResult", firstResult,
                        "maxResult", maxResult,
                        "fPlayer", player
                )
        );
    }
}
