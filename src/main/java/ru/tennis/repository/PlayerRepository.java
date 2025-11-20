package ru.tennis.repository;

import org.hibernate.exception.ConstraintViolationException;
import ru.tennis.exceptions.PlayerAlreadyExistsException;
import ru.tennis.exceptions.PlayerNameNotFoundException;
import ru.tennis.hibernate.CrudRepository;
import ru.tennis.model.Player;

import java.util.List;
import java.util.Map;

public class PlayerRepository {

    private final CrudRepository crudRepository;

    public PlayerRepository(CrudRepository crudRepository) {
        this.crudRepository = crudRepository;
    }

    public Player add(Player player) {
        crudRepository.run(session -> {
            try{
                session.persist(player);
                session.flush();
            } catch (ConstraintViolationException e) {
                throw new PlayerAlreadyExistsException("Игрок 2 с именем '" + player.getName() + "' уже существует");
            }
        });
        return player;
    }

    public Player findById(int id) throws NoSuchFieldException {
        return crudRepository.optional(
                "FROM Player p where p.id = :fId",
                Player.class,
                Map.of("fId", id)
                )
                .orElseThrow(() ->
                        new NoSuchFieldException("Игрока с id = " + id + " не найдено.")
                );
    }

    public Player findByName(String name) {
        return crudRepository.optional(
                        "FROM Player p where p.name = :fName",
                        Player.class,
                        Map.of("fName", name)
                )
                .orElseThrow(() ->
                        new PlayerNameNotFoundException("Игрок с именем '" + name + "' не найден.")
                );
    }

    public List<Player> findAll() {
        return crudRepository.query(
                "FROM Player p Select p",
                Player.class
        );
    }

    public List<Player> findAllPage(int firstResult, int maxResult) {
        return crudRepository.query(
                "FROM Player p Select p",
                Player.class,
                Map.of("firstResult", firstResult,
                        "maxResult", maxResult
                )
                );
    }

    public void delete(int id) {
        crudRepository.run(
                "delete Player p where p.id = :fId",
                Map.of("fId", id)
        );
    }

    public Long getTotal() {
        List<Long> query = crudRepository.query(
                "FROM Player p SELECT COUNT(p)", Long.class);
        return query.get(0);
    }
}
