package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRatingRepostory;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final MpaRatingRepostory mpaRatingRepostory;

    @Override
    public Collection<Film> getAll() {
        List<Film> allFilms = filmRepository.findAll();
        final Map<Integer, MpaRating> ratingsByFilmId = mpaRatingRepostory.findAllIndexByFilmId();
        final Map<Integer, Set<Genre>> genresByFilmId = genreRepository.findAllIndexByFilmId();
        final Map<Integer, Set<Integer>> likesByFilmId = userRepository.findAllLikesIndexByFilmId();
        allFilms.forEach(f -> {
            f.setMpaRating(ratingsByFilmId.get(f.getId()));
            f.setGenres(genresByFilmId.getOrDefault(f.getId(), new HashSet<>()));
            f.setLikes(likesByFilmId.getOrDefault(f.getId(), new HashSet<>()));
        });

        return allFilms;
    }

    @Override
    public Optional<Film> getById(int filmId) {
        return filmRepository.findById(filmId).map(f -> {
            f.setMpaRating(mpaRatingRepostory.findByFilm(f).orElse(null));
            f.setGenres(genreRepository.findByFilm(f));
            f.setLikes(userRepository.findByLikedFilm(f).stream().map(User::getId).collect(Collectors.toSet()));
            return f;
        });
    }

    @Override
    public Film create(Film film) {
        filmRepository.save(film);
        genreRepository.saveFilmGenres(film, film.getGenres());
        return film;
    }

    @Override
    public Film update(Film film) {
        Film f = filmRepository.update(film);
        genreRepository.saveFilmGenres(f, f.getGenres());
        return f;
    }

    @Override
    public void addLike(Film film, User user) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.getLikes().add(user.getId());
        filmRepository.insertLike(film, user.getId());
    }

    @Override
    public void removeLike(Film film, User user) {
        film.getLikes().remove(user.getId());
        filmRepository.deleteLike(film, user.getId());
    }

    @Override
    public Collection<Film> getTopN(int topN) {
        List<Film> topFilms = filmRepository.findTop(topN);
        final Map<Integer, MpaRating> ratingsByFilmId = mpaRatingRepostory.findAllIndexByFilmId();
        final Map<Integer, Set<Genre>> genresByFilmId = genreRepository.findAllIndexByFilmId();
        final Map<Integer, Set<Integer>> likesByFilmId = userRepository.findAllLikesIndexByFilmId();
        topFilms.forEach(f -> {
            f.setMpaRating(ratingsByFilmId.get(f.getId()));
            f.setGenres(genresByFilmId.getOrDefault(f.getId(), new HashSet<>()));
            f.setLikes(likesByFilmId.getOrDefault(f.getId(), new HashSet<>()));
        });
        return topFilms;
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    @Override
    public Optional<Genre> getGenreById(int genreId) {
        return genreRepository.findById(genreId);
    }

    @Override
    public Collection<MpaRating> getAllMpaRatings() {
        return mpaRatingRepostory.findAll();
    }

    @Override
    public Optional<MpaRating> getMpaRatingById(int ratingId) {
        return mpaRatingRepostory.findById(ratingId);
    }
}
