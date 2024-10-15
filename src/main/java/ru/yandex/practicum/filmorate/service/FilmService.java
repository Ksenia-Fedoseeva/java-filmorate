package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addFilm(Film film) {
        log.info("Добавление фильма – {}", film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
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
        userStorage.checkUserExist(userId);
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        log.info("Юзер {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        userStorage.checkUserExist(userId);
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
        log.info("Юзер {} удалил лайк к фильму {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получаем количество {} популярных фильмов", count);
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
