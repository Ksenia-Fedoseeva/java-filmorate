package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mappers.MpaMapper;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<MpaRating> getAllMpa() {
        String sql = "SELECT * FROM mpa_rating ORDER BY rating_id";
        return jdbcTemplate.query(sql, new MpaMapper());
    }

    @Override
    public MpaRating getMpaById(Long id) {
        checkMpaExist(id);
        String sql = "SELECT * FROM mpa_rating WHERE rating_id = ?";
        return jdbcTemplate.queryForObject(sql, new MpaMapper(), id);
    }

    @Override
    public void checkMpaExist(Long id) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM mpa_rating WHERE rating_id = ?", Integer.class, id);
        if (count == 0 || count == null) {
            throw new NotFoundException("Рейтинг с ID " + id + " не найден.");
        }
    }
}
