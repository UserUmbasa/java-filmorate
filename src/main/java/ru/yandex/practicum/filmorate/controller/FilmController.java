package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Validated
@RestController
@RequestMapping("/films") // обработка пути
@Slf4j // private final static Logger log
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    @Autowired
    private Validator validator;

    @GetMapping // get всех юзеров
    public Collection<Film> findAll() { //возврат коллекции пользователей
        return films.values();
    }

    @PostMapping //post (новый юзер)
    public Film create(@Validated(Marker.OnCreate.class) @RequestBody Film film) {
        film.setId(getNextId()); // автоматически устанавливает id
        // сохраняем новый фильм в памяти приложения
        log.info("Добавлен элемент: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Validated(Marker.OnUpdate.class) @RequestBody Film film) throws ValidationException {
        if (films.containsKey(film.getId())) {
            Film count = films.get(film.getId());
            // Валидируем user
            Set<ConstraintViolation<Film>> violations = validator.validate(film, Marker.OnCreate.class);

            // Если есть нарушения валидации, обрабатываем их
            if (!violations.isEmpty()) {
                // Создаем Map для хранения ошибок валидации по полям
                Map<String, String> fieldErrors = new HashMap<>();
                for (ConstraintViolation<Film> violation : violations) {
                    fieldErrors.put(violation.getPropertyPath().toString(), violation.getMessage());
                    log.error("Ошибка валидации: " + violation.getPropertyPath() +
                            " - " + violation.getMessage());
                }

                // Обновляем только те поля, для которых нет ошибок
                if (!fieldErrors.containsKey("name")) {
                    count.setName(film.getName());
                }

                if (!fieldErrors.containsKey("description")) {
                    count.setDescription(film.getDescription());
                }

                if (!fieldErrors.containsKey("releaseDate")) {
                    count.setReleaseDate(film.getReleaseDate());
                }

                if (!fieldErrors.containsKey("duration")) {
                    count.setDuration(film.getDuration());
                }
                log.info("Обновлен элемент частично: {}", count);
                return count;
            } else {
                //если все поля валидные то обновляем все
                films.put(film.getId(), film);
                log.info("Обновлен элемент: {}", film);
                return film;
            }
        }
        log.error("User update не выполнен - Не валидный Id");
        throw new ValidationException("User update не выполнен - Не валидный Id");
    }

    //----------------------вспомогательные методы----------------------

    // для генерации идентификатора нового фильма
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}