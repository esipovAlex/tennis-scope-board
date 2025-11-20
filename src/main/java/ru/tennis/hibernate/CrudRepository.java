package ru.tennis.hibernate;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import ru.tennis.exceptions.PlayerAlreadyExistsException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class CrudRepository {

    public void run(Consumer<Session> command) {
        tx(session -> {
                    command.accept(session);
                    return null;
                }
        );
    }

    public void run(String query, Map<String, Object> args) {
        Consumer<Session> command = session -> {
            MutationQuery mutationQuery = session
                    .createMutationQuery(query);
            for (Map.Entry<String, Object> arg : args.entrySet()) {
                mutationQuery.setParameter(arg.getKey(), arg.getValue());
            }
            mutationQuery.executeUpdate();
        };
        run(command);
    }

    public <T> List<T> query(String query, Class<T> cl) {
        Function<Session, List<T>> command = session -> session
                .createQuery(query, cl)
                .list();
        return tx(command);
    }

    public <T> Optional<T> optional(String query, Class<T> cl, Map<String, Object> args) {
        Function<Session, Optional<T>> command = session -> {
            var sq = session
                    .createQuery(query, cl);
            for (Map.Entry<String, Object> arg : args.entrySet()) {
                sq.setParameter(arg.getKey(), arg.getValue());
            }
            return sq.stream().findFirst();
        };
        return tx(command);
    }

    public <T> List<T> query(String query, Class<T> cl, Map<String, Object> args) {
        Function<Session, List<T>> command = session -> {
            Query<T> sq = session
                    .createQuery(query, cl);
            for (Map.Entry<String, Object> arg : args.entrySet()) {
                String parameter = arg.getKey();
                switch (parameter) {
                    case "firstResult" -> sq.setFirstResult((Integer) arg.getValue());
                    case "maxResult" -> sq.setMaxResults((Integer) arg.getValue());
                    default -> sq.setParameter(arg.getKey(), arg.getValue());
                }
            }
            return sq.list();
        };
        return tx(command);
    }

    private  <T> T tx(Function<Session, T> command) {
        Session session = HibernateConfig.getSessionFactory().openSession();
        try (session) {
            Transaction tx = session.beginTransaction();
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (Exception e) {
            if (e instanceof PlayerAlreadyExistsException) {
                throw e;
            }
            if (e instanceof NoSuchElementException) {
                throw e;
            }
            Transaction tx = session.getTransaction();
            e.printStackTrace();
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}
