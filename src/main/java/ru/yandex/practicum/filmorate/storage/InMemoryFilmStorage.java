package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

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
