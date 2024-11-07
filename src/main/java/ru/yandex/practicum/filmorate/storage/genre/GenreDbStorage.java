package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreMapper;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre ORDER BY genre_id";
        return jdbcTemplate.query(sql, new GenreMapper());
    }

    @Override
    public Genre getGenreById(Long id) {
        checkGenreExist(id);
        String sql = "SELECT * FROM genre WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, new GenreMapper(), id);
    }

    @Override
    public void checkGenreExist(Long id) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM genre WHERE genre_id = ?", Integer.class, id);
        if (count == 0 || count == null) {
            throw new NotFoundException("Жанр с ID " + id + " не найден.");
        }
    }
}
