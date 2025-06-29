package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_QUERY =
            "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, f.mpa_rating_id " +
            "FROM film f " +
            "ORDER BY f.film_id";

    private static final String FIND_BY_ID_QUERY =
            "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, f.mpa_rating_id " +
            "FROM film f " +
            "WHERE f.film_id = ? " +
            "ORDER BY f.film_id";

    private static final String INSERT_FILM_QUERY =
            "INSERT INTO film(name, description, releaseDate, duration, mpa_rating_id) " +
            "VALUES(?, ?, ?, ?, ?)";

    private static final String INSERT_LIKE_QUERY =
            "INSERT INTO Film_Likes(film_id, user_id) " +
            "VALUES(?, ?)";

    private static final String DELETE_LIKE_QUERY =
            "DELETE FROM Film_Likes WHERE film_id = ? AND user_id = ?";

    private static final String TOP_FILMS_QUERY =
            "SELECT f.film_id, f.name, f.description, f.releaseDate, f.duration, f.mpa_rating_id " +
            "  FROM Film f " +
            "       LEFT JOIN Film_Likes fl " +
            "              ON fl.FILM_ID = f.FILM_ID " +
            " GROUP BY f.film_id, f.name, f.description, f.releaseDate, f.duration, f.mpa_rating_id " +
            " ORDER BY count(*) DESC, f.film_id " +
            "LIMIT ?";

    private static final String UPDATE_FILM_QUERY =
            "UPDATE film " +
            "SET name = ?, description = ?, releaseDate = ?, duration = ?, mpa_rating_id = ? " +
            "WHERE film_id = ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> findById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Film save(Film film) {
        int id = insertReturningId(INSERT_FILM_QUERY,
                        film.getName(),
                        film.getDescription(),
                        film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null,
                        film.getDuration(),
                        film.getMpaRating() != null ? film.getMpaRating().getId() : null);
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        update(UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null,
                film.getDuration(),
                film.getMpaRating().getId(),
                film.getId());
        return film;
    }

    public void insertLike(Film film, int userId) {
        insert(INSERT_LIKE_QUERY, film.getId(), userId);
    }

    public void deleteLike(Film film, int userId) {
        delete(DELETE_LIKE_QUERY, film.getId(), userId);
    }

    public List<Film> findTop(int topN) {
        return findMany(TOP_FILMS_QUERY, topN);
    }
}
