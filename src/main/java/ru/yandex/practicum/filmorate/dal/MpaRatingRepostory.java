package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRatingRepostory extends BaseRepository<MpaRating> {

    private static final String FIND_ALL_QUERY =
            "SELECT r.mpa_rating_id, r.rating " +
            "FROM Mpa_Rating r " +
            "ORDER BY r.mpa_rating_id";

    private static final String FIND_BY_ID_QUERY =
            "SELECT r.mpa_rating_id, r.rating " +
            "FROM Mpa_Rating r " +
            "WHERE r.mpa_rating_id = ?";

    private static final String FIND_BY_FILM_QUERY =
            "SELECT r.mpa_rating_id, r.rating " +
            "FROM Film f JOIN Mpa_Rating r ON r.mpa_rating_id = f.mpa_rating_id " +
            "WHERE f.film_id = ?";

    public MpaRatingRepostory(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper);
    }

    public List<MpaRating> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<MpaRating> findById(int ratingId) {
        return findOne(FIND_BY_ID_QUERY, ratingId);
    }

    public Optional<MpaRating> findByFilm(Film f) {
        return findOne(FIND_BY_FILM_QUERY, f.getId());
    }


}
