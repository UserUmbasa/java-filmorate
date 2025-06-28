package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FriendShipsRepository {
    private final JdbcTemplate jdbc;
    private static final String ADD_FRIENDS = """
    INSERT INTO friendships (requester_id, addressee_id, status) VALUES (?, ?, ?)
    """;
    private static final String CHECK_REVERSE_FRIENDSHIP = """
    SELECT status FROM friendships WHERE requester_id = ? AND addressee_id = ?
    """;
    private static final String UPDATE_STATUS = """
    UPDATE friendships SET status = ? WHERE requester_id = ? AND addressee_id = ?
    """;
    private static final String FIND_USER_FRIENDS = """
    SELECT u.user_id, u.email, u.login, u.name, u.birthday\s
    FROM users u\s
    WHERE u.user_id IN (\s
       SELECT addressee_id FROM friendships WHERE requester_id = ?\s
       UNION\s
       SELECT requester_id FROM friendships WHERE addressee_id = ? AND status = 'confirmed'\s
    )
   \s""";
    private static final String FIND_MUTUAL_FRIENDS = """
    SELECT u.user_id, u.email, u.login, u.name, u.birthday\s
    FROM users u\s
    WHERE u.user_id IN (\s
       SELECT addressee_id FROM friendships WHERE requester_id = ?\s
       UNION\s
       SELECT requester_id FROM friendships WHERE addressee_id = ? AND status = 'confirmed'\s
    )\s
    AND u.user_id IN (\s
       SELECT addressee_id FROM friendships WHERE requester_id = ?\s
       UNION\s
       SELECT requester_id FROM friendships WHERE addressee_id = ? AND status = 'confirmed'\s
    )
   \s""";
    private static final String REMOVE_FRIENDS = """
    DELETE FROM friendships\s
    WHERE (requester_id = ? AND addressee_id = ?)\s
    OR (requester_id = ? AND addressee_id = ? AND status = 'confirmed')
   \s""";

    public Optional<List<User>> findUserFriends(Long userId) {
        try {
            List<User> users = jdbc.query(
                    FIND_USER_FRIENDS,
                    new Object[]{userId, userId},
                    (rs, rowNum) -> {
                        User user = new User();
                        user.setId(rs.getLong("user_id"));
                        user.setEmail(rs.getString("email"));
                        user.setLogin(rs.getString("login"));
                        user.setName(rs.getString("name"));
                        java.sql.Date birthday = rs.getDate("birthday");
                        user.setBirthday(birthday != null ? birthday.toLocalDate() : null);
                        return user;
                    }
            );
            return Optional.of(users);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<User> findMutualFriends(Long id, Long otherId) {
        try {
            return jdbc.query(FIND_MUTUAL_FRIENDS, (rs, rowNum) -> {
                User user = new User();
                user.setBirthday(rs.getDate("birthday").toLocalDate());
                user.setEmail(rs.getString("email"));
                user.setLogin(rs.getString("login"));
                user.setName(rs.getString("name"));
                user.setId(rs.getLong("user_id"));
                return user;
            }, id, id, otherId, otherId);
        } catch (Exception e) {
            return List.of();
        }
    }

    public void addFriends(Long userId, Long friendId) {
        String existingStatus = getFriendshipStatus(friendId, userId, jdbc);
        if (existingStatus != null) {
            confirmFriendship(userId, friendId, jdbc);
            confirmFriendship(friendId, userId, jdbc);
        } else {
            jdbc.update(ADD_FRIENDS, userId, friendId, "pending");
        }
    }

    public void removeFriend(Long id, Long friendId) {
        jdbc.update(REMOVE_FRIENDS, id, friendId, friendId, id);
    }

    private static String getFriendshipStatus(Long requesterId, Long addresseeId, JdbcTemplate jdbc) {
        try {
            return jdbc.queryForObject(CHECK_REVERSE_FRIENDSHIP, String.class, requesterId, addresseeId);
        } catch (EmptyResultDataAccessException e) {
            // Запись не найдена
            return null;
        }
    }

    private static void confirmFriendship(Long userId, Long friendId, JdbcTemplate jdbc) {
        jdbc.update(UPDATE_STATUS, "confirmed", userId, friendId);
    }
}
