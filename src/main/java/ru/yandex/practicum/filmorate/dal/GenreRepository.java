package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String FIND_ALL_QUERY =
            "SELECT g.genre_id, g.name " +
            "FROM genre g " +
            "ORDER BY g.genre_id";

    private static final String FIND_BY_FILM_QUERY =
            "SELECT g.genre_id, g.name " +
            "FROM Film_Genre fg JOIN Genre g ON g.genre_id = fg.genre_id " +
            "WHERE fg.film_id = ? " +
            "ORDER BY g.genre_id";

    private static final String FIND_BY_ID_QUERY =
            "SELECT g.genre_id, g.name " +
            "FROM genre g " +
            "WHERE g.genre_id = ?";

    private static final String INSERT_FILM_GENRES_QUERY =
            "INSERT INTO Film_Genre(film_id, genre_id) " +
            "VALUES(?, ?)";

    private static final String DELETE_FILM_GENRES_QUERY =
            "DELETE FROM Film_Genre WHERE film_id = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> findById(int genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public List<Genre> findByFilm(Film f) {
        return findMany(FIND_BY_FILM_QUERY, f.getId());
    }

    public void saveFilmGenres(Film f, Set<Genre> genres) {
        delete(DELETE_FILM_GENRES_QUERY, f.getId());
        genres.forEach(g -> insert(INSERT_FILM_GENRES_QUERY, f.getId(), g.getId()));
    }
}
