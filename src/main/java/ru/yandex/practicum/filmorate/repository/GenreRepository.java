package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreRepository {
    private final JdbcTemplate jdbc;
    private static final String CHECK_GENRE = """
    SELECT CASE 
        WHEN EXISTS (
           SELECT 1 
           FROM genres 
           WHERE genre_id = ?
        ) THEN true 
        ELSE false END
    """;
    private static final String FIND_GENRES_BY_FILM_ID =  """
    SELECT g.genre_id, g.name
    FROM genres g
    JOIN film_genres fg ON g.genre_id = fg.genre_id
    WHERE fg.film_id = ?
    """;
    private static final String INSERT_QUERY = """
    INSERT INTO FILM_GENRES(film_id, genre_id)
    VALUES (?, ?)
    """;
    private static final String FIND_ALL_QUERY = """
    SELECT * FROM GENRES ORDER BY genre_id
    """;
    private static final String FIND_BY_ID_QUERY = """
    SELECT * FROM GENRES WHERE genre_id = ?
    """;

    public Collection<Genre> findAll() {
        return jdbc.query(
                FIND_ALL_QUERY,
                (rs, rowNum) -> mapGenre(rs)
        );
    }

    public Genre findById(Long genreId) {
        return jdbc.queryForObject(
                FIND_BY_ID_QUERY,
                new Object[]{genreId},
                (rs, rowNum) -> new Genre(rs.getLong("genre_id"), rs.getString("name"))
        );
    }

    private Genre mapGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getLong("genre_id"),rs.getString("name"));
    }

    public void save(List<Genre> genres, Long filmId) {
        for (Genre genre : genres) {
            jdbc.update(INSERT_QUERY,
                    filmId,
                    genre.getId()
            );
        }
    }

    public Optional<List<Genre>> findGenresByFilmId(Long filmId) {
        try {
            List<Genre> genres = jdbc.query(
                    FIND_GENRES_BY_FILM_ID,
                    new Object[]{filmId},
                    (rs, rowNum) -> {
                        return new Genre(rs.getLong("genre_id"), rs.getString("name"));
                    }
            );
            return Optional.of(genres);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsGenreById(Long genreId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(
                CHECK_GENRE,
                new Object[]{genreId},
                Boolean.class
        ));
    }
}
