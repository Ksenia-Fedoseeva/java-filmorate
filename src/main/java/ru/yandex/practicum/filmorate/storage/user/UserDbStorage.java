package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO user_info (name, email, login, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"user_id"});
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());

        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUserExist(user.getId());

        String sql = "UPDATE user_info SET name = ?, email = ?, login = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM user_info";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    @Override
    public User getUserById(Long id) {
        checkUserExist(id);
        String sql = "SELECT * FROM user_info WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, new UserMapper(), id);
    }

    @Override
    public void checkUserExist(Long id) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user_info WHERE user_id = ?", Integer.class, id);
        if (count == 0 || count == null) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        Boolean existingStatus = getFriendshipStatus(friendId, id);

        if (existingStatus == null) {
            addFriendship(id, friendId, false);
        } else if (!existingStatus) {
            addFriendship(id, friendId, true);
            updateFriendshipStatus(friendId, id, true);
        }
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        Boolean existingStatus = getFriendshipStatus(friendId, id);

        if (existingStatus != null && existingStatus) {
            deleteFriendship(id, friendId);
            updateFriendshipStatus(friendId, id, false);
        } else {
            deleteFriendship(id, friendId);
        }
    }

    private Boolean getFriendshipStatus(Long userId, Long friendId) {
        String sql = "SELECT status FROM friendship WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return rs.getBoolean("status");
            }
            return null;
        }, userId, friendId);
    }

    private void addFriendship(Long userId, Long friendId, boolean status) {
        String sql = "INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, status);
    }

    private void updateFriendshipStatus(Long userId, Long friendId, boolean status) {
        String sql = "UPDATE friendship SET status = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, status, userId, friendId);
    }

    private void deleteFriendship(Long userId, Long friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long id) {
        String sql = "SELECT u.* FROM user_info u JOIN friendship f ON u.user_id = f.friend_id WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, new UserMapper(), id);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        String sql = "SELECT u.* FROM user_info u " +
                "JOIN friendship f1 ON u.user_id = f1.friend_id " +
                "JOIN friendship f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, new UserMapper(), id, otherId);
    }
}
