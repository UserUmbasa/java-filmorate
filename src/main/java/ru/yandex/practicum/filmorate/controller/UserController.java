package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.*;

@Validated
@RestController
@RequestMapping("/users") // обработка пути
@Slf4j // private final static Logger log
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    @Autowired
    private Validator validator;

    @GetMapping // get всех юзеров
    public Collection<User> findAll() { //возврат коллекции пользователей
        return users.values();
    }

    @PostMapping //post (новый юзер)
    public User create(@Validated(Marker.OnCreate.class) @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Логин присвоен имени  {}", user.getLogin());
        }
        user.setId(getNextId()); // автоматически устанавливает id
        // сохраняем нового юзера в памяти приложения
        log.info("Добавлен элемент: {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Validated(Marker.OnUpdate.class) @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            User count = users.get(user.getId());
            // Валидируем user
            Set<ConstraintViolation<User>> violations = validator.validate(user, Marker.OnCreate.class);

            // Если есть нарушения валидации, обрабатываем их
            if (!violations.isEmpty()) {
                // Создаем Map для хранения ошибок валидации по полям
                Map<String, String> fieldErrors = new HashMap<>();
                for (ConstraintViolation<User> violation : violations) {
                    fieldErrors.put(violation.getPropertyPath().toString(), violation.getMessage());
                    log.error("Ошибка валидации: " + violation.getPropertyPath() +
                            " - " + violation.getMessage());
                }

                // Обновляем только те поля, для которых нет ошибок
                if (!fieldErrors.containsKey("email")) {
                    count.setEmail(user.getEmail());
                }

                if (!fieldErrors.containsKey("login")) {
                    count.setLogin(user.getLogin());
                }

                if (!fieldErrors.containsKey("name")) {
                    count.setName(user.getName());
                }

                if (!fieldErrors.containsKey("birthday")) {
                    count.setBirthday(user.getBirthday());
                }
                log.info("Обновлен элемент частично: {}", count);
                return count;
            } else {
                //если все поля валидные то обновляем все
                log.info("Обновлен элемент: {}", user);
                users.put(user.getId(), user);
                return user;
            }
        }
        log.error("User update не выполнен - Не валидный Id");
        throw new ValidationException("User update не выполнен - Не валидный Id");
    }



    //----------------------вспомогательные методы----------------------

    // для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
