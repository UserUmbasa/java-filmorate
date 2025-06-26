package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.Collection;

@Validated
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final FilmService filmService;

    @GetMapping
    public Collection<MpaDto> findAll() {
        return filmService.findAllMpa();
    }

    @GetMapping("/{id}")
    public MpaDto findByMpaId(@PathVariable Long id) {
        return filmService.findByMpaId(id);
    }
}
