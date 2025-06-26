package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmDtoMapper;
import ru.yandex.practicum.filmorate.mapper.GenreDtoMapper;
import ru.yandex.practicum.filmorate.mapper.MpaDtoMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.*;
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
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final RatingRepository ratingRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final FilmDtoMapper filmDtoMapper;
    private final Comparator<FilmDto> likesSizeDtoComparator =
            (film1, film2) -> Integer.compare(
                    film1.getLikes().size(),
                    film2.getLikes().size());
    private final Validator validator;

    public Collection<FilmDto> findAll() {
        return filmRepository.findAll()
                .stream()
                .map(filmDtoMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> findFilmLike(Integer count) {
        List<FilmDto> result = findAll().stream()
                .sorted(likesSizeDtoComparator.reversed())
                .toList();
        int actualCount = Math.min(count, result.size());
        return result.subList(0, actualCount);
    }

    public void updateFilm(FilmDto filmDto) {
        if(!checkFilmExists(filmDto.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не корректные ID фильма");
        }
        FilmDto existingFilm = findById(filmDto.getId());
        try {
            validator.validate(filmDto, Marker.OnCreate.class);
            existingFilm.setName(filmDto.getName());
            existingFilm.setDescription(filmDto.getDescription());
            existingFilm.setReleaseDate(filmDto.getReleaseDate());
            existingFilm.setDuration(filmDto.getDuration());
            log.info("Фильм успешно обновлен: {}", existingFilm);
        } catch (ConstraintViolationException e) {
            Map<String, String> errors = extractValidationErrors(e.getConstraintViolations());
            throw new ValidationException("Ошибки валидации", errors);
        }

        Film updatedFilm = FilmDtoMapper.mapToFilm(existingFilm);
        filmRepository.updateFilm(updatedFilm);
    }

    public void putLikeFilm(Long idFilm, Long idUser) {
        if(!checkFilmExists(idFilm) && !checkUserExists(idUser)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не корректные ID");
        }
        likeRepository.save(List.of(new Like(idUser,idFilm)));
    }

    public void deleteLikeFilm(Long idFilm, Long idUser) {
        if(!checkFilmExists(idFilm) && !checkUserExists(idUser)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не корректные ID");
        }
        likeRepository.delete(new Like(idFilm,idUser));
    }

    public FilmDto addFilm(FilmDto filmDto) {
        Film film;
        List<Genre> genres = new ArrayList<>();
        List<Like> likes = new ArrayList<>();
        film = FilmDtoMapper.mapToFilm(filmDto);
        if(filmDto.getMpa() != null  && !checkMpaExists(filmDto.getMpa().getId())) {
            throw  new NotFoundException("такого рейтинга нет");
        }
        film.setRatingId(filmDto.getMpa().getId());
        if (filmDto.getGenres() != null) {
            for (GenreDto genreDto : filmDto.getGenres()) {
                if(!checkGenreExists(genreDto.getId())){
                    throw  new NotFoundException("такого жанра нет");
                }
                genres.add(genreRepository.findById(genreDto.getId()));
            }
        }
        if(filmDto.getLikes() != null) {
            for (Long userId : filmDto.getLikes()) {
                if(!checkUserExists(userId)) {
                    throw  new NotFoundException("такого user нет");
                }
                User result = userRepository.findById(userId);
                likes.add(new Like(userId, film.getId()));
            }
        }
        // Сохранение
        Long id = filmRepository.save(film);
        genreRepository.save(genres, film.getId());
        likeRepository.save(likes);
        FilmDto dto = findById(id);
        log.info("Добавлен элемент: {}", filmDto);
        return dto;
    }

    public FilmDto findById(Long id) {
        if(!checkFilmExists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не корректный ID фильма");
        }
        return filmDtoMapper.mapToFilmDto(filmRepository.findById(id));
    }

    //-------------------MPA----------------------------------
    public Collection<MpaDto> findAllMpa() {
        return ratingRepository.findAll()
                .stream()
                .map(MpaDtoMapper::mapToMpaDTO)
                .collect(Collectors.toList());
    }

    public MpaDto findByMpaId(Long id) {
        return MpaDtoMapper.mapToMpaDTO(ratingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("MPA с id " + id + " не найден")));
    }

    //-------------------Genre----------------------------------
    public Collection<GenreDto> findAllGenres() {
        return genreRepository.findAll()
                .stream()
                .map(GenreDtoMapper::mapToGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto findByGenreId(Long id) {
        if(!checkGenreExists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не корректный ID жанра");
        }
        return GenreDtoMapper.mapToGenreDto(genreRepository.findById(id));
    }

    //-------------- вспомогательные методы ---------------
    public boolean checkFilmExists(Long filmId) {
        if(filmId == null) {
            return false;
        }
        return filmRepository.existsFilmById(filmId);
    }

    public boolean checkUserExists(Long userId) {
        if(userId == null) {
            return false;
        }
        return userRepository.existsUserById(userId);
    }

    public boolean checkMpaExists(Long mpaId) {
        return ratingRepository.existsMpaById(mpaId);
    }

    public boolean checkGenreExists(Long genreId) {
        if (genreId == null) {
            return false;
        }
        return genreRepository.existsGenreById(genreId);
    }

    private Map<String, String> extractValidationErrors(Set<ConstraintViolation<?>> violations) {
        return violations.stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        violation -> violation.getMessage()
                ));
    }
}
