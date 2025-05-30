package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.*;
import java.util.stream.Collectors;

/**
 * будет отвечать за операции с фильмами — добавление и удаление лайка, вывод 10 наиболее популярных
 * фильмов по количеству лайков
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final Validator validator;
    private final Comparator<Film> likesSizeComparator =
            (film1, film2) -> Integer.compare(
                    film1.getLikes().size(),
                    film2.getLikes().size());

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public Collection<Film> findFilmLike(Integer count) {
        if (count == null) {
            count = 10;
        }
        List<Film> sortedFilms = findAll().stream()
                .sorted(likesSizeComparator.reversed())
                .collect(Collectors.toList());
        int actualCount = Math.min(count, sortedFilms.size());
        return sortedFilms.subList(0, actualCount);
    }

    public void addFilm(Film film) {
        filmStorage.addFilm(film);
        log.info("Добавлен элемент: {}", film);
    }

    public void updateFilm(Film film) {
        Film filmUpdate = findById(film.getId());
        // проверка полей через validator
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
                filmUpdate.setName(film.getName());
            }

            if (!fieldErrors.containsKey("description")) {
                filmUpdate.setDescription(film.getDescription());
            }

            if (!fieldErrors.containsKey("releaseDate")) {
                filmUpdate.setReleaseDate(film.getReleaseDate());
            }

            if (!fieldErrors.containsKey("duration")) {
                filmUpdate.setDuration(film.getDuration());
            }
            log.info("Обновлен элемент частично: {}", filmUpdate);
        } else {
            //если все поля валидные, то обновляем все
            filmUpdate = film;
            log.info("Обновлен элемент: {}", film);
        }
    }

    public void putLikeFilm(Long idFilm, Long idUser) {
        User user = userService.findById(idUser);
        Film result = findById(idFilm);
        result.getLikes().add(user.getId());
    }

    public void deleteLikeFilm(Long idFilm, Long idUser) {
        User user = userService.findById(idUser);
        Film result = findById(idFilm);
        result.getLikes().remove(user.getId());
    }
}
