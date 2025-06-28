package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFound;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

    @Override
    public void addFriend(User user, User friend) {
        user.getFriendsId().add(friend.getId());
    }

    @Override
    public void removeFriend(User user, User friend) {
        user.getFriendsId().remove(friend.getId());
    }

    @Override
    public Set<User> getMutualFriend(User user1, User user2) {
        Set<Integer> mutualFriendsIds = new HashSet<>(user1.getFriendsId());
        mutualFriendsIds.retainAll(user2.getFriendsId());
        mutualFriendsIds.remove(user2.getId());

        return mutualFriendsIds.stream()
                .map(i -> getById(i).get())
                .collect(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparingInt(User::getId))
                ));
    }
}
