package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmExist(film.getId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(Long id) {
        Film film = films.get(id);

        if (film == null) {
            throw new NotFoundException("Фильм с ID " + id + " не найден.");
        }

        return film;
    }

    private long getNextId() {
        return films.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }

    @Override
    public void checkFilmExist(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с ID " + id + " не найден.");
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        throw new RuntimeException("Метод не поддерживается");
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        throw new RuntimeException("Метод не поддерживается");
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        throw new RuntimeException("Метод не поддерживается");
    }
}
