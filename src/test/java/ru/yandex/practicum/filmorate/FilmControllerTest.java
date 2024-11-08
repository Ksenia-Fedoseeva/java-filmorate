package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @BeforeEach
//    void cleanDb() {
//        jdbcTemplate.execute("DELETE FROM film_genre;");
//        jdbcTemplate.execute("DELETE FROM film;");
//        jdbcTemplate.execute("ALTER TABLE film ALTER COLUMN film_id RESTART WITH 1;");
//    }

    @AfterEach
    void cleanDbAfterAll() {
        jdbcTemplate.execute("DELETE FROM film_genre;");
        jdbcTemplate.execute("DELETE FROM film_like;");
        jdbcTemplate.execute("DELETE FROM film;");
        jdbcTemplate.execute("ALTER TABLE film ALTER COLUMN film_id RESTART WITH 1;");
        jdbcTemplate.execute("DELETE FROM user_info;");
        jdbcTemplate.execute("ALTER TABLE user_info ALTER COLUMN user_id RESTART WITH 1;");
    }

    @Test
    void shouldCreateFilmWithValidFields() throws Exception {
        Film film = new Film();
        film.setName("Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "G"));
        film.setGenres(List.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")));

        String jsonFilm = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowExceptionIfNameIsEmpty() throws Exception {
        Film film = new Film();
        film.setName("");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "G"));
        film.setGenres(List.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")));

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
        film.setMpa(new MpaRating(1L, "G"));
        film.setGenres(List.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")));

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
        film.setMpa(new MpaRating(1L, "G"));
        film.setGenres(List.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")));

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
        film.setMpa(new MpaRating(1L, "G"));
        film.setGenres(List.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")));

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
        film.setMpa(new MpaRating(1L, "G"));
        film.setGenres(List.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")));

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
        film.setMpa(new MpaRating(1L, "G"));
        film.setGenres(List.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")));

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
        film.setMpa(new MpaRating(1L, "G"));
        film.setGenres(List.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")));

        String jsonFilm = objectMapper.writeValueAsString(film);

        mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("create.film.releaseDate: Дата релиза не должна быть пустой при создании."));
    }

    @Test
    void shouldUpdateFilmWithValidFields() throws Exception {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "G"));
        film.setGenres(List.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")));

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
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotUpdateFieldsIfNullProvided() throws Exception {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "G"));
        film.setGenres(List.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")));

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
        film.setMpa(new MpaRating(1L, "G"));
        film.setGenres(List.of(new Genre(1L, "Комедия"), new Genre(2L, "Драма")));

        String jsonFilm = objectMapper.writeValueAsString(film);

        mockMvc.perform(put("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Фильм с ID 999 не найден."));
    }

    @Test
    void shouldNotAddDuplicateGenres() throws Exception {
        Film film = new Film();
        film.setName("Test Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "G"));
        film.setGenres(List.of(new Genre(1L, "Комедия"), new Genre(1L, "Комедия")));

        String jsonFilm = objectMapper.writeValueAsString(film);

        MvcResult result = mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        Film createdFilm = objectMapper.readValue(jsonResponse, Film.class);

        mockMvc.perform(get("/films/{id}", createdFilm.getId())
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genres.length()").value(1));
    }

    @Test
    void shouldReturnNotFoundForNonExistingFilmId() throws Exception {
        mockMvc.perform(get("/films/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorMessage").value("Фильм с ID 9999 не найден."));
    }

    @Test
    void shouldAddLikeToFilm() throws Exception {
        User user = new User();
        user.setName("User1");
        user.setEmail("user1@test.com");
        user.setLogin("user1Login");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        String jsonUser = objectMapper.writeValueAsString(user);

        MvcResult userResult = mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Long userId = objectMapper.readValue(userResult.getResponse().getContentAsString(), User.class).getId();

        Film film = new Film();
        film.setName("Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "G"));
        String jsonFilm = objectMapper.writeValueAsString(film);

        MvcResult filmResult = mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Long filmId = objectMapper.readValue(filmResult.getResponse().getContentAsString(), Film.class).getId();

        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(filmId));
    }

    @Test
    void shouldRemoveLikeFromFilm() throws Exception {
        User user = new User();
        user.setName("User1");
        user.setEmail("user1@test.com");
        user.setLogin("user1Login");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        String jsonUser = objectMapper.writeValueAsString(user);

        MvcResult userResult = mockMvc.perform(post("/users")
                        .content(jsonUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Long userId = objectMapper.readValue(userResult.getResponse().getContentAsString(), User.class).getId();

        Film film = new Film();
        film.setName("Movie");
        film.setDescription("A good movie");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "G"));
        String jsonFilm = objectMapper.writeValueAsString(film);

        MvcResult filmResult = mockMvc.perform(post("/films")
                        .content(jsonFilm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Long filmId = objectMapper.readValue(filmResult.getResponse().getContentAsString(), Film.class).getId();

        mockMvc.perform(put("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/films/{id}/like/{userId}", filmId, userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
