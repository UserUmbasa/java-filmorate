package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.time.LocalDate;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс для тестирования валидации User объекта
 */
@SpringBootTest
class UserValidationTest {

    @Autowired
    private Validator validator;
    private User validUser;

    /**
     * Метод, который выполняется перед каждым тестом
     */
    @BeforeEach
    void setUp() {
        validUser = new User(); // валидная сущность
        validUser.setId(1L);
        validUser.setEmail("test@example.com");
        validUser.setLogin("testUser");
        validUser.setName("John Doe");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }

    /**
     * Тестирование валидного пользователя
     */
    @Test
    void testValidUser() {
        // Используем Set<ConstraintViolation> для получения информации о нарушениях
        Set<ConstraintViolation<User>> violations = validator.validate(validUser, Marker.OnCreate.class);
        assertTrue(violations.isEmpty(), "Должны отсутствовать нарушения валидации для валидного пользователя");
    }

    /**
     * Тестирование некорректного email
     */
    @Test
    void testInvalidEmail() {
        validUser.setEmail("invalid_email");
        Set<ConstraintViolation<User>> violations = validator.validate(validUser, Marker.OnCreate.class);
        assertEquals(1, violations.size(), "Должно быть одно нарушение");
        assertEquals("Неверный формат email", violations.iterator().next().getMessage(),
                "Сообщение должно соответствовать ожидаемому");
    }

    /**
     * Тестирование null email
     */
    @Test
    void testNullEmail() {
        validUser.setEmail(null);
        Set<ConstraintViolation<User>> violations = validator.validate(validUser, Marker.OnCreate.class);
        assertEquals(1, violations.size(), "Должно быть одно нарушение");
        assertEquals("Email не может быть пустым или null", violations.iterator().next().getMessage(),
                "Сообщение должно соответствовать ожидаемому");
    }

    /**
     * Тестирование некорректного логина (с пробелами)
     */
    @Test
    void testInvalidLogin() {
        validUser.setLogin("test login");
        Set<ConstraintViolation<User>> violations = validator.validate(validUser, Marker.OnCreate.class);
        assertEquals(1, violations.size(), "Должно быть одно нарушение");
        assertEquals("login должен быть слитным", violations.iterator().next().getMessage(),
                "Сообщение должно соответствовать ожидаемому");
    }

    /**
     * Тестирование null даты рождения
     */
    @Test
    void testNullBirthday() {
        validUser.setBirthday(null);
        Set<ConstraintViolation<User>> violations = validator.validate(validUser, Marker.OnCreate.class);
        assertEquals(1, violations.size(), "Должно быть одно нарушение");
        assertEquals("Дата рождения не может быть null", violations.iterator().next().getMessage(),
                "Сообщение должно соответствовать ожидаемому");
    }

    @Test
    void testFutureBirthday() {
        validUser.setBirthday(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<User>> violations = validator.validate(validUser, Marker.OnCreate.class);
        assertEquals(1, violations.size());
        assertEquals("Дата рождения должна быть в прошлом", violations.iterator().next().getMessage());
    }
}

