package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        return filmService.getFilmById(id);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        Film existingFilm = filmService.getFilmById(film.getId());

        if (existingFilm == null) {
            throw new ValidationException("Фильм с таким ID не найден.");
        }

        if (film.getName() != null) {
            existingFilm.setName(film.getName());
        }
        if (film.getDescription() != null) {
            existingFilm.setDescription(film.getDescription());
        }
        if (film.getReleaseDate() != null) {
            existingFilm.setReleaseDate(film.getReleaseDate());
        }
        if (film.getDuration() > 0) {
            existingFilm.setDuration(film.getDuration());
        }

        return filmService.updateFilm(existingFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }
}