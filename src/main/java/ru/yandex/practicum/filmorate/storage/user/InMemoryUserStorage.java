package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFound;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    private int lastId = 0;

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    private int getNextId() {
        return ++lastId;
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("User id={} created", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.debug("Fail to update user: no ID = {}", user.getId());
            throw new UserNotFound("User with id = %d not found!".formatted(user.getId()));
        }

        users.put(user.getId(), user);
        log.debug("User id={} updated", user.getId());
        return user;
    }

    @Override
    public Optional<User> getById(int userId) {
        return Optional.ofNullable(users.get(userId));
    }

}
