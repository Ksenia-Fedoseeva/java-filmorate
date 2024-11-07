package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanDb() {
        jdbcTemplate.execute("DELETE FROM friendship;");
        jdbcTemplate.execute("DELETE FROM user_info;");
        jdbcTemplate.execute("ALTER TABLE user_info ALTER COLUMN user_id RESTART WITH 1;");
    }

    @Test
    void shouldAddFriends() throws Exception {
        User user1 = new User();
        user1.setName("Test1");
        user1.setEmail("test1@test.com");
        user1.setLogin("test1Login");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        String jsonUser1 = objectMapper.writeValueAsString(user1);

        MvcResult result1 = mockMvc.perform(post("/users")
                        .content(jsonUser1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse1 = result1.getResponse().getContentAsString();
        User createdUser1 = objectMapper.readValue(jsonResponse1, User.class);

        User user2 = new User();
        user2.setName("Test2");
        user2.setEmail("test2@test.com");
        user2.setLogin("test2Login");
        user2.setBirthday(LocalDate.of(1990, 1, 1));

        String jsonUser2 = objectMapper.writeValueAsString(user2);

        MvcResult result2 = mockMvc.perform(post("/users")
                        .content(jsonUser2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse2 = result2.getResponse().getContentAsString();
        User createdUser2 = objectMapper.readValue(jsonResponse2, User.class);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", createdUser1.getId(), createdUser2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(put("/users/{id}/friends/{friendId}", createdUser2.getId(), createdUser1.getId()))
                .andExpect(status().isOk());

        String sql = "SELECT * FROM friendship";
        List<Map<String, Object>> friendships = jdbcTemplate.queryForList(sql);

        Assertions.assertEquals(2, friendships.size());

        Map<String, Object> row1 = friendships.get(0);
        Assertions.assertEquals(1, row1.get("USER_ID"));
        Assertions.assertEquals(2, row1.get("FRIEND_ID"));
        Assertions.assertEquals(true, row1.get("STATUS"));

        Map<String, Object> row2 = friendships.get(1);
        Assertions.assertEquals(2, row2.get("USER_ID"));
        Assertions.assertEquals(1, row2.get("FRIEND_ID"));
        Assertions.assertEquals(true, row2.get("STATUS"));
    }

    @Test
    void shouldRemoveFriend() throws Exception {
        User user1 = new User();
        user1.setName("Test1");
        user1.setEmail("test1@test.com");
        user1.setLogin("test1Login");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        String jsonUser1 = objectMapper.writeValueAsString(user1);

        MvcResult result1 = mockMvc.perform(post("/users")
                        .content(jsonUser1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse1 = result1.getResponse().getContentAsString();
        User createdUser1 = objectMapper.readValue(jsonResponse1, User.class);

        User user2 = new User();
        user2.setName("Test2");
        user2.setEmail("test2@test.com");
        user2.setLogin("test2Login");
        user2.setBirthday(LocalDate.of(1990, 1, 1));

        String jsonUser2 = objectMapper.writeValueAsString(user2);

        MvcResult result2 = mockMvc.perform(post("/users")
                        .content(jsonUser2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse2 = result2.getResponse().getContentAsString();
        User createdUser2 = objectMapper.readValue(jsonResponse2, User.class);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", createdUser1.getId(), createdUser2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(put("/users/{id}/friends/{friendId}", createdUser2.getId(), createdUser1.getId()))
                .andExpect(status().isOk());

        String sql = "SELECT * FROM friendship";
        List<Map<String, Object>> friendships = jdbcTemplate.queryForList(sql);

        Assertions.assertEquals(2, friendships.size());

        Map<String, Object> row1 = friendships.get(0);
        Assertions.assertEquals(createdUser1.getId().intValue(), row1.get("user_id"));
        Assertions.assertEquals(createdUser2.getId().intValue(), row1.get("friend_id"));
        Assertions.assertEquals(true, row1.get("status"));

        Map<String, Object> row2 = friendships.get(1);
        Assertions.assertEquals(createdUser2.getId().intValue(), row2.get("user_id"));
        Assertions.assertEquals(createdUser1.getId().intValue(), row2.get("friend_id"));
        Assertions.assertEquals(true, row2.get("status"));

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", createdUser1.getId(), createdUser2.getId()))
                .andExpect(status().isOk());

        friendships = jdbcTemplate.queryForList(sql);

        Assertions.assertEquals(1, friendships.size());
        Map<String, Object> remainingFriendship = friendships.get(0);
        Assertions.assertEquals(createdUser2.getId().intValue(), remainingFriendship.get("user_id"));
        Assertions.assertEquals(createdUser1.getId().intValue(), remainingFriendship.get("friend_id"));
        Assertions.assertEquals(false, remainingFriendship.get("status"));
    }

    @Test
    void shouldCreateUserWithValidFields() throws Exception {
        User user = new User();
        user.setName("Test");
        user.setEmail("test@test.com");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowExceptionIfEmailIsInvalid() throws Exception {
        User user = new User();
        user.setEmail("invalidEmail");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.user.email: Email должен содержать символ @."));
    }

    @Test
    void shouldThrowExceptionIfLoginIsInvalid() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("invalid login");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.user.login: Логин не может содержать пробелы."));
    }

    @Test
    void shouldThrowExceptionIfBirthdayIsInFuture() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(3000, 1, 1));

        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.user.birthday: Дата рождения не может быть в будущем."));
    }

    @Test
    void shouldThrowExceptionIfEmailIsNull() throws Exception {
        User user = new User();
        user.setEmail(null);
        user.setLogin("testLogin");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.user.email: Email должен быть указан."));
    }

    @Test
    void shouldThrowExceptionIfLoginIsNull() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin(null);
        user.setBirthday(LocalDate.of(1990, 1, 1));

        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.user.login: Логин не может быть пустым."));
    }

    @Test
    void shouldThrowExceptionIfBirthdayIsNull() throws Exception {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testLogin");
        user.setBirthday(null);

        String jsonUser = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.user.birthday: Дата рождения не заполнена."));
    }

    @Test
    void shouldGetUserFriends() throws Exception {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@test.com");
        user1.setLogin("user1Login");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@test.com");
        user2.setLogin("user2Login");
        user2.setBirthday(LocalDate.of(1990, 1, 1));

        String jsonUser1 = objectMapper.writeValueAsString(user1);
        String jsonUser2 = objectMapper.writeValueAsString(user2);

        mockMvc.perform(post("/users")
                        .content(jsonUser1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult result2 = mockMvc.perform(post("/users")
                        .content(jsonUser2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        User createdUser2 = objectMapper.readValue(result2.getResponse().getContentAsString(), User.class);

        mockMvc.perform(put("/users/{id}/friends/{friendId}", 1L, createdUser2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}/friends", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(createdUser2.getId()));
    }

    @Test
    void shouldGetCommonFriends() throws Exception {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@test.com");
        user1.setLogin("user1Login");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        String jsonUser1 = objectMapper.writeValueAsString(user1);

        MvcResult result1 = mockMvc.perform(post("/users")
                        .content(jsonUser1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Long userId1 = objectMapper.readValue(result1.getResponse().getContentAsString(), User.class).getId();

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@test.com");
        user2.setLogin("user2Login");
        user2.setBirthday(LocalDate.of(1990, 1, 1));
        String jsonUser2 = objectMapper.writeValueAsString(user2);

        MvcResult result2 = mockMvc.perform(post("/users")
                        .content(jsonUser2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Long userId2 = objectMapper.readValue(result2.getResponse().getContentAsString(), User.class).getId();

        User user3 = new User();
        user3.setName("User3");
        user3.setEmail("user3@test.com");
        user3.setLogin("user3Login");
        user3.setBirthday(LocalDate.of(1990, 1, 1));
        String jsonUser3 = objectMapper.writeValueAsString(user3);

        MvcResult result3 = mockMvc.perform(post("/users")
                        .content(jsonUser3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Long userId3 = objectMapper.readValue(result3.getResponse().getContentAsString(), User.class).getId();

        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId1, userId3)).andExpect(status().isOk());
        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId2, userId3)).andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", userId1, userId2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("User3"));
    }
}
