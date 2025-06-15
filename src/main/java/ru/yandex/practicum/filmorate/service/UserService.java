package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.TreeSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    @Setter
    @Autowired
    private UserStorage userStorage;

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        validateUser(user);

        return userStorage.create(user);
    }

    public User update(User user) {
        validateUser(user);

        return userStorage.update(user);
    }

    public void validateUser(User user) {
        if (user == null) {
            throw new ValidationException("User is not valid: empty data");
        }

        StringBuilder sb = new StringBuilder();

        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            sb.append("\nЭлектронная почта не может быть пустой и должна быть корректна");
        }

        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            sb.append("\nЛогин не может быть пустым и содержать пробелы");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            sb.append("\nДата рождения не может быть в будущем");
        }

        if (!sb.isEmpty()) {
            log.debug("User validation failed: {}", sb);
            throw new ValidationException("User is not valid: " + sb);
        }
    }

    public User getUserById(int userId) {
        final Optional<User> userOpt = userStorage.getById(userId);
        if (userOpt.isEmpty()) {
            throw new UserNotFound("User not found with id " + userId);
        }
        return userOpt.get();
    }

    public void addFriend(User user, User friend) {
        user.getFriendsId().add(friend.getId());
        friend.getFriendsId().add(user.getId());
    }

    public void removeFriend(User user, User friend) {
        user.getFriendsId().remove(friend.getId());
        friend.getFriendsId().remove(user.getId());
    }

    public Set<User> getMutualFriend(User user1, User user2) {
        Set<Integer> mutualFriendsIds = new HashSet<>(user1.getFriendsId());
        mutualFriendsIds.retainAll(user2.getFriendsId());
        mutualFriendsIds.remove(user2.getId());

        return mutualFriendsIds.stream()
                .map(this::getUserById)
                .collect(Collectors.toCollection(
                            () -> new TreeSet<>(Comparator.comparingInt(User::getId))
                ));
    }
}
