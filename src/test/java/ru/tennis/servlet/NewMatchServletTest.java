package ru.tennis.servlet;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tennis.service.MatchService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.*;

class NewMatchServletTest {

    private NewMatchServlet servlet;

    @Mock
    private MatchService matchService;

    @Mock
    private ServletContext servletContext;

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
        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletContext.getAttribute("matchService")).thenReturn(matchService);

        // Инициализируем сервлет и подменяем ServletContext
        servlet = new NewMatchServlet();
        servlet.init(servletConfig);
    }

    @Test
    void testDoGet() throws ServletException, IOException {
        // Настраиваем mock для RequestDispatcher
        when(request.getRequestDispatcher("new-match.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        // Проверяем, что был вызван forward на new-match.jsp
        verify(request).getRequestDispatcher("new-match.jsp");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoPost() throws ServletException, IOException {
        // Подготавливаем параметры запроса
        when(request.getParameter("first-player-name")).thenReturn("Alice");
        when(request.getParameter("second-player-name")).thenReturn("Bob");

        // Задаём UUID, который вернёт сервис
        UUID testUuid = UUID.randomUUID();
        when(matchService.createNewMatch("Alice", "Bob")).thenReturn(testUuid);

        servlet.doPost(request, response);

        // Проверяем вызов createNewMatch
        verify(matchService).createNewMatch("Alice", "Bob");

        // Проверяем redirect на match-score с UUID
        verify(response).sendRedirect("match-score?uuid=" + testUuid);
    }
}
