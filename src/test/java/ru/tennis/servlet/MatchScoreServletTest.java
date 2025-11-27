package ru.tennis.servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tennis.model.response.MatchResponse;
import ru.tennis.model.response.ScoreResponse;
import ru.tennis.model.response.WinnerResponse;
import ru.tennis.service.MatchService;
import ru.tennis.service.ScoreService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.*;

class MatchScoreServletTest {

    private MatchScoreServlet servlet;

    @Mock
    private ScoreService scoreService;

    @Mock
    private MatchService matchService;

    @Mock
    private ServletContext context;
    @Mock
    private ServletConfig servletConfig;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    @BeforeEach
    void setUp() throws ServletException {
        MockitoAnnotations.openMocks(this);
        servlet = new MatchScoreServlet();
        when(servletConfig.getServletContext()).thenReturn(context);
        when(context.getAttribute("scoreService")).thenReturn(scoreService);
        when(context.getAttribute("matchService")).thenReturn(matchService);
        servlet.init(servletConfig);
    }

    @Test
    void whenDoGetThenForwardToMatchScoreJsp() throws ServletException, IOException {
        UUID uuid = UUID.randomUUID();
        MatchResponse matchResponse = new MatchResponse(uuid, "first", "second", new ScoreResponse(0, 0, 0, 0, 0, 0));
        when(request.getParameter("uuid")).thenReturn(uuid.toString());
        when(scoreService.addPoint(uuid, 0, 0)).thenReturn(matchResponse);
        when(request.getRequestDispatcher("match-score.jsp")).thenReturn(requestDispatcher);
        servlet.doGet(request, response);
        verify(request).setAttribute("uuid", uuid.toString());
        verify(request).setAttribute("match", matchResponse);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void whenDoPostThenForwardToWinnerJsp() throws ServletException, IOException {
        UUID uuid = UUID.randomUUID();
        WinnerResponse winnerResponse = new WinnerResponse("Player 1", "Player 2", "6-4", "Player 1");
        MatchResponse matchResponse = new MatchResponse(uuid, "first", "second", new ScoreResponse(0, 0, 0, 0, 0, 0));
        when(request.getParameter("uuid")).thenReturn(uuid.toString());
        when(request.getParameter("point-winner-id")).thenReturn("1");
        when(scoreService.addPoint(uuid, 1, 0)).thenReturn(matchResponse);
        when(matchService.winProcess(matchResponse)).thenReturn(winnerResponse);
        when(request.getRequestDispatcher("winner.jsp")).thenReturn(requestDispatcher);
        servlet.doPost(request, response);
        verify(scoreService).delete(uuid);
        verify(request).setAttribute("firstPlayerName", "Player 1");
        verify(request).setAttribute("secondPlayerName", "Player 2");
        verify(request).setAttribute("score", "6-4");
        verify(request).setAttribute("winnerName", "Player 1");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void whenNoWinnerThenForwardToMatchScoreJsp() throws ServletException, IOException {
        UUID uuid = UUID.randomUUID();
        MatchResponse matchResponse = new MatchResponse(uuid, "first", "second", new ScoreResponse(0, 0, 0, 0, 0, 0));
        when(request.getParameter("uuid")).thenReturn(uuid.toString());
        when(request.getParameter("point-winner-id")).thenReturn("2");
        when(scoreService.addPoint(uuid, 0, 1)).thenReturn(matchResponse);
        when(matchService.winProcess(matchResponse)).thenReturn(null);
        when(request.getRequestDispatcher("match-score.jsp")).thenReturn(requestDispatcher);
        servlet.doPost(request, response);
        verify(scoreService, never()).delete(uuid);
        verify(request).setAttribute("uuid", uuid.toString());
        verify(request).setAttribute("match", matchResponse);
        verify(requestDispatcher).forward(request, response);
    }
}
