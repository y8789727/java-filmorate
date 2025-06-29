package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
            """
                    SELECT u.user_id, u.email, u.login, u.name, u.birthday
                      FROM Users u
                           JOIN Friends f1
                             ON f1.USER_ID = ?
                            AND f1.FRIEND_ID = u.USER_ID
                           JOIN Friends f2
                             ON f2.USER_ID = ?
                            AND f2.FRIEND_ID = u.USER_ID
                      WHERE u.user_id NOT IN (?, ?)
                      ORDER BY u.user_id;""";

    private static final String FIND_ALL_LIKES_FILM_ID_QUERY =
            "SELECT fl.film_id, u.user_id " +
            "FROM Film_Likes fl JOIN Users u ON u.user_id = fl.user_id " +
            "ORDER BY fl.film_id, u.user_id";

    private static final String FIND_FRIENDS_USER_ID_QUERY =
            "SELECT fr.user_id, fr.friend_id " +
            "FROM Friends fr " +
            "ORDER BY fr.user_id, fr.friend_id";

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
                (u.getBirthday() != null ? Date.valueOf(u.getBirthday()) : null));
        u.setId(id);
        return u;
    }

    public User update(User u) {
        update(UPDATE_USER_QUERY,
                u.getEmail(),
                u.getLogin(),
                u.getName(),
                (u.getBirthday() != null ? Date.valueOf(u.getBirthday()) : null),
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

    public Map<Integer, Set<Integer>> findAllLikesIndexByFilmId() {
        final Map<Integer, Set<Integer>> likesByFilmId = new HashMap<>();

        jdbc.query(FIND_ALL_LIKES_FILM_ID_QUERY, (rs, rowNum) -> {
            int filmId = rs.getInt("film_id");
            int likedBy = rs.getInt("user_id");
            if (likesByFilmId.containsKey(filmId)) {
                likesByFilmId.get(filmId).add(likedBy);
            } else {
                likesByFilmId.put(filmId, new HashSet<>(List.of(likedBy)));
            }
            return null;
        });
        return likesByFilmId;
    }

    public Map<Integer, Set<Integer>> findAllFriendsIndexByUserId() {
        final Map<Integer, Set<Integer>> friendsByUserId = new HashMap<>();

        jdbc.query(FIND_FRIENDS_USER_ID_QUERY, (rs, rowNum) -> {
            int userId = rs.getInt("user_id");
            int friendId = rs.getInt("friend_id");
            if (friendsByUserId.containsKey(userId)) {
                friendsByUserId.get(userId).add(friendId);
            } else {
                friendsByUserId.put(userId, new HashSet<>(List.of(friendId)));
            }
            return null;
        });

        return friendsByUserId;
    }
}
