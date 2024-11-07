package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmDbStorage;

    private final UserStorage userDbStorage;

    private final GenreService genreService;

    public Film addFilm(Film film) {
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> {
                try {
                    genreService.checkGenreExist(genre.getId());
                } catch (Exception e) {
                    throw new BadRequestException(e.getMessage());
                }
            });
        }
        log.info("Добавление фильма – {}", film);
        return filmDbStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Обновление фильма – {}", film);
        return filmDbStorage.updateFilm(film);
    }

    public Collection<Film> getAllFilms() {
        log.info("Получение фильмов");
        return filmDbStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        Film film = filmDbStorage.getFilmById(id);
        log.info("Получение фильма – {}", film);
        return film;
    }

    public void addLike(Long filmId, Long userId) {
        userDbStorage.checkUserExist(userId);
        filmDbStorage.checkFilmExist(filmId);
        filmDbStorage.addLike(filmId, userId);
        log.info("Юзер {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(Long filmId, Long userId) {
        userDbStorage.checkUserExist(userId);
        filmDbStorage.checkFilmExist(filmId);
        filmDbStorage.removeLike(filmId, userId);
        log.info("Юзер {} удалил лайк к фильму {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получаем количество {} популярных фильмов", count);
        return filmDbStorage.getPopularFilms(count);
    }
}
