package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.LikeRepository;
import ru.yandex.practicum.filmorate.repository.RatingRepository;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class FilmDtoMapper {
    private final GenreRepository genreRepository;
    private final RatingRepository ratingRepository;
    private final LikeRepository likeRepository;
    public static Film mapToFilm(FilmDto filmDto) {
        Film film = new Film();
        if (filmDto.getId() != null) {
            film.setId(filmDto.getId());
        }
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());
        return film;
    }

    public FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        if (film.getRatingId() != 0) {
            dto.setMpa(new MPA(film.getRatingId(), ratingRepository.getRatingNameById(film.getRatingId())));
        }
        dto.setGenres(genreRepository.findGenresByFilmId(film.getId())
                .map(genres -> genres.stream()
                        .map(GenreDtoMapper::mapToGenreDto)
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .orElse(new LinkedHashSet<>())
        );
        dto.setLikes(likeRepository.findLikesByFilmId(film.getId()).orElse(Collections.emptyList()));
        return dto;
    }
}
