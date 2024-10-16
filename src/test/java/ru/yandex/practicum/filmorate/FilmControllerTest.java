package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldThrowExceptionIfNameIsEmpty() throws Exception {
        Film film = new Film();
        film.setName("");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        String jsonFilm = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.film.name: Название не может быть пустым."));
    }

    @Test
    void shouldThrowExceptionIfDescriptionIsTooLong() throws Exception {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        String jsonFilm = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.film.description: Описание не может превышать 200 символов."));
    }

    @Test
    void shouldThrowExceptionIfReleaseDateIsTooEarly() throws Exception {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.of(1895, 1, 1));
        film.setDuration(120);

        String jsonFilm = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.film.releaseDate: Дата релиза не может быть раньше 28 декабря 1895 года или быть пустой."));
    }

    @Test
    void shouldThrowExceptionIfDurationIsNegative() throws Exception {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(-120);

        String jsonFilm = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.film.duration: Продолжительность фильма должна быть положительным числом."));
    }

    @Test
    void shouldThrowExceptionIfNameIsNull() throws Exception {
        Film film = new Film();
        film.setName(null);
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        String jsonFilm = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.film.name: Название не может быть пустым."));
    }

    @Test
    void shouldThrowExceptionIfDescriptionIsNull() throws Exception {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription(null);
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        String jsonFilm = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.film.description: Описание не может быть пустым."));
    }

    @Test
    void shouldThrowExceptionIfReleaseDateIsNull() throws Exception {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(null);
        film.setDuration(120);

        String jsonFilm = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.film.releaseDate: Дата релиза не может быть раньше 28 декабря 1895 года или быть пустой."));
    }

    @Test
    void shouldUpdateFilmWithValidFields() throws Exception {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        String jsonFilm = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Film updatedFilm = film;
        updatedFilm.setId(1L);
        updatedFilm.setName("Updated Movie");
        updatedFilm.setDescription("Updated description");


        String jsonUpdatedFilm = objectMapper.writeValueAsString(updatedFilm);

        mockMvc.perform(put("/films")
                        .content(jsonUpdatedFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Movie"))
                .andExpect(jsonPath("$.description").value("Updated description"));


    }

    @Test
    void shouldNotUpdateFieldsIfNullProvided() throws Exception {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        String jsonFilm = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Film updatedFilm = film;
        updatedFilm.setId(1L);

        String jsonUpdatedFilm = objectMapper.writeValueAsString(updatedFilm);

        mockMvc.perform(put("/films")
                        .content(jsonUpdatedFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Movie"))
                .andExpect(jsonPath("$.description").value("A good movie"));
    }

    @Test
    void shouldThrowExceptionIfFilmIdIsNotFound() throws Exception {
        Film film = new Film();
        film.setId(999L);
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        String jsonFilm = objectMapper.writeValueAsString(film);

        mockMvc.perform(put("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Фильм с ID 999 не найден."));
    }
}
