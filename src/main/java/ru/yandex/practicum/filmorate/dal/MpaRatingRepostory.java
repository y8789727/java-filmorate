package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final String FIND_ALL_FILM_ID_QUERY =
        "SELECT f.film_id, r.mpa_rating_id, r.rating " +
        "FROM Film f JOIN Mpa_Rating r ON r.mpa_rating_id = f.mpa_rating_id " +
        "ORDER BY f.film_id, r.mpa_rating_id";

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

    public Map<Integer, MpaRating> findAllIndexByFilmId() {
        final Map<Integer, MpaRating> ratingByFilmId = new HashMap<>();
        final RowMapper<MpaRating> mpaRatingRowMapper = new MpaRatingRowMapper();
        jdbc.query(FIND_ALL_FILM_ID_QUERY, (rs, rowNum) -> {
            ratingByFilmId.put(rs.getInt("film_id"), mpaRatingRowMapper.mapRow(rs, rowNum));
            return null;
        });

        return ratingByFilmId;
    }
}
