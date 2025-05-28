package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.*;
import java.util.stream.Collectors;

/**
 * будет отвечать за операции с фильмами — добавление и удаление лайка, вывод 10 наиболее популярных
 * фильмов по количеству лайков
 */
@Service
@Slf4j // private final static Logger log
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final Validator validator;
    private final Comparator<Film> likesSizeComparator =
            (film1, film2) -> Integer.compare(
                    film1.getLikes().size(),
                    film2.getLikes().size());

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, Validator validator, UserService userService) {
        this.filmStorage = filmStorage;
        this.validator = validator;
        this.userService = userService;
    }

    //возврат коллекции фильмов
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    //возврат фильма по айди
    public Film findById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public Collection<Film> findFilmLike(Integer count){
        if (count == null) {
            count = 10;
        }
        List<Film> sortedFilms = findAll().stream()
                .sorted(likesSizeComparator.reversed())
                .collect(Collectors.toList());
        int actualCount = Math.min(count, sortedFilms.size());
        Collection<Film> result = sortedFilms.subList(0, actualCount);
        return result;
    }

    //добавление фильма
    public void addFilm(Film film) {
        filmStorage.addFilm(film);
    }

    //обновление фильма
    public void updateFilm(Film film) {
        Film filmUpdate = findById(film.getId());
        // Валидируем
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

    // поставить лайк (предположу, что айди пользователя существует)
    public void putLikeFilm(Long idFilm, Long idUser) {
        User user = userService.findById(idUser);
        Film result = findById(idFilm);
        result.getLikes().add(user.getId());
    }

    // пользователь удаляет лайк
    public void deleteLikeFilm(Long idFilm, Long idUser) {
        User user = userService.findById(idUser);
        Film result = findById(idFilm);
        result.getLikes().remove(user.getId());
    }
}
