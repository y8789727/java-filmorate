package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    @Test
    public void checkCreateFilm() {
        Film f = new Film();
        f.setName("Film Name");
        f.setDescription("Description");
        f.setDuration(50);
        f.setReleaseDate(LocalDate.of(1994, 1, 1));

        final FilmController fc = new FilmController();
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

        final FilmController fc = new FilmController();
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
        final FilmController fc = new FilmController();
        assertThrows(ValidationException.class, () -> fc.create(null));
    }

    @Test
    public void whenFillNameEmptyThenExceptionThrown() {
        final FilmController fc = new FilmController();
        final Film f = new Film();
        f.setName("");

        assertThrows(ValidationException.class, () -> fc.create(f));
    }

    @Test
    public void whenDurationNegativeThenExceptionThrown() {
        final FilmController fc = new FilmController();
        final Film f = new Film();
        f.setName("Name");
        f.setDuration(-500);

        assertThrows(ValidationException.class, () -> fc.create(f));
    }

    @Test
    public void whenNameIsNullThenNameEqualsLogin() {
        final FilmController fc = new FilmController();
        final Film f = new Film();
        f.setName("Name");
        f.setReleaseDate(LocalDate.of(1700,1,1));

        assertThrows(ValidationException.class, () -> fc.create(f));
    }

    @Test
    public void whenDescTooLargeThenNameEqualsLogin() {
        final FilmController fc = new FilmController();
        final Film f = new Film();
        f.setName("Name");

        f.setDescription("t".repeat(250));

        assertThrows(ValidationException.class, () -> fc.create(f));
    }
}