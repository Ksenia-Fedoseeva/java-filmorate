package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;
@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreDbStorage;
    public Collection<Genre> getAllGenres() {
        log.info("Получение жанров");
        return genreDbStorage.getAllGenres();
    }

    public Genre getGenreById(Long id) {
        Genre genre = genreDbStorage.getGenreById(id);
        log.info("Получение жанра – {}", genre);
        return genre;
    }

    public void checkGenreExist(Long id) {
       genreDbStorage.checkGenreExist(id);
    }

}
