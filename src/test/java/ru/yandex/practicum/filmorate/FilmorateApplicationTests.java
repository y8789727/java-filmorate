package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan(basePackages = "ru.yandex.practicum.filmorate")
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;

	@Test
	public void checkCreateUser() {
		User u = new User();
		u.setLogin("test");
		u.setEmail("some@mail.com");

		u = userStorage.create(u);

		assertTrue(u.getId() != 0, "ID пользователя не сформирован");

		User[] expected = new User[1];
		expected[0] = u;
		assertArrayEquals(expected, userStorage.getAll().toArray(),"Список пользователей некорректен");
	}

	@Test
	public void checkUpdateUser() {
		User u1 = new User();
		u1.setLogin("test");
		u1.setEmail("some@mail.com");
		u1.setName("Name1");

		userStorage.create(u1);

		User u2 = new User();
		u2.setId(u1.getId());
		u2.setEmail("some@yandex.com");
		u2.setLogin("test2");

		userStorage.update(u2);

		User[] expected = new User[1];
		expected[0] = u2;
		assertArrayEquals(expected, userStorage.getAll().toArray(),"Список пользователей после обновления некорректен");
	}

	@Test
	public void whenGetByIdNoUserExists() {
		Optional<User> userOpt = userStorage.getById(-99);
		assertTrue(userOpt.isEmpty(), "Некорректное значение при поиска несуществующего пользователя");
	}

	@Test
	public void whenGetByIdUserExists() {
		User u1 = new User();
		u1.setName("Name1");
		u1.setLogin("Name1");
		userStorage.create(u1);
		Optional<User> userOpt = userStorage.getById(u1.getId());
		assertEquals(u1.getId(), userOpt.get().getId(), "Некорректное значение при поиске пользователя");
	}

	@Test
	public void checkCreateFilm() {
		Film f = new Film();
		f.setName("Film Name");
		f.setDescription("Description");
		f.setDuration(50);
		f.setReleaseDate(LocalDate.of(1994, 1, 1));
		MpaRating rating = new MpaRating();
		rating.setId(1);
		rating.setName("G");
		f.setMpaRating(rating);

		f = filmStorage.create(f);

		assertTrue(f.getId() != 0, "ID фильма не сформирован");

		Film[] expected = new Film[1];
		expected[0] = f;
		assertArrayEquals(expected, filmStorage.getAll().toArray(),"Список фильмов некорректен");
	}

	@Test
	public void checkUpdateFilm() {
		Film f1 = new Film();
		f1.setName("Name1");
		MpaRating rating2 = new MpaRating();
		rating2.setId(2);
		f1.setMpaRating(rating2);

		filmStorage.create(f1);

		Film f2 = new Film();
		f2.setId(f1.getId());
		f2.setName("Name2");
		MpaRating rating4 = new MpaRating();
		rating4.setId(4);
		rating4.setName("R");
		f2.setMpaRating(rating4);

		filmStorage.update(f2);

		Film[] expected = new Film[1];
		expected[0] = f2;
		assertArrayEquals(expected, filmStorage.getAll().toArray(),"Список фильмов после обновления некорректен");
	}

	@Test
	public void whenGetByIdNoFilmExists() {
		Optional<Film> filmOpt = filmStorage.getById(-99);
		assertTrue(filmOpt.isEmpty(), "Некорректное значение при поиска несуществующего фильма");
	}

	@Test
	public void whenGetByIdFilmExists() {
		Film f1 = new Film();
		f1.setName("Name1");
		filmStorage.create(f1);
		Optional<Film> filmOpt = filmStorage.getById(f1.getId());
		assertEquals(f1.getId(), filmOpt.get().getId(), "Некорректное значение при поиске фильма");
	}
}
