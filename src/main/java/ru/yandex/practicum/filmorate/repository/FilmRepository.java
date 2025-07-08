package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import java.sql.PreparedStatement;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class FilmRepository {
    private final JdbcTemplate jdbc;
    private static final String FIND_ALL_QUERY = """
    SELECT * FROM films
    """;
    private static final String CHECK_FILM = """
    SELECT CASE WHEN EXISTS (SELECT 1 FROM films WHERE film_id = ?) THEN true ELSE false END
    """;
    private static final String FIND_BY_ID_QUERY = """
    SELECT * FROM films WHERE film_id = ?
    """;
    private static final String INSERT_QUERY = """
    INSERT INTO films(name, description, release_date, duration, rating_Id)
    VALUES (?, ?, ?, ?, ?)
    """;
    private static final String UPDATE_QUERY = """
    UPDATE films\s
    SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?\s
    WHERE film_id = ?
   \s""";


    public Long save(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, new String[]{"film_id"});
                    ps.setString(1, film.getName());
                    ps.setString(2, film.getDescription());
                    ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
                    ps.setLong(4, film.getDuration());
                    ps.setLong(5, film.getRatingId());
                    return ps;
                },
                keyHolder
        );
        Long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);
        return filmId;
    }

    public Film findById(Long id) {
        return jdbc.queryForObject(
                FIND_BY_ID_QUERY,
                new Object[]{id},
                (rs, rowNum) -> {
                    Film filmInstance = new Film();
                    filmInstance.setId(rs.getLong("film_id"));
                    filmInstance.setName(rs.getString("name"));
                    filmInstance.setDescription(rs.getString("description"));
                    filmInstance.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    filmInstance.setDuration(rs.getLong("duration"));
                    filmInstance.setRatingId(rs.getLong("rating_id"));
                    return filmInstance;
                }
        );
    }

    public Collection<Film> findAll() {
        return jdbc.query(
                FIND_ALL_QUERY,
                (rs, rowNum) -> {
                    Film film = new Film();
                    film.setId(rs.getLong("film_id"));
                    film.setName(rs.getString("name"));
                    film.setDescription(rs.getString("description"));
                    film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    film.setDuration(rs.getLong("duration"));
                    film.setRatingId(rs.getLong("rating_id"));
                    return film;
                }
        );
    }

    public void updateFilm(Film film) {
        jdbc.update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getRatingId(),
                film.getId()
        );
    }

    public boolean existsFilmById(Long filmId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(
                CHECK_FILM,
                new Object[]{filmId},
                Boolean.class
        ));
    }
}
