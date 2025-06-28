package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.PreparedStatement;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbc;
    private static final String FIND_ALL_QUERY = """
    SELECT user_id, email, login, name, birthday FROM users
    """;
    private static final String CHECK_USER = """
    SELECT CASE 
        WHEN EXISTS (
           SELECT 1 
           FROM users 
           WHERE user_id = ?
        ) THEN true 
        ELSE false END
    """;
    private static final String FIND_BY_ID_QUERY = """
    SELECT * FROM users WHERE user_id = ?
    """;
    private static final String INSERT_QUERY = """
    INSERT INTO users(email, login, name, birthday)
    VALUES (?, ?, ?, ?)
    """;
    private static final String UPDATE_USER = """
    UPDATE users 
    SET email = ?, login = ?, name = ?, birthday = ? 
    WHERE user_id = ?
    """;


    public Long save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, new String[]{"user_id"});
                    ps.setString(1, user.getEmail());
                    ps.setString(2, user.getLogin());
                    ps.setString(3, user.getName());
                    ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
                    return ps;
                },
                keyHolder
        );
        Long userId = keyHolder.getKey().longValue();
        user.setId(userId);
        return userId;
    }

    public void updateUser(User user) {
        jdbc.update(UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                java.sql.Date.valueOf(user.getBirthday()),
                user.getId()
        );
    }

    public User findById(Long userId) {
        return jdbc.queryForObject(
                FIND_BY_ID_QUERY,
                new Object[]{userId},
                (rs, rowNum) -> {
                    User userInstance = new User();
                    userInstance.setId(rs.getLong("user_id"));
                    userInstance.setEmail(rs.getString("email"));
                    userInstance.setLogin(rs.getString("login"));
                    userInstance.setName(rs.getString("name"));
                    userInstance.setBirthday(rs.getDate("birthday").toLocalDate());
                    return userInstance;
                });
    }

    public boolean existsUserById(Long userId) {
        return Boolean.TRUE.equals(jdbc.queryForObject(
                CHECK_USER,
                new Object[]{userId},
                Boolean.class
        ));
    }

    public Collection<User> findAll() {
        return jdbc.query(
                FIND_ALL_QUERY,
                (rs, rowNum) -> {
                    User user = new User();
                    user.setId(rs.getLong("user_id"));
                    user.setEmail(rs.getString("email"));
                    user.setLogin(rs.getString("login"));
                    user.setName(rs.getString("name"));
                    user.setBirthday(rs.getDate("birthday").toLocalDate());
                    return user;
                }
        );
    }
}
