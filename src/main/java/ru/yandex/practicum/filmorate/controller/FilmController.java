package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.Marker;
import java.util.Collection;

@Validated
@RestController
@RequestMapping("/films") // обработка пути
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    //возврат коллекции фильмов
    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    //возврат фильма по айди
    @GetMapping("/{id}")
    public Film findFilm(@PathVariable Long id) {
        return filmService.findById(id);
    }

    //возвращает список из первых count фильмов по количеству лайков
    @GetMapping("/popular")
    public Collection<Film> findFilmLike(@RequestParam(value = "count", required = false) Integer count) {
        return filmService.findFilmLike(count);
    }

    //добавление фильма
    @PostMapping
    public Film create(@Validated(Marker.OnCreate.class) @RequestBody Film film) {
        filmService.addFilm(film);
        return film;
    }

    // обновление фильма (не пропускаю только не валидный айди)
    @PutMapping
    public Film update(@Validated(Marker.OnUpdate.class) @RequestBody Film film) throws ValidationException {
        filmService.updateFilm(film);
        return film;
    }

    // пользователь ставит лайк фильму
    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<?> putLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.putLikeFilm(id, userId);
        return ResponseEntity.ok().build();
    }

    // пользователь удаляет лайк
    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<?> deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLikeFilm(id, userId);
        return ResponseEntity.ok().build();
    }
}