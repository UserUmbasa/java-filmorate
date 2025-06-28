package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.Marker;
import java.util.Collection;
import java.util.List;

@Validated
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Validated(Marker.OnCreate.class) @RequestBody FilmDto filmDto) {
        return filmService.addFilm(filmDto);
    }

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDto findFilm(@PathVariable Long id) {
        return filmService.findById(id);
    }

    @GetMapping("/popular")
    public List<FilmDto> findFilmLike(@RequestParam(value = "count", required = false) Integer count) {
        return filmService.findFilmLike(count);
    }

    @PutMapping
    public FilmDto update(@RequestBody FilmDto filmDto) {
        filmService.updateFilm(filmDto);
        return filmDto;
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<?> putLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.putLikeFilm(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<?> deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLikeFilm(id, userId);
        return ResponseEntity.ok().build();
    }
}