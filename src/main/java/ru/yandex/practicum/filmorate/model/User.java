package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotations.ValidationGroup;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    @Null(groups = ValidationGroup.OnCreate.class, message = "ID должно быть пустым при создании.")
    @NotNull(groups = ValidationGroup.OnUpdate.class, message = "ID должно быть указано при обновлении.")
    private Long id;

    @NotBlank(groups = {ValidationGroup.OnCreate.class}, message = "Email должен быть указан.")
    @Email(groups = {ValidationGroup.OnCreate.class, ValidationGroup.OnUpdate.class},
            message = "Email должен содержать символ @.")
    private String email;

    @NotBlank(groups = {ValidationGroup.OnCreate.class}, message = "Логин не может быть пустым.")
    @Pattern(groups = {ValidationGroup.OnCreate.class, ValidationGroup.OnUpdate.class}, regexp = "\\S+",
            message = "Логин не может содержать пробелы.")
    private String login;

    private String name;

    @NotNull(groups = {ValidationGroup.OnCreate.class}, message = "Дата рождения не заполнена.")
    @PastOrPresent(groups = {ValidationGroup.OnCreate.class, ValidationGroup.OnUpdate.class},
            message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;

    @JsonIgnore
    private Set<Long> friends = new HashSet<>();
}