package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RatingRepository {
    private final JdbcTemplate jdbc;
    private static final String CHECK_USER = """
    SELECT CASE 
        WHEN EXISTS (
           SELECT 1 
           FROM AGE_RATINGS 
           WHERE RATING_ID = ?
        ) THEN true 
        ELSE false END
    """;
    private static final String FIND_ALL_QUERY = """
    SELECT * FROM age_ratings ORDER BY rating_id
    """;
    private static final String FIND_BY_ID_QUERY = """
    SELECT * FROM age_ratings WHERE rating_id = ?
    """;
    private static final String FIND_RATING_NAME_ID_QUERY = """
    SELECT rating_name FROM age_ratings WHERE rating_id = ?
    """;

    public String getRatingNameById(Long ratingId) {
        return jdbc.queryForObject(FIND_RATING_NAME_ID_QUERY, new Object[]{ratingId}, String.class);
    }

    public Optional<MPA> findById(Long mpaId) {
        try {
            MPA result = jdbc.queryForObject(
                    FIND_BY_ID_QUERY,
                    new Object[]{mpaId},
                    (rs, rowNum) -> {
                        return new MPA(rs.getLong("rating_id"),rs.getString("rating_name"));
                    }
            );
            return Optional.of(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Collection<MPA> findAll() {
        return jdbc.query(
                FIND_ALL_QUERY,
                (rs, rowNum) -> mapMpa(rs)
        );
    }

    private MPA mapMpa(ResultSet rs) throws SQLException {
        return new MPA(rs.getLong("rating_id"),rs.getString("rating_name"));
    }

    public boolean existsMpaById(Long mpaId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(
                CHECK_USER,
                new Object[]{mpaId},
                Boolean.class
        ));
    }
}
