package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUserExist(user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        User user = users.get(id);

        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }

        return user;
    }

    private long getNextId() {
        return users.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }

    @Override
    public void checkUserExist(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        throw new RuntimeException("Метод не поддерживается");
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        throw new RuntimeException("Метод не поддерживается");
    }

    @Override
    public List<User> getFriends(Long id) {
        throw new RuntimeException("Метод не поддерживается");
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        throw new RuntimeException("Метод не поддерживается");
    }
}
