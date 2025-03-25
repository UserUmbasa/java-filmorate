package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/users") // обработка пути
@Slf4j // private final static Logger log
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @GetMapping // get всех юзеров
    public Collection<User> findAll() { //возврат коллекции пользователей
        return users.values();
    }

    @PostMapping //post (новый юзер)
    public User create(@RequestBody User user) throws ValidationException { //не стал @Valid делать
        // Проверяем валидность пользователя
        checkDataValidation(user);
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
    public User update(@RequestBody User user) throws ValidationException { //не стал @Valid делать
        // проверяем необходимые условия
        if (users.containsKey(user.getId())) { // user с указанным идентификатором существует
            checkDataValidation(user); // валидация
            log.info("Обновлен элемент: {}", user);
            users.put(user.getId(), user);
            return user;
        }
        log.error("нельзя обновить пользователя с таким id: {}", user);
        throw new ValidationException("Пользователь с id = " + user.getId() + " не найден");
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

    // для валидации пользователя
    private void checkDataValidation(User user) throws ValidationException {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            // Если есть нарушения валидации, выводим ошибки
            for (ConstraintViolation<User> violation : violations) {
                throw new ValidationException("Ошибка валидации: " + violation.getPropertyPath() +
                        " - " + violation.getMessage());
            }
        }
    }
}
