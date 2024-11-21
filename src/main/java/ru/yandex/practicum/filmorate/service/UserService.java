package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userDbStorage;

    public User addUser(User user) {
        log.info("Добавление юзера – {}", user);
        return userDbStorage.addUser(user);
    }

    public User updateUser(User user) {
        log.info("Обновление юзера – {}", user);
        return userDbStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        log.info("Получение юзеров");
        return userDbStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        User user = userDbStorage.getUserById(id);
        log.info("Получение юзера – {}", user);
        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        userDbStorage.checkUserExist(userId);
        userDbStorage.checkUserExist(friendId);
        userDbStorage.addFriend(userId, friendId);
        log.info("Юзер {} и юзер {} теперь друзья", userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userDbStorage.checkUserExist(userId);
        userDbStorage.checkUserExist(friendId);
        userDbStorage.removeFriend(userId, friendId);
        log.info("Юзер {} и юзер {} больше не друзья", userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        log.info("Получаем друзей юзера {}", userId);
        userDbStorage.checkUserExist(userId);
        return userDbStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        log.info("Получаем общих друзей юзера {} и другого юзера {}", userId, otherUserId);
        return userDbStorage.getCommonFriends(userId, otherUserId);
    }
}
