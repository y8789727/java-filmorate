package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    @Test
    public void checkCreateUser() {
        User u = new User();
        u.setLogin("test");
        u.setEmail("some@mail.com");

        final UserController uc = new UserController();
        uc.create(u);

        assertTrue(u.getId() != 0, "ID пользователя не сформирован");

        User[] expected = new User[1];
        expected[0] = u;
        assertArrayEquals(expected, uc.getAll().toArray(),"Список пользователей некорректен");
    }

    @Test
    public void checkUpdateUser() {
        User u1 = new User();
        u1.setLogin("test");
        u1.setEmail("some@mail.com");
        u1.setName("Name1");

        final UserController uc = new UserController();
        uc.create(u1);

        User u2 = new User();
        u2.setId(u1.getId());
        u2.setEmail("some@yandex.com");
        u2.setLogin("test2");

        uc.update(u2);

        User[] expected = new User[1];
        expected[0] = u2;
        assertArrayEquals(expected, uc.getAll().toArray(),"Список пользователей после обновления некорректен");
    }

    @Test
    public void whenNullUserThenExceptionThrown() {
        final UserController uc = new UserController();
        assertThrows(ValidationException.class, () -> uc.create(null));
    }

    @Test
    public void whenLoginNullThenExceptionThrown() {
        final UserController uc = new UserController();
        final User u = new User();
        u.setEmail("some@mail.com");
        u.setName("Name1");

        assertThrows(ValidationException.class, () -> uc.create(u));
    }

    @Test
    public void whenBirthDateInFutureThenExceptionThrown() {
        final UserController uc = new UserController();
        final User u = new User();
        u.setLogin("test2");
        u.setEmail("some@mail.com");
        u.setName("Name1");
        u.setBirthday(LocalDate.of(2125, 1,1));

        assertThrows(ValidationException.class, () -> uc.create(u));
    }

    @Test
    public void whenNameIsNullThenNameEqualsLogin() {
        final UserController uc = new UserController();
        final User u = new User();
        u.setLogin("test2");
        u.setEmail("some@mail.com");

        uc.create(u);

        assertEquals(u.getLogin(), u.getName(), "Если имя не задано, то должно равняться логину");
    }
}