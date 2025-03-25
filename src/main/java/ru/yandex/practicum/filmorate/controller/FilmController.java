package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/films") // обработка пути
@Slf4j // private final static Logger log
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @GetMapping // get всех юзеров
    public Collection<Film> findAll() { //возврат коллекции пользователей
        return films.values();
    }

    @PostMapping //post (новый юзер)
    public Film create(@RequestBody Film film) throws ValidationException {
        checkDataValidation(film); // валидация
        film.setId(getNextId()); // автоматически устанавливает id
        // сохраняем новый фильм в памяти приложения
        log.info("Добавлен элемент: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ValidationException {
        // проверяем необходимые условия
        if (films.containsKey(film.getId())) { // user с указанным идентификатором существует
            checkDataValidation(film); // валидация
            log.info("Обновлен элемент: {}", film);
            films.put(film.getId(), film);
            return film;
        }
        log.error("нельзя обновить фильм с таким id: {}", film);
        throw new ValidationException("Фильм с id = " + film.getId() + " не найден");
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

    // для валидации пользователя
    private void checkDataValidation(Film film) throws ValidationException {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            // Если есть нарушения валидации, выводим ошибки
            for (ConstraintViolation<Film> violation : violations) {
                throw new ValidationException("Ошибка валидации: " + violation.getPropertyPath() +
                        " - " + violation.getMessage());
            }
        }
    }
}