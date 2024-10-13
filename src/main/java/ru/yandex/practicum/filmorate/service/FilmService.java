package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> filmLikes = new HashMap<>();

    private final int maxDescription = 200;
    private final LocalDate earliestReleaseDate = LocalDate.of(1895, 12, 28);

    public Film addFilm(Film film) {
        validateFilmForCreation(film);
        log.info("Добавление фильма – {}", film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilmForUpdate(film);
        log.info("Обновление фильма – {}", film);
        return filmStorage.updateFilm(film);
    }

    public Collection<Film> getAllFilms() {
        log.info("Получение фильмов");
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.getFilmById(id);
        log.info("Получение фильма – {}", film);
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.checkFilmExist(filmId);
        userStorage.checkUserExist(userId);
        filmLikes.putIfAbsent(filmId, new HashSet<>());
        filmLikes.get(filmId).add(userId);
        log.info("Юзер {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.checkFilmExist(filmId);
        userStorage.checkUserExist(userId);
        if (filmLikes.containsKey(filmId)) {
            filmLikes.get(filmId).remove(userId);
        }
        log.info("Юзер {} удалил лайк к фильму {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получаем количество {} популярных фильмов", count);
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(filmLikes.getOrDefault(f2.getId(), Collections.emptySet()).size(),
                        filmLikes.getOrDefault(f1.getId(), Collections.emptySet()).size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilmForCreation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() == null) {
            throw new ValidationException("Описание фильма не заполнено.");
        }
        if (film.getDescription().length() > maxDescription) {
            throw new ValidationException("Описание фильма не может превышать 200 символов.");
        }
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза не заполнена.");
        }
        if (film.getReleaseDate().isBefore(earliestReleaseDate)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }

    private void validateFilmForUpdate(Film film) {
        if (film.getName() != null && film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription() != null) {
            if (film.getDescription().isBlank()) {
                throw new ValidationException("Описание фильма не может быть пустым.");
            }
            if (film.getDescription().length() > maxDescription) {
                throw new ValidationException("Описание фильма не может превышать 200 символов.");
            }
        }
        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate().isBefore(earliestReleaseDate)) {
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
            }
        }
        if (film.getDuration() != 0 && film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
        }
    }
}
