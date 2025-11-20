package ru.tennis.servlet;

import ru.tennis.model.response.MatchResponse;
import ru.tennis.model.response.WinnerResponse;
import ru.tennis.service.MatchService;
import ru.tennis.service.ScoreService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@WebServlet("/match-score")
public class MatchScoreServlet extends HttpServlet {

    private ScoreService scoreService;
    private MatchService matchService;

    @Override
    public void init() throws ServletException{
        super.init();
        ServletContext context = getServletContext();
        this.scoreService = (ScoreService) context.getAttribute("scoreService");
        this.matchService = (MatchService) context.getAttribute("matchService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID uuid = UUID.fromString(request.getParameter("uuid"));
        MatchResponse match = scoreService.addPoint(uuid, 0, 0);
        request.setAttribute("uuid", uuid.toString());
        request.setAttribute("match", match);
        System.out.println("match = " + match);
        request.getRequestDispatcher("match-score.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID uuid = UUID.fromString(request.getParameter("uuid"));
        int pointWinnerId = Integer.parseInt(request.getParameter("point-winner-id"));
        MatchResponse match = scoreService.addPoint(
                uuid,
                pointWinnerId == 1 ? 1 : 0,
                pointWinnerId == 2 ? 1 : 0
        );
        WinnerResponse winnerResponse = matchService.winProcess(match);
        if (Objects.nonNull(winnerResponse)) {
            scoreService.delete(uuid);
            request.setAttribute("firstPlayerName", winnerResponse.getFirstPlayerName());
            request.setAttribute("secondPlayerName", winnerResponse.getSecondPlayerName());
            request.setAttribute("score", winnerResponse.getScore());
            request.setAttribute("winnerName", winnerResponse.getWinnerName());
            request.getRequestDispatcher("winner.jsp").forward(request, response);
            return;
        }
        request.setAttribute("uuid", uuid.toString());
        request.setAttribute("match", match);
        request.getRequestDispatcher("match-score.jsp").forward(request, response);
    }
}
