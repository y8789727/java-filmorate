package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    public static final LocalDate EARLIEST_FILM_DATE = LocalDate.of(1895, 12, 28);

    @Setter
    @Autowired
    private FilmStorage filmStorage;

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        validateFilm(film);

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateFilm(film);

        return filmStorage.update(film);
    }

    private void validateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Film is not valid: empty data");
        }

        StringBuilder sb = new StringBuilder();

        if (film.getName() == null || film.getName().isEmpty()) {
            sb.append("\nНазвание не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            sb.append("\nМаксимальная длина описания — %d символов".formatted(MAX_DESCRIPTION_LENGTH));
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(EARLIEST_FILM_DATE)) {
            sb.append("\nДата релиза не может быть раньше %s".formatted(EARLIEST_FILM_DATE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        }

        if (film.getDuration() < 0) {
            sb.append("\nПродолжительность фильма не может быть отрицательной");
        }

        if (!sb.isEmpty()) {
            log.debug("Film validation failed: {}", sb);
            throw new ValidationException("Film is not valid: " + sb);
        }
    }

    public Film getFilmById(int filmId) {
        final Optional<Film> filmOpt = filmStorage.getById(filmId);
        if (filmOpt.isEmpty()) {
            throw new FilmNotFound("Film not found with id " + filmId);
        }
        return filmOpt.get();
    }

    public void addLike(Film film, User user) {
        film.getLikes().add(user.getId());
    }

    public void removeLike(Film film, User user) {
        film.getLikes().remove(user.getId());
    }

    public Collection<Film> getFilmTop(int topN) {
        final Comparator<Film> compByLikes = Comparator.comparingInt(f -> f.getLikes().size());
        return filmStorage.getAll().stream()
                .sorted(compByLikes.reversed())
                .limit(topN)
                .toList();
    }
}
