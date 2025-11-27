package ru.tennis.servlet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndexServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    @InjectMocks
    private IndexServlet indexServlet;

    @Test
    void whenDoGetThenForwardToIndexJsp() throws ServletException, IOException {

        when(request.getRequestDispatcher("index.jsp")).thenReturn(requestDispatcher);

        indexServlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);

        verify(request).getRequestDispatcher("index.jsp");
    }
}