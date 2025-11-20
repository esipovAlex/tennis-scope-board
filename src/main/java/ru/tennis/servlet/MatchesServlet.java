package ru.tennis.servlet;

import ru.tennis.model.Match;
import ru.tennis.model.dto.PageParam;
import ru.tennis.model.response.Page;
import ru.tennis.service.MatchService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/matches")
public class MatchesServlet extends HttpServlet {
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private MatchService matchService;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();
        this.matchService = (MatchService) context.getAttribute("matchService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String page = req.getParameter("page");
        String playerName = req.getParameter("filter-by-player-name");
        int pageNumber = (page == null || "0".equals(page)) ? DEFAULT_PAGE_NUMBER : Integer.parseInt(page);
        PageParam pageParam = new PageParam(pageNumber, DEFAULT_PAGE_SIZE);
        Page<Match> pageMatch = matchService.getPage(playerName, pageParam);
        req.setAttribute("matches",     pageMatch.getResults());
        req.setAttribute("currentPage", pageMatch.getPage());
        req.setAttribute("totalPages",  pageMatch.getTotalPages());
        req.getRequestDispatcher("matches.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String playerName = request.getParameter("filter-by-player-name");
        String encodedPlayerName = URLEncoder.encode(playerName, StandardCharsets.UTF_8);
        response.sendRedirect("matches?filter-by-player-name=" + encodedPlayerName);
    }
}
