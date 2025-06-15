package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryFilmStorageTest {
    @Test
    public void checkCreateFilm() {
        Film f = new Film();
        f.setName("Film Name");
        f.setDescription("Description");
        f.setDuration(50);
        f.setReleaseDate(LocalDate.of(1994, 1, 1));

        final FilmStorage fc = new InMemoryFilmStorage();
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

        final FilmStorage fc = new InMemoryFilmStorage();
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
    public void whenGetByIdNoFilmExists() {
        final FilmStorage fs = new InMemoryFilmStorage();
        Optional<Film> filmOpt = fs.getById(-99);
        assertTrue(filmOpt.isEmpty(), "Некорректное значение при поиска несуществующего фильма");
    }

    @Test
    public void whenGetByIdFilmExists() {
        final FilmStorage fs = new InMemoryFilmStorage();
        Film f1 = new Film();
        f1.setName("Name1");
        fs.create(f1);
        Optional<Film> filmOpt = fs.getById(f1.getId());
        assertEquals(f1.getId(), filmOpt.get().getId(), "Некорректное значение при поиске фильма");
    }
}