package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.time.LocalDate;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmValidationTest {

    @Autowired
    private Validator validator;
    private FilmDto validFilm;

    @BeforeEach
    void setUp() {
        validFilm = new FilmDto();
        validFilm.setName("Test Film");
        validFilm.setDescription("Описание фильма");
        validFilm.setReleaseDate(LocalDate.of(1900, 1, 1));
        validFilm.setDuration(120L);
    }

    @Test
    void testValidFilm() {
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(validFilm, Marker.OnCreate.class);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullName() {
        validFilm.setName(null);
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(validFilm, Marker.OnCreate.class);
        assertEquals(1, violations.size());
        assertEquals("name фильма не может быть пустым или null", violations.iterator().next().getMessage());
    }

    @Test
    void testEmptyName() {
        validFilm.setName("");
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(validFilm, Marker.OnCreate.class);
        assertEquals(1, violations.size());
        assertEquals("name фильма не может быть пустым или null", violations.iterator().next().getMessage());
    }

    @Test
    void testTooLongDescription() {
        validFilm.setDescription("a".repeat(201));
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(validFilm, Marker.OnCreate.class);
        assertEquals(1, violations.size());
        assertEquals("Длина описания должна быть от 1 до 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void testReleaseDateBefore1895() {
        validFilm.setReleaseDate(LocalDate.of(1895, 1, 1));
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(validFilm, Marker.OnCreate.class);
        assertEquals(1, violations.size());
        assertEquals("Фильм не может быть раньше 1895-12-28", violations.iterator().next().getMessage());
    }

    @Test
    void testNegativeDuration() {
        validFilm.setDuration(-1L);
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(validFilm, Marker.OnCreate.class);
        assertEquals(1, violations.size());
    }

    @Test
    void testZeroDuration() {
        validFilm.setDuration(0L);
        Set<ConstraintViolation<FilmDto>> violations = validator.validate(validFilm, Marker.OnCreate.class);
        assertEquals(1, violations.size());
    }
}

