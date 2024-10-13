package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    void setUp() {
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        controller = new FilmController(filmService);
    }

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

    @Test
    void shouldThrowExceptionIfNameIsNull() {
        Film film = new Film();
        film.setName(null);
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(film);
        });
        assertEquals("Название фильма не может быть пустым.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfDescriptionIsNull() {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription(null);
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(film);
        });
        assertEquals("Описание фильма не заполнено.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfReleaseDateIsNull() {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(null);
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.create(film);
        });
        assertEquals("Дата релиза не заполнена.", exception.getMessage());
    }

    @Test
    void shouldUpdateFilmWithValidFields() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        controller.create(film);

        Film updatedFilm = new Film();
        updatedFilm.setId(1L);
        updatedFilm.setName("Updated Movie");
        updatedFilm.setDescription("Updated description");

        Film result = controller.update(updatedFilm);
        assertEquals("Updated Movie", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(film.getReleaseDate(), result.getReleaseDate());
    }

    @Test
    void shouldNotUpdateFieldsIfNullProvided() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        controller.create(film);

        Film updatedFilm = new Film();
        updatedFilm.setId(1L);

        Film result = controller.update(updatedFilm);

        assertEquals("Test Movie", result.getName());
        assertEquals("A good movie", result.getDescription());
        assertEquals(film.getReleaseDate(), result.getReleaseDate());
        assertEquals(film.getDuration(), result.getDuration());
    }

    @Test
    void shouldThrowExceptionIfFilmIdIsNotFound() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        Film updatedFilm = new Film();
        updatedFilm.setId(999L);
        updatedFilm.setName("Updated Movie");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            controller.update(updatedFilm);
        });
        assertEquals("Фильм с ID 999 не найден.", exception.getMessage());
    }
}