package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    private static final String FIND_ALL_FILM_ID_QUERY =
            "SELECT fg.film_id, g.genre_id, g.name " +
            "FROM Film_Genre fg JOIN Genre g ON g.genre_id = fg.genre_id " +
            "ORDER BY fg.film_id, g.genre_id";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> findById(int genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public Set<Genre> findByFilm(Film f) {
        return new HashSet<>(findMany(FIND_BY_FILM_QUERY, f.getId()));
    }

    public Map<Integer, Set<Genre>> findAllIndexByFilmId() {
        final Map<Integer, Set<Genre>> genresByFilmId = new HashMap<>();
        final RowMapper<Genre> genreRowMapper = new GenreRowMapper();
        jdbc.query(FIND_ALL_FILM_ID_QUERY, (rs, rowNum) -> {
            int filmId = rs.getInt("film_id");
            Genre genre = genreRowMapper.mapRow(rs, rowNum);
            if (genre != null) {
                if (genresByFilmId.containsKey(filmId)) {
                    genresByFilmId.get(filmId).add(genre);
                } else {
                    genresByFilmId.put(filmId, new HashSet<>(List.of(genre)));
                }
            }
            return null;
        });
        return genresByFilmId;
    }

    public void saveFilmGenres(final Film f, Set<Genre> genres) {
        delete(DELETE_FILM_GENRES_QUERY, f.getId());

        if (genres != null && !genres.isEmpty()) {
            jdbc.batchUpdate(INSERT_FILM_GENRES_QUERY,
                    genres,
                    genres.size(),
                    (PreparedStatement ps, Genre genre) -> {
                        ps.setInt(1, f.getId());
                        ps.setInt(2, genre.getId());
                    });
        }
    }
}
