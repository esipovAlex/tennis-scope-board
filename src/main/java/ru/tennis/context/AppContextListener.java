package ru.tennis.context;

import org.mapstruct.factory.Mappers;
import ru.tennis.hibernate.CrudRepository;
import ru.tennis.mapper.MapperMapstruct;
import ru.tennis.repository.MatchRepository;
import ru.tennis.repository.PlayerRepository;
import ru.tennis.service.ScoreService;
import ru.tennis.service.*;
import ru.tennis.service.process.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        MapperMapstruct mapper = Mappers.getMapper(MapperMapstruct.class);
        CrudRepository crudRepository = new CrudRepository();
        PlayerRepository playerRepository = new PlayerRepository(crudRepository);
        PlayerService playerService = new PlayerService(playerRepository, mapper);
        MatchRepository matchRepository = new MatchRepository(crudRepository);
        MatchService matchService = new MatchService(matchRepository, playerService);
        TieBreakProcess tieBreakProcess = new TieBreakProcess();
        CheckGameProcess checkGameProcess = new CheckGameProcess();
        RegularProcess regularProcess = new RegularProcess();
        GreatLessProcess greatLessProcess = new GreatLessProcess();
        ScoreProcess scoreProcess = new ScoreProcess(tieBreakProcess,checkGameProcess, regularProcess, greatLessProcess);
        ScoreService scoreService = new ScoreService(scoreProcess, matchService, mapper);
        ServletContext context = sce.getServletContext();
        context.setAttribute("playerService", playerService);
        context.setAttribute("matchService", matchService);
        context.setAttribute("scoreService", scoreService);
    }
}
