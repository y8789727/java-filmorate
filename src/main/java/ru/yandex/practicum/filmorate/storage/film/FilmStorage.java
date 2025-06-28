package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> getAll();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> getById(int filmId);

    void addLike(Film film, User user);

    void removeLike(Film film, User user);

    Collection<Film> getTopN(int topN);

    Collection<Genre> getAllGenres();

    Optional<Genre> getGenreById(int genreId);

    Collection<MpaRating> getAllMpaRatings();

    Optional<MpaRating> getMpaRatingById(int ratingId);
}
