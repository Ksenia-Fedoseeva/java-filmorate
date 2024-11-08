package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.ValidReleaseDate;
import ru.yandex.practicum.filmorate.annotations.ValidationGroup;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {

    @Null(groups = ValidationGroup.OnCreate.class, message = "ID должен быть пустым при создании.")
    @NotNull(groups = ValidationGroup.OnUpdate.class, message = "ID должен быть указан при обновлении.")
    private Long id;

    @NotBlank(groups = {ValidationGroup.OnCreate.class}, message = "Название не может быть пустым.")
    private String name;

    @NotBlank(groups = {ValidationGroup.OnCreate.class}, message = "Описание не может быть пустым.")
    @Size(groups = {ValidationGroup.OnCreate.class, ValidationGroup.OnUpdate.class}, max = 200,
            message = "Описание не может превышать 200 символов.")
    private String description;

    @NotNull(groups = {ValidationGroup.OnCreate.class}, message = "Дата релиза не должна быть пустой при создании.")
    @ValidReleaseDate(groups = {ValidationGroup.OnCreate.class})
    private LocalDate releaseDate;

    @Positive(groups = {ValidationGroup.OnCreate.class, ValidationGroup.OnUpdate.class},
            message = "Продолжительность фильма должна быть положительным числом.")
    private int duration;

    @NotNull(groups = {ValidationGroup.OnCreate.class},
            message = "Рейтинг фильма не может быть пустым.")
    private MpaRating mpa;

    private List<Genre> genres;

    @JsonIgnore
    private Set<Long> likes = new HashSet<>();
}