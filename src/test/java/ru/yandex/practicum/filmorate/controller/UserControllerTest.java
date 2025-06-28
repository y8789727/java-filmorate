package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserNotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    private UserController getUserController() {
        UserService us = new UserService();
        us.setUserStorage(new InMemoryUserStorage());
        UserController uc = new UserController();
        uc.setUserService(us);
        return uc;
    }

    @Test
    public void checkCreateUser() {
        User u = new User();
        u.setLogin("test");
        u.setEmail("some@mail.com");

        final UserController uc = getUserController();
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

        final UserController uc = getUserController();
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
        final UserController uc = getUserController();
        assertThrows(ValidationException.class, () -> uc.create(null));
    }

    @Test
    public void whenLoginNullThenExceptionThrown() {
        final UserController uc = getUserController();
        final User u = new User();
        u.setEmail("some@mail.com");
        u.setName("Name1");

        assertThrows(ValidationException.class, () -> uc.create(u));
    }

    @Test
    public void whenBirthDateInFutureThenExceptionThrown() {
        final UserController uc = getUserController();
        final User u = new User();
        u.setLogin("test2");
        u.setEmail("some@mail.com");
        u.setName("Name1");
        u.setBirthday(LocalDate.of(2125, 1,1));

        assertThrows(ValidationException.class, () -> uc.create(u));
    }

    @Test
    public void whenNameIsNullThenNameEqualsLogin() {
        final UserController uc = getUserController();
        final User u = new User();
        u.setLogin("test2");
        u.setEmail("some@mail.com");

        uc.create(u);

        assertEquals(u.getLogin(), u.getName(), "Если имя не задано, то должно равняться логину");
    }

    @Test
    public void testGetUserById() {
        final UserController uc = getUserController();
        final User u = new User();
        u.setLogin("test2");
        u.setEmail("some@mail.com");
        uc.create(u);

        User u2 = uc.getUserById(u.getId());
        assertEquals(u.getId(), u2.getId(), "Некорректный поиск существующего пользователя");

        assertThrows(UserNotFound.class, () -> uc.getUserById(-99), "Некорректный поиск несуществующего пользователя");
    }

    @Test
    public void testAddRemoveFriend() {
        final UserController uc = getUserController();

        final User u1 = new User();
        u1.setLogin("test1");
        u1.setEmail("some1@mail.com");
        uc.create(u1);

        final User u2 = new User();
        u2.setLogin("test2");
        u2.setEmail("some2@mail.com");
        uc.create(u2);

        uc.addFriend(u1.getId(), u2.getId());
        Integer[] expected1 = {u2.getId()};
        assertArrayEquals(expected1, u1.getFriendsId().toArray(),"Список друзей для 1го пользователя некорректен");

        Integer[] expected2 = {};
        assertArrayEquals(expected2, u2.getFriendsId().toArray(),"Список друзей для 2го пользователя некорректен");

        uc.removeFriend(u1.getId(), u2.getId());
        assertEquals(0, u1.getFriendsId().size(), "Неверное количество друзей 1го пользователя после удаления");
        assertEquals(0, u2.getFriendsId().size(), "Неверное количество друзей 2го пользователя после удаления");
    }

    @Test
    public void testMutualFriends() {
        final UserController uc = getUserController();

        final User u1 = new User();
        u1.setLogin("test1");
        u1.setEmail("some1@mail.com");
        uc.create(u1);

        final User u2 = new User();
        u2.setLogin("test2");
        u2.setEmail("some2@mail.com");
        uc.create(u2);

        final User u3 = new User();
        u3.setLogin("test3");
        u3.setEmail("some3@mail.com");
        uc.create(u3);

        final User u4 = new User();
        u4.setLogin("test4");
        u4.setEmail("some4@mail.com");
        uc.create(u4);

        uc.addFriend(u1.getId(), u2.getId());
        uc.addFriend(u1.getId(), u3.getId());
        uc.addFriend(u4.getId(), u3.getId());
        uc.addFriend(u4.getId(), u2.getId());
        uc.addFriend(u4.getId(), u1.getId());

        User[] expected = {u2, u3};
        assertArrayEquals(expected, uc.getCommonFriends(u1.getId(), u4.getId()).toArray(),"Список общих друзей некорректен");
    }

}