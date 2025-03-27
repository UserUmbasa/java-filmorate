package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
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

    private Long id;

    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email не может быть пустым или null")
    private String email;

    @NotBlank(message = "login не должен содержать пустым или null")
    @Pattern(regexp = "\\S+", message = "login должен быть слитным")
    private String login;

    private String name;

    @NotNull(message = "Дата рождения не может быть null")
    @PastOrPresent(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;
}