package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final UserRepository userRepository;

    @Override
    public Collection<User> getAll() {
        final List<User> allUsers = userRepository.findAll();
        final Map<Integer, Set<Integer>> friendsIndexByUserId = userRepository.findAllFriendsIndexByUserId();
        allUsers.forEach(u -> u.setFriendsId(friendsIndexByUserId.getOrDefault(u.getId(), new HashSet<>())));
        return allUsers;
    }

    @Override
    public Optional<User> getById(int userId) {
        return userRepository.findById(userId).map(u -> {
            u.setFriendsId(new HashSet<>(userRepository.findUserFriendIds(u)));
            return u;
        });
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.update(user);
    }

    @Override
    public void addFriend(User user, User friend) {
        user.getFriendsId().add(friend.getId());
        userRepository.insertFriend(user, friend);
    }

    @Override
    public void removeFriend(User user, User friend) {
        user.getFriendsId().remove(friend.getId());
        userRepository.deleteFriend(user, friend);
    }

    @Override
    public List<User> getMutualFriend(User user1, User user2) {
        final List<User> mutualFriends = userRepository.findMutualFriends(user1, user2);
        final Map<Integer, Set<Integer>> friendsIndexByUserId = userRepository.findAllFriendsIndexByUserId();
        mutualFriends.forEach(u -> u.setFriendsId(friendsIndexByUserId.getOrDefault(u.getId(), new HashSet<>())));

        return mutualFriends;
    }
}
