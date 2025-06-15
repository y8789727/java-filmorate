package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmServiceTest {
    private FilmService getFilmService() {
        FilmService fc = new FilmService();
        fc.setFilmStorage(new InMemoryFilmStorage());
        return fc;
    }

    @Test
    public void checkCreateFilm() {
        Film f = new Film();
        f.setName("Film Name");
        f.setDescription("Description");
        f.setDuration(50);
        f.setReleaseDate(LocalDate.of(1994, 1, 1));

        final FilmService fc = getFilmService();
        fc.create(f);

        assertTrue(f.getId() != 0, "ID фильма не сформирован");

        Film[] expected = new Film[1];
        expected[0] = f;
        assertArrayEquals(expected, fc.getAll().toArray(),"Список фильмов некорректен");
    }

    @Test
    public void checkUpdateFilm() {
        Film f1 = new Film();
        f1.setName("Name1");

        final FilmService fc = getFilmService();
        fc.create(f1);

        Film f2 = new Film();
        f2.setId(f1.getId());
        f2.setName("Name2");

        fc.update(f2);

        Film[] expected = new Film[1];
        expected[0] = f2;
        assertArrayEquals(expected, fc.getAll().toArray(),"Список фильмов после обновления некорректен");
    }

    @Test
    public void whenNullFilmThenExceptionThrown() {
        final FilmService fc = getFilmService();
        assertThrows(ValidationException.class, () -> fc.create(null));
    }

    @Test
    public void whenFillNameEmptyThenExceptionThrown() {
        final FilmService fc = getFilmService();
        final Film f = new Film();
        f.setName("");

        assertThrows(ValidationException.class, () -> fc.create(f));
    }

    @Test
    public void whenDurationNegativeThenExceptionThrown() {
        final FilmService fc = getFilmService();
        final Film f = new Film();
        f.setName("Name");
        f.setDuration(-500);

        assertThrows(ValidationException.class, () -> fc.create(f));
    }

    @Test
    public void whenNameIsNullThenNameEqualsLogin() {
        final FilmService fc = getFilmService();
        final Film f = new Film();
        f.setName("Name");
        f.setReleaseDate(LocalDate.of(1700,1,1));

        assertThrows(ValidationException.class, () -> fc.create(f));
    }

    @Test
    public void whenDescTooLargeThenNameEqualsLogin() {
        final FilmService fc = getFilmService();
        final Film f = new Film();
        f.setName("Name");

        f.setDescription("t".repeat(FilmService.MAX_DESCRIPTION_LENGTH + 10));

        assertThrows(ValidationException.class, () -> fc.create(f));
    }

    @Test
    public void testGetFilmById() {
        final FilmService fs = getFilmService();
        final Film f = new Film();
        f.setName("Name");
        fs.create(f);

        Film f2 = fs.getFilmById(f.getId());
        assertEquals(f.getId(), f2.getId(), "Некорректный поиск существующего фильма");

        assertThrows(FilmNotFound.class, () -> fs.getFilmById(-99), "Некорректный поиск несуществующего фильма");
    }

    @Test
    public void testAddRemoveLike() {
        final FilmService fs = getFilmService();
        final UserStorage us = new InMemoryUserStorage();

        User u = new User();
        u.setLogin("login");
        u.setName("Name");
        us.create(u);

        Film f = new Film();
        f.setName("Name");
        fs.create(f);

        fs.addLike(f, u);
        Integer[] expectedLikes = {u.getId()};
        assertArrayEquals(expectedLikes, f.getLikes().toArray(),"Список лайков некорректен");

        fs.removeLike(f, u);
        assertEquals(0, f.getLikes().size(), "Неверное количество лайков после удаления");
    }

    @Test
    public void testTopNFilms() {
        final FilmService fs = getFilmService();
        final UserStorage us = new InMemoryUserStorage();

        User u1 = new User();
        u1.setLogin("login1");
        u1.setName("Name1");
        us.create(u1);

        User u2 = new User();
        u2.setLogin("login2");
        u2.setName("Name2");
        us.create(u2);

        Film f1 = new Film();
        f1.setName("Name1");
        fs.create(f1);

        Film f2 = new Film();
        f2.setName("Name2");
        fs.create(f2);

        Film f3 = new Film();
        f3.setName("Name3");
        fs.create(f3);

        fs.addLike(f1, u1);
        fs.addLike(f1, u2);
        fs.addLike(f2, u1);
        fs.addLike(f2, u2);
        fs.removeLike(f1, u1);

        Film[] expected = {f2, f1, f3};
        assertArrayEquals(expected, fs.getFilmTop(5).toArray(),"Список ТОП-фильмов некорректен");
    }
}