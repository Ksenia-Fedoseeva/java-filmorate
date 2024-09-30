package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final int MAX_DESCRIPTION = 200;
    private final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilmForCreation(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        Film existingFilm = films.get(film.getId());

        if (existingFilm == null) {
            throw new ValidationException("Фильм с таким ID не найден.");
        }

        if (film.getName() != null) {
            validateFilmForUpdate(film);
            existingFilm.setName(film.getName());
        }
        if (film.getDescription() != null) {
            validateFilmForUpdate(film);
            existingFilm.setDescription(film.getDescription());
        }
        if (film.getReleaseDate() != null) {
            validateFilmForUpdate(film);
            existingFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getDuration() > 0) {
            existingFilm.setDuration(film.getDuration());
        }

        validateFilmForUpdate(existingFilm);

        films.put(existingFilm.getId(), existingFilm);
        log.info("Фильм обновлен: {}", existingFilm);
        return existingFilm;
    }

    private long getNextId() {
        return films.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }

    private void validateFilmForCreation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Валидация не пройдена: название фильма не может быть пустым.");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() == null) {
            log.error("Валидация не пройдена: описание фильма не заполнено.");
            throw new ValidationException("Описание фильма не заполнено.");
        }
        if (film.getDescription().length() > MAX_DESCRIPTION) {
            log.error("Валидация не пройдена: длина описания фильма превышает 200 символов.");
            throw new ValidationException("Описание фильма не может превышать 200 символов.");
        }
        if (film.getReleaseDate() == null) {
            log.error("Валидация не пройдена: дата релиза не заполнена.");
            throw new ValidationException("Дата релиза не заполнена.");
        }
        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            log.error("Валидация не пройдена: дата релиза фильма раньше 28 декабря 1895 года.");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            log.error("Валидация не пройдена: продолжительность фильма должна быть положительным числом.");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }

    private void validateFilmForUpdate(Film film) {
        if (film.getName() != null && film.getName().isBlank()) {
            log.error("Валидация не пройдена: название фильма не может быть пустым.");
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() != null) {
            if (film.getDescription().isBlank()) {
                log.error("Валидация не пройдена: описание фильма не может быть пустым.");
                throw new ValidationException("Описание фильма не может быть пустым.");
            }
            if (film.getDescription().length() > MAX_DESCRIPTION) {
                log.error("Валидация не пройдена: длина описания фильма превышает 200 символов.");
                throw new ValidationException("Описание фильма не может превышать 200 символов.");
            }
        }
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
                log.error("Валидация не пройдена: дата релиза фильма раньше 28 декабря 1895 года.");
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
            }
        }
        if (film.getDuration() != 0 && film.getDuration() <= 0) {
            log.error("Валидация не пройдена: продолжительность фильма должна быть положительным числом.");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }
}