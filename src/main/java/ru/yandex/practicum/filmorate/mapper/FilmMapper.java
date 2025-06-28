package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.repository.LikeRepository;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.RatingRepository;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
// @RequiredArgsConstructor не работает с mapstruct
public abstract class FilmMapper {
    @Autowired
    protected LikeRepository likeRepository;
    @Autowired
    protected RatingRepository mpaRepository;
    @Autowired
    protected GenreRepository genreRepository;
    @Autowired
    protected GenreMapper genreMapper;

    @Mappings({
            @Mapping(target = "ratingId", source = "mpa.id")
    })
    public abstract Film mapToFilm(FilmDto filmDto);

    @Mappings({
            @Mapping(target = "likes", expression = "java(mapLikes(film))"),
            @Mapping(target = "mpa", expression = "java(mapMpa(film))"),
            @Mapping(target = "genres", expression = "java(mapGenres(film))")
    })
    public abstract FilmDto mapToFilmDto(Film film);

    protected List<Long> mapLikes(Film film) {
        return likeRepository.findLikesByFilmId(film.getId()).orElse(Collections.emptyList());
    }

    protected MPA mapMpa(Film film) {
        return mpaRepository.findById(film.getRatingId()).orElse(null);
    }

    protected LinkedHashSet<GenreDto> mapGenres(Film film) {
        return genreRepository.findGenresByFilmId(film.getId())
                .map(genres -> genres.stream()
                        .map(genreMapper::mapToGenreDto)
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .orElse(new LinkedHashSet<>());
    }
}

