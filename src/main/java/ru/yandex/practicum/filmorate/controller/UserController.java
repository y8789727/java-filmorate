package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validateUser(user);

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("User id={} created", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.debug("Fail to update user: no ID");
            throw new IllegalArgumentException("Updated user not found!");
        }

        validateUser(user);

        users.put(user.getId(), user);
        log.debug("User id={} updated", user.getId());
        return user;
    }

    private int getNextId() {
        int currentMaxId = users.keySet().stream()
                .mapToInt(i -> i)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateUser(User user) {
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
}

