package ru.tennis.servlet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tennis.model.Match;
import ru.tennis.model.Player;
import ru.tennis.model.ScoreShort;
import ru.tennis.model.dto.PageParam;
import ru.tennis.model.response.Page;
import ru.tennis.service.MatchService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchesServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletContext servletContext;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private MatchService matchService;

    @Mock
    private RequestDispatcher requestDispatcher;

    private MatchesServlet matchesServlet;

    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;

    @BeforeEach
    void setUp() throws ServletException {
        matchesServlet = new MatchesServlet();

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("matchService")).thenReturn(matchService);

        matchesServlet.init(servletConfig);

        player1 = createPlayer("John");
        player2 = createPlayer("Mike");
        player3 = createPlayer("Anna");
        player4 = createPlayer("David");
    }

    private Player createPlayer(String name) {
        Player player = new Player();
        player.setId(0);
        player.setName(name);
        return player;
    }

    private Match createMatch(Player firstPlayer, Player secondPlayer, Player winner) {
        Match match = new Match();
        match.setId(UUID.randomUUID());
        match.setFirstPlayer(firstPlayer);
        match.setSecondPlayer(secondPlayer);
        match.setWinner(winner);

        ScoreShort score = new ScoreShort(2, 1, "6-3, 4-6, 6-2");
        match.setScore(score);

        return match;
    }

    @Test
    void whenDoGetWithDefaultParameters() throws ServletException, IOException {
        when(request.getParameter("page")).thenReturn(null);
        when(request.getParameter("filter-by-player-name")).thenReturn(null);
        List<Match> mockMatches = Arrays.asList(
                createMatch(player1, player2, player1),
                createMatch(player3, player4, player3)
        );
        Page<Match> mockPage = new Page<>(mockMatches, 1, 5);
        when(matchService.getPage(isNull(), any(PageParam.class))).thenReturn(mockPage);
        when(request.getRequestDispatcher("matches.jsp")).thenReturn(requestDispatcher);
        matchesServlet.doGet(request, response);
        verify(matchService).getPage(null, new PageParam(1, 10));
        verify(request).setAttribute("matches", mockMatches);
        verify(request).setAttribute("currentPage", 1L);
        verify(request).setAttribute("totalPages", 5L);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void whenDoGetWithCustomPageAndFilter() throws ServletException, IOException {
        when(request.getParameter("page")).thenReturn("3");
        when(request.getParameter("filter-by-player-name")).thenReturn("John");
        List<Match> mockMatches = Arrays.asList(
                createMatch(player1, player2, player1),
                createMatch(player1, player4, player1)
        );
        Page<Match> mockPage = new Page<>(mockMatches, 3, 8);
        when(matchService.getPage(eq("John"), any(PageParam.class))).thenReturn(mockPage);
        when(request.getRequestDispatcher("matches.jsp")).thenReturn(requestDispatcher);
        matchesServlet.doGet(request, response);
        verify(matchService).getPage("John", new PageParam(3, 10));
        verify(request).setAttribute("matches", mockMatches);
        verify(request).setAttribute("currentPage", 3L);
        verify(request).setAttribute("totalPages", 8L);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void whenDoGetWithPageZero() throws ServletException, IOException {
        when(request.getParameter("page")).thenReturn("0");
        when(request.getParameter("filter-by-player-name")).thenReturn(null);
        List<Match> mockMatches = Arrays.asList(createMatch(player1, player2, player1));
        Page<Match> mockPage = new Page<>(mockMatches, 1, 2);
        when(matchService.getPage(isNull(), any(PageParam.class))).thenReturn(mockPage);
        when(request.getRequestDispatcher("matches.jsp")).thenReturn(requestDispatcher);
        matchesServlet.doGet(request, response);
        verify(matchService).getPage(null, new PageParam(1, 10));
        verify(request).setAttribute("currentPage", 1L);
    }

    @Test
    void whenDoGetWithEmptyPlayerName() throws ServletException, IOException {
        when(request.getParameter("page")).thenReturn(null);
        when(request.getParameter("filter-by-player-name")).thenReturn("");
        List<Match> mockMatches = Arrays.asList(
                createMatch(player1, player2, player1),
                createMatch(player3, player4, player4)
        );
        Page<Match> mockPage = new Page<>(mockMatches, 1, 4);
        when(matchService.getPage(eq(""), any(PageParam.class))).thenReturn(mockPage);
        when(request.getRequestDispatcher("matches.jsp")).thenReturn(requestDispatcher);
        matchesServlet.doGet(request, response);
        verify(matchService).getPage("", new PageParam(1, 10));
        verify(request).setAttribute("matches", mockMatches);
    }

    @Test
    void whenDoPostWithPlayerName() throws ServletException, IOException {
        String playerName = "John Doe";
        when(request.getParameter("filter-by-player-name")).thenReturn(playerName);

        matchesServlet.doPost(request, response);

        String expectedEncodedName = "John+Doe";
        verify(response).sendRedirect("matches?filter-by-player-name=" + expectedEncodedName);
    }

    @Test
    void whenDoPostWithEmptyPlayerName() throws ServletException, IOException {
        when(request.getParameter("filter-by-player-name")).thenReturn("");
        matchesServlet.doPost(request, response);
        verify(response).sendRedirect("matches?filter-by-player-name=");
    }

    @Test
    void whenDoPostWithSpecialCharacters() throws ServletException, IOException {
        String playerName = "John & Smith";
        when(request.getParameter("filter-by-player-name")).thenReturn(playerName);
        matchesServlet.doPost(request, response);
        String expectedEncodedName = "John+%26+Smith";
        verify(response).sendRedirect("matches?filter-by-player-name=" + expectedEncodedName);
    }

    @Test
    void whenDoPostWithUnicodeCharacters() throws ServletException, IOException {
        String playerName = "Jöhn Müller";
        when(request.getParameter("filter-by-player-name")).thenReturn(playerName);
        matchesServlet.doPost(request, response);
        String expectedEncodedName = "J%C3%B6hn+M%C3%BCller";
        verify(response).sendRedirect("matches?filter-by-player-name=" + expectedEncodedName);
    }

    @Test
    void testInit() throws ServletException {
        MatchesServlet newServlet = new MatchesServlet();
        ServletConfig localServletConfig = mock(ServletConfig.class);
        ServletContext localServletContext = mock(ServletContext.class);
        when(localServletConfig.getServletContext()).thenReturn(localServletContext);
        when(localServletContext.getAttribute("matchService")).thenReturn(matchService);
        newServlet.init(localServletConfig);
        verify(localServletContext).getAttribute("matchService");
    }

    @Test
    void whenDoGetWithInvalidPageParameter() throws ServletException, IOException {
        when(request.getParameter("page")).thenReturn("invalid");
        when(request.getParameter("filter-by-player-name")).thenReturn(null);
        try {
            matchesServlet.doGet(request, response);
            Assertions.fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            // Ожидаемое поведение - исключение было выброшено
        }
    }

    @Test
    void whenDoGetWithNullPageParameter() throws ServletException, IOException {
        when(request.getParameter("page")).thenReturn(null);
        when(request.getParameter("filter-by-player-name")).thenReturn("test");
        List<Match> mockMatches = Arrays.asList(createMatch(player1, player2, player1));
        Page<Match> mockPage = new Page<>(mockMatches, 1, 1);
        when(matchService.getPage(eq("test"), any(PageParam.class))).thenReturn(mockPage);
        when(request.getRequestDispatcher("matches.jsp")).thenReturn(requestDispatcher);
        matchesServlet.doGet(request, response);
        verify(matchService).getPage("test", new PageParam(1, 10));
        verify(request).setAttribute("currentPage", 1L);
    }
}
