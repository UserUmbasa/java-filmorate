package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.time.LocalDate;

/**
 * Модель данных приложения.
 * целочисленный идентификатор — id;
 * электронная почта — email;
 * логин пользователя — login;
 * имя для отображения — name;
 * дата рождения — birthday.
 */
@Data
public class User {
    @NotNull(groups = Marker.OnUpdate.class, message = "Id не должен быть пустым")
    private Long id;

    @Email(groups = Marker.OnCreate.class, message = "Неверный формат email")
    @NotBlank(groups = Marker.OnCreate.class, message = "Email не может быть пустым или null")
    private String email;

    @NotBlank(groups = Marker.OnCreate.class, message = "login не должен содержать пустым или null")
    @Pattern(groups = Marker.OnCreate.class, regexp = "\\S+", message = "login должен быть слитным")
    private String login;

    private String name;

    @NotNull(groups = Marker.OnCreate.class, message = "Дата рождения не может быть null")
    @PastOrPresent(groups = Marker.OnCreate.class, message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;
}