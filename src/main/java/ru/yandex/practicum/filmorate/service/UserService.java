package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User addUser(User user) {
        validateUser(user);
        log.info("Добавление юзера – {}", user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        log.info("Обновление юзера – {}", user);
        return userStorage.updateUser(user);
    }

    public Collection<User> getAllUsers() {
        log.info("Получение юзеров");
        return userStorage.getAllUsers();
    }

    public User getUserById(Long id) {
        User user = userStorage.getUserById(id);
        log.info("Получение юзера – {}", user);
        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Юзер {} и юзер {} теперь друзья", user, friend);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Юзер {} и юзер {} больше не друзья", user, friend);
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        log.info("Получаем друзей юзера {}", user);
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        log.info("Получаем общих друзей юзера {} и другого юзера {}", user, otherUser);
        return user.getFriends().stream()
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    private void validateUser(User user) {
        if (user.getBirthday() == null) {
            throw new ValidationException("Дата рождения не заполнена.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Email должен быть указан и содержать символ @.");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
}
}
