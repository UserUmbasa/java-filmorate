package ru.yandex.practicum.filmorate.model;
import jakarta.validation.constraints.*;
import lombok.Data;
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

    private Long id;

    @NotNull(message = "name фильма не может быть null")
    @Size(min = 1, max = 200, message = "имя фильма не может быть пустым")
    private String name;

    @NotNull(message = "description фильма не может быть null")
    @Size(min = 1, max = 200, message = "Длина описания должна быть от 1 до 200 символов")
    private String description;

    @MinDate(value = "1895-12-28", message = "Фильм не может быть раньше 1895-12-28")
    private LocalDate releaseDate;

    @Positive
    private Long duration;
}