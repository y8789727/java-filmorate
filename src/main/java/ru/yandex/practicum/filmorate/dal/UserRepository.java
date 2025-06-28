package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {

    private static final String FIND_BY_LIKED_FILM_QUERY =
            "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
            "FROM Film_Likes fl JOIN Users u ON u.user_id = fl.user_id " +
            "WHERE fl.film_id = ? " +
            "ORDER BY u.user_id";

    private static final String FIND_ALL_QUERY =
            "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
            "FROM Users u " +
            "ORDER BY u.user_id";

    private static final String FIND_BY_ID_QUERY =
            "SELECT u.user_id, u.email, u.login, u.name, u.birthday " +
            "FROM Users u " +
            "WHERE u.user_id = ?";

    private static final String FIND_USER_FRIEND_IDS_QUERY =
            "SELECT fr.friend_id " +
            "FROM Friends fr " +
            "WHERE fr.user_id = ? " +
            "ORDER BY fr.friend_id";

//    private static final String INSERT_USER_QUERY =
//            "INSERT INTO users(email, login, name, birthday) " +
//            "VALUES (?, ?, ?, ?) " +
//            "RETURNING user_id";
    private static final String INSERT_USER_QUERY =
            "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_USER_QUERY =
            "UPDATE users " +
            "SET email = ?,  login = ?, name = ?, birthday = ? " +
            "WHERE user_id = ?";

    private static final String INSERT_FRIEND_QUERY =
            "INSERT INTO Friends(user_id, friend_id) " +
            "VALUES(?, ?)";

    private static final String DELETE_FRIEND_QUERY =
            "DELETE FROM Friends WHERE user_id = ? AND friend_id = ?";

    private static final String MUTUAL_FRIENDS_QUERY =
            "SELECT u.user_id, u.email, u.login, u.name, u.birthday\n" +
            "  FROM Users u\n" +
            "       JOIN Friends f1\n" +
            "         ON f1.USER_ID = ?\n" +
            "        AND f1.FRIEND_ID = u.USER_ID\n" +
//            "        AND f1.CONFIRMED\n" +
            "       JOIN Friends f2\n" +
            "         ON f2.USER_ID = ?\n" +
            "        AND f2.FRIEND_ID = u.USER_ID\n" +
//            "        AND f2.CONFIRMED\n" +
            "  WHERE u.user_id NOT IN (?, ?)\n" +
            "  ORDER BY u.user_id;";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findByLikedFilm(Film f) {
        return findMany(FIND_BY_LIKED_FILM_QUERY, f.getId());
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<User> findById(int userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public List<Integer> findUserFriendIds(User user) {
        return jdbc.query(FIND_USER_FRIEND_IDS_QUERY, (rs, rowNum) -> rs.getInt("friend_id"), user.getId());
    }

    public User save(User u) {
        int id = insertReturningId(INSERT_USER_QUERY,
                u.getEmail(),
                u.getLogin(),
                u.getName(),
                (u.getBirthday() != null ? Date.valueOf(u.getBirthday()): null));
                //Date.valueOf(u.getBirthday()));
        u.setId(id);
        return u;
    }

    public User update(User u) {
        update(UPDATE_USER_QUERY,
                u.getEmail(),
                u.getLogin(),
                u.getName(),
                (u.getBirthday() != null ? Date.valueOf(u.getBirthday()): null),
                u.getId());
        return u;
    }

    public void insertFriend(User user, User friend) {
        insert(INSERT_FRIEND_QUERY, user.getId(), friend.getId());
    }

    public void deleteFriend(User user, User friend) {
        delete(DELETE_FRIEND_QUERY, user.getId(), friend.getId());
    }

    public List<User> findMutualFriends(User u1, User u2) {
        return findMany(MUTUAL_FRIENDS_QUERY, u1.getId(), u2.getId(), u1.getId(), u2.getId());
    }
}
