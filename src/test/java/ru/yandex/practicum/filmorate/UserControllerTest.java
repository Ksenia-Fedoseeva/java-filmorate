package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
}
