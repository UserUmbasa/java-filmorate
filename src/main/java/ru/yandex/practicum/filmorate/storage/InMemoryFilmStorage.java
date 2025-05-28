package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.*;

@Slf4j // private final static Logger log
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    @Autowired
    private Validator validator;

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    // возврат фильма по айди
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    //добавление фильма
    @Override
    public void addFilm(Film film) {
        film.setId(getNextId());
        log.info("Добавлен элемент: {}", film);
        films.put(film.getId(), film);
    }

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
