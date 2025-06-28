package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    Collection<User> getAll();

    User create(User user);

    User update(User user);

    Optional<User> getById(int userId);

    void addFriend(User user, User friend);

    void removeFriend(User user, User friend);

    Set<User> getMutualFriend(User user1, User user2);
}
