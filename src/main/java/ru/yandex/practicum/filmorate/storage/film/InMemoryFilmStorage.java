package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFound;
import ru.yandex.practicum.filmorate.model.Film;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    private int lastId = 0;

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("Film id={} created", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.debug("Fail to update film: no film with ID = {}", film.getId());
            throw new FilmNotFound("Updated film with id = %d not found!".formatted(film.getId()));
        }

        films.put(film.getId(), film);
        log.debug("Film id={} updated", film.getId());
        return film;
    }

    private int getNextId() {
        return ++lastId;
    }

    @Override
    public Optional<Film> getById(int filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public void addLike(Film film, User user) {
        film.getLikes().add(user.getId());
    }

    @Override
    public void removeLike(Film film, User user) {
        film.getLikes().remove(user.getId());
    }

    @Override
    public Collection<Film> getTopN(int topN) {
        final Comparator<Film> compByLikes = Comparator.comparingInt(f -> f.getLikes().size());
        return getAll().stream()
                .sorted(compByLikes.reversed())
                .limit(topN)
                .toList();
    }

    @Override
    public Collection<Genre> getAllGenres() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Genre> getGenreById(int genreId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<MpaRating> getAllMpaRatings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<MpaRating> getMpaRatingById(int ratingId) {
        throw new UnsupportedOperationException();
    }
}
