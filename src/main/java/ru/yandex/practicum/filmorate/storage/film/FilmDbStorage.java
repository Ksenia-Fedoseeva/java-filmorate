package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.FilmLikeMapper;
import ru.yandex.practicum.filmorate.storage.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        String checkMpaSql = "SELECT COUNT(*) FROM mpa_rating WHERE rating_id = ?";
        Integer mpaExists = jdbcTemplate.queryForObject(checkMpaSql, Integer.class, film.getMpa().getId());

        if (mpaExists == null || mpaExists == 0) {
            throw new BadRequestException("MPA рейтинг с ID " + film.getMpa().getId() + " не существует.");
        }

        String sql = "INSERT INTO film (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId().intValue());
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        if (film.getGenres() != null) {
            film.getGenres().stream()
                    .distinct()
                    .forEach(genre -> {
                        jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                                film.getId(),
                                genre.getId());
                    });
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmExist(film.getId());
        jdbcTemplate.update("UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, "
                + "rating_id = ? WHERE film_id = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?",
                film.getId());
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> {
                jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)",
                        film.getId(),
                        genre.getId());
            });
        }
        return film;
    }

    @Override
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * FROM film JOIN mpa_rating ON film.rating_id = mpa_rating.rating_id";
        String sqlGenre = "SELECT * FROM film_genre JOIN genre ON genre.genre_id = film_genre.genre_id WHERE film_id = ?";

        List<Film> filmList = jdbcTemplate.query(sql, new FilmMapper());

        filmList.forEach(film -> {
            List<Genre> genres = jdbcTemplate.query(sqlGenre, new GenreMapper(), film.getId());
            film.setGenres(genres);
        });

        return filmList;
    }

    @Override
    public Film getFilmById(Long id) {
        checkFilmExist(id);
        String sqlFilm = "SELECT * FROM film JOIN mpa_rating ON film.rating_id = mpa_rating.rating_id WHERE film_id = ?";
        String sqlGenre = "SELECT * FROM film_genre JOIN genre ON genre.genre_id = film_genre.genre_id WHERE film_id = ?";

        Film film = jdbcTemplate.queryForObject(sqlFilm, new FilmMapper(), id);

        List<Genre> genres = jdbcTemplate.query(sqlGenre, new GenreMapper(), id);
        film.setGenres(genres);

        return film;
    }

    @Override
    public void checkFilmExist(Long id) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM film WHERE film_id = ?", Integer.class, id);
        if (count == 0 || count == null) {
            throw new NotFoundException("Фильм с ID " + id + " не найден.");
        }
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, COUNT(fl.user_id) AS like_count\n" +
                "FROM film f\n" +
                "LEFT JOIN film_like fl ON f.film_id = fl.film_id\n" +
                "GROUP BY f.film_id\n" +
                "HAVING like_count > 0\n" +
                "ORDER BY like_count DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, new FilmLikeMapper(), count);
    }
}
