package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private final UserController controller = new UserController();

    @Test
    void shouldThrowExceptionIfEmailIsInvalid() {
        User user = new User();
        user.setEmail("invalidEmail");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(user);
        });
        assertEquals("Имейл должен быть указан и содержать символ @.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfLoginIsInvalid() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("invalid login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(user);
        });
        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfBirthdayIsInFuture() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(3000, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(user);
        });
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfEmailIsNull() {
        User user = new User();
        user.setEmail(null);
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(user);
        });
        assertEquals("Имейл должен быть указан и содержать символ @.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfLoginIsNull() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin(null);
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(user);
        });
        assertEquals("Логин не может быть пустым и содержать пробелы.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfBirthdayIsNull() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testLogin");
        user.setBirthday(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(user);
        });
        assertEquals("Дата рождения не может быть в будущем.", exception.getMessage());
    }
}