package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user.getId() == null || !users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким ID не найден.");
        }
        validateUser(user);
        users.put(user.getId(), user);
        log.info("Пользователь обновлен: {}", user);
        return user;
    }

    private long getNextId() {
        return users.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }

    private void validateUser(User user) {
        if (user.getBirthday() == null) {
            log.error("Валидация не пройдена: дата рождения не заполнена.");
            throw new ValidationException("Дата рождения не заполнена.");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.error("Валидация не пройдена: некорректный email.");
            throw new ValidationException("Имейл должен быть указан и содержать символ @.");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            log.error("Валидация не пройдена: некорректный login.");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Валидация не пройдена: дата рождения в будущем.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}