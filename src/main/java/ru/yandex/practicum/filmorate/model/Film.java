package ru.yandex.practicum.filmorate.model;
import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.Marker;
import ru.yandex.practicum.filmorate.validator.MinDate;
import java.time.LocalDate;

/**
 * Модель данных приложения.
 * целочисленный идентификатор — id;
 * название — name;
 * описание — description;
 * дата релиза — releaseDate;
 * продолжительность фильма — duration.
 */
@Data
public class Film {

    @NotNull(groups = Marker.OnUpdate.class, message = "Id не должен быть пустым")
    private Long id;

    @NotBlank(groups = Marker.OnCreate.class, message = "name фильма не может быть пустым или null")
    private String name;

    @NotBlank(groups = Marker.OnCreate.class, message = "description фильма не может быть пустым или null")
    @Size(groups = Marker.OnCreate.class, min = 1, max = 200, message = "Длина описания должна быть от 1 до 200 символов")
    private String description;

    @MinDate(groups = Marker.OnCreate.class, value = "1895-12-28", message = "Фильм не может быть раньше 1895-12-28")
    private LocalDate releaseDate;

    @Positive(groups = Marker.OnCreate.class)
    private Long duration;
}