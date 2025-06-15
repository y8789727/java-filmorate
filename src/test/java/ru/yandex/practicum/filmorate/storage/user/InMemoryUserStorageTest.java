package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryUserStorageTest {
    @Test
    public void checkCreateUser() {
        User u = new User();
        u.setLogin("test");
        u.setEmail("some@mail.com");

        final UserStorage uc = new InMemoryUserStorage();
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

        final UserStorage uc = new InMemoryUserStorage();
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
    public void whenGetByIdNoUserExists() {
        final UserStorage uc = new InMemoryUserStorage();
        Optional<User> userOpt = uc.getById(-99);
        assertTrue(userOpt.isEmpty(), "Некорректное значение при поиска несуществующего пользователя");
    }

    @Test
    public void whenGetByIdUserExists() {
        final UserStorage uc = new InMemoryUserStorage();
        User u1 = new User();
        u1.setName("Name1");
        uc.create(u1);
        Optional<User> userOpt = uc.getById(u1.getId());
        assertEquals(u1.getId(), userOpt.get().getId(), "Некорректное значение при поиске пользователя");
    }
}