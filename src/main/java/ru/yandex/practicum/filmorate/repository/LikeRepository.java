package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LikeRepository {
    private final JdbcTemplate jdbc;
    private static final String FIND_LIKES_BY_FILM_ID = """
    SELECT user_id FROM film_likes WHERE film_id = ?
    """;
    private static final String INSERT_QUERY = """
    INSERT INTO film_likes(user_id, film_id)
    VALUES (?, ?)
    """;
    private static final String DELETE_QUERY = """
    DELETE FROM film_likes WHERE user_id = ? AND film_id = ?
    """;


    public void save(List<Like> likes) {
        for (Like like : likes) {
            jdbc.update(INSERT_QUERY,
                    like.getIdUser(),
                    like.getIdFIlm()
            );
        }
    }

    public Optional<List<Long>> findLikesByFilmId(Long filmId) {
        try {
            List<Long> likes = jdbc.query(
                    FIND_LIKES_BY_FILM_ID,
                    new Object[]{filmId},
                    (rs, rowNum) -> rs.getLong("user_id")
            );
            return Optional.of(likes);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void delete(Like like) {
        jdbc.update(
                DELETE_QUERY,
                like.getIdUser(),
                like.getIdFIlm()
        );
    }
}
