package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFound;
import ru.yandex.practicum.filmorate.exception.ReferenceObjectNotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

        if (film.getMpaRating() != null) {
            Optional<MpaRating> mpaOpt = filmStorage.getMpaRatingById(film.getMpaRating().getId());
            if (mpaOpt.isEmpty()) {
                throw new ReferenceObjectNotFound("Invalid MPA rating id=" + film.getMpaRating().getId());
            }
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Integer> genreIds = filmStorage.getAllGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            film.getGenres().forEach(g -> {
                    if (!genreIds.contains(g.getId())) {
                        throw new ReferenceObjectNotFound("Invalid genre id=" + g.getId());
                    }
            });
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
        filmStorage.addLike(film, user);
    }

    public void removeLike(Film film, User user) {
        filmStorage.removeLike(film, user);
    }

    public Collection<Film> getFilmTop(int topN) {
        return filmStorage.getTopN(topN);
    }

    public Collection<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int genreId) {
        final Optional<Genre> genreOpt = filmStorage.getGenreById(genreId);
        if (genreOpt.isEmpty()) {
            throw new FilmNotFound("Genre not found with id " + genreId);
        }
        return genreOpt.get();
    }

    public Collection<MpaRating> getAllMpaRatings() {
        return filmStorage.getAllMpaRatings();
    }

    public MpaRating getMpaRatingById(int mpaRatingId) {
        final Optional<MpaRating> ratingOpt = filmStorage.getMpaRatingById(mpaRatingId);
        if (ratingOpt.isEmpty()) {
            throw new FilmNotFound("MPA rating not found with id " + mpaRatingId);
        }
        return ratingOpt.get();
    }

}
