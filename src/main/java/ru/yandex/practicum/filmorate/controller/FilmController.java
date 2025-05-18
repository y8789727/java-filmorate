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
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validateFilm(film);

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("Film id={} created", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.debug("Fail to update film: no input ID");
            throw new IllegalArgumentException("Updated film not found!");
        }

        validateFilm(film);

        films.put(film.getId(), film);
        log.debug("Film id={} updated", film.getId());
        return film;
    }

    private int getNextId() {
        int currentMaxId = films.keySet().stream()
                .mapToInt(i -> i)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Film is not valid: empty data");
        }

        StringBuilder sb = new StringBuilder();

        if (film.getName() == null || film.getName().isEmpty()) {
            sb.append("\nНазвание не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            sb.append("\nМаксимальная длина описания — 200 символов");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            sb.append("\nДата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() < 0) {
            sb.append("\nПродолжительность фильма не может быть отрицательной");
        }

        if (!sb.isEmpty()) {
            log.debug("Film validation failed: {}", sb);
            throw new ValidationException("Film is not valid: " + sb);
        }
    }
}
