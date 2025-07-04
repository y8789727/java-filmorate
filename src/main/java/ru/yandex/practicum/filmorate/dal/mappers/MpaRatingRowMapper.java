package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRatingRowMapper implements RowMapper<MpaRating>  {

    @Override
    public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        final MpaRating mpa = new MpaRating();
        mpa.setId(rs.getInt("mpa_rating_id"));
        mpa.setName(rs.getString("rating"));

        return mpa;
    }

}
