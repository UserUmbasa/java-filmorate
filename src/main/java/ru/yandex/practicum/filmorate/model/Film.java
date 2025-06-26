package ru.yandex.practicum.filmorate.model;

import lombok.Data;
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
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Long ratingId;
}