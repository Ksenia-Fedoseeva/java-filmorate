package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class MpaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetAllMpaRatings() throws Exception {
        mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    void shouldGetMpaRatingById() throws Exception {
        mockMvc.perform(get("/mpa/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").isNotEmpty());
    }

    @Test
    void shouldReturnNotFoundForNonExistingMpaRating() throws Exception {
        mockMvc.perform(get("/mpa/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Рейтинг с ID 999 не найден."));
    }
}
