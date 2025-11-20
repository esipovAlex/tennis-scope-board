package ru.tennis.service;

import ru.tennis.exceptions.DuplicatePlayerNameException;
import ru.tennis.model.Match;
import ru.tennis.model.Player;
import ru.tennis.model.ScoreShort;
import ru.tennis.model.dto.PageParam;
import ru.tennis.model.dto.PlayerDtoRequest;
import ru.tennis.model.response.*;
import ru.tennis.repository.MatchRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class MatchService {

    private final MatchRepository matchRepository;
    private final PlayerService playerService;

    public MatchService(MatchRepository matchRepository, PlayerService playerService) {
        this.matchRepository = matchRepository;
        this.playerService = playerService;
    }

    public UUID createNewMatch(String firstPlayerName, String secondPlayerName) {
        if (Objects.equals(firstPlayerName, secondPlayerName)) {
            throw new DuplicatePlayerNameException("Игроки в матче должны иметь разные имена");
        }
        PlayerDtoRequest firstPlayerDtoReq = new PlayerDtoRequest(firstPlayerName);
        PlayerDtoRequest secondPlayerDtoReq = new PlayerDtoRequest(secondPlayerName);
        Player firstPlayer = playerService.createOrGet(firstPlayerDtoReq);
        Player secondPlayer = playerService.createOrGet(secondPlayerDtoReq);
        ScoreShort scoreShort = new ScoreShort();

        Match match = new Match();
        match.setScore(scoreShort);
        match.setFirstPlayer(firstPlayer);
        match.setSecondPlayer(secondPlayer);
        matchRepository.save(match);
        return match.getId();
    }

    public MatchPlayerResponse findById(UUID id) {
        Optional<MatchPlayerResponse> matchPlayerResponse =  matchRepository.findPlayersNamesByMatshId(id);
        return matchPlayerResponse.get();
    }

    private WinnerResponse checkWin(MatchResponse match) {
        WinnerResponse winner = null;
        ScoreResponse score = match.getScore();
        int firstPlayerSets = score.getFirstPlayerSets();
        int secondPlayerSets = score.getSecondPlayerSets();
        if (Math.max(firstPlayerSets, secondPlayerSets) > 1) {
            winner = new WinnerResponse();
            winner.setFirstPlayerName(match.getFirstPlayer());
            winner.setSecondPlayerName(match.getSecondPlayer());
            if (firstPlayerSets > secondPlayerSets) { // первый выиграл
                winner.setWinnerName(match.getFirstPlayer());
                winner.setScore(firstPlayerSets + " - " + secondPlayerSets);
            } else { //второй выиграл
                winner.setWinnerName(match.getSecondPlayer());
                winner.setScore(secondPlayerSets + " - " + firstPlayerSets);
            }
        }
        return winner;
    }

    public WinnerResponse winProcess(MatchResponse match) {
        WinnerResponse winner = checkWin(match);
        if (Objects.isNull(winner)) {
            return null;
        }
        UUID id = match.getId();
        int winnerId = playerService.findIdByName(winner.getWinnerName());
        ScoreResponse score = match.getScore();
        ScoreShort scoreShort = new ScoreShort(score.getFirstPlayerSets(), score.getSecondPlayerSets(), winner.getScore());
        matchRepository.updateMatchScoreAndWinner(id, new Player(winnerId, winner.getWinnerName()), scoreShort);

        return winner;
    }

    public Long getTotal() {
        return matchRepository.getTotalCount();
    }

    private Page<Match> getPageMatch(PageParam pageParam) {
        int pageNumber = pageParam.pageNumber();
        int size = pageParam.size();
        int firstResult = (pageNumber - 1) * size;
        long total = matchRepository.getTotalCount();
        long pageCount = (total / size) + 1;
        List<Match> page = matchRepository.findAllPage(firstResult, firstResult + size);
        return new Page<>(page, pageNumber, pageCount);
    }

    private Page<Match> getPageMatchByPlayerName(PageParam pageParam, String playerName) {
        int pageNumber = pageParam.pageNumber();
        int size = pageParam.size();
        int firstResult = (pageNumber - 1) * size;
        Player player = playerService.findByName(playerName);
        long total = matchRepository.getCountByPlayer(player);
        long pageCount = (total / size) + 1;
        List<Match> page = matchRepository.findPageByPlayer(firstResult, firstResult + size, player);
        return new Page<>(page, pageNumber, pageCount);
    }

    public Page<Match> getPage(String playerName, PageParam pageParam) {
        return Objects.isNull(playerName)
                ? getPageMatch(pageParam)
                : getPageMatchByPlayerName(pageParam, playerName);
    }
}
