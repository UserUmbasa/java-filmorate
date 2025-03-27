package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films") // обработка пути
@Slf4j // private final static Logger log
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping // get всех юзеров
    public Collection<Film> findAll() { //возврат коллекции пользователей
        return films.values();
    }

    @PostMapping //post (новый юзер)
    public Film create(@Valid @RequestBody Film film) { //@Valid валидация
        film.setId(getNextId()); // автоматически устанавливает id
        // сохраняем новый фильм в памяти приложения
        log.info("Добавлен элемент: {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) throws ValidationException {
        // проверяем необходимые условия
        if (films.containsKey(film.getId())) { // user с указанным идентификатором существует
            log.info("Обновлен элемент: {}", film);
            films.put(film.getId(), film);
            return film;
        }
        log.error("Film update не выполнен - Не валидный Id");
        throw new ValidationException("Film update не выполнен - Не валидный Id");
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