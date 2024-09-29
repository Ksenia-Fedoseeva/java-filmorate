package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private final FilmController controller = new FilmController();

    @Test
    void shouldThrowExceptionIfNameIsEmpty() {
        Film film = new Film();
        film.setName("");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(film);
        });
        assertEquals("Название фильма не может быть пустым.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfDescriptionIsTooLong() {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(film);
        });
        assertEquals("Описание фильма не может превышать 200 символов.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfReleaseDateIsTooEarly() {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.of(1890, 1, 1));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(film);
        });
        assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfDurationIsNegative() {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-120);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(film);
        });
        assertEquals("Продолжительность фильма должна быть положительным числом.", exception.getMessage());
    }
}