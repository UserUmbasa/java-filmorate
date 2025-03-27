package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users") // обработка пути
@Slf4j // private final static Logger log
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping // get всех юзеров
    public Collection<User> findAll() { //возврат коллекции пользователей
        return users.values();
    }

    @PostMapping //post (новый юзер)
    public User create(@Valid @RequestBody User user) { //@Valid валидация
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
    public User update(@Valid @RequestBody User user) { //@Valid валидация
        // проверяем необходимые условия
        if (users.containsKey(user.getId())) { // user с указанным идентификатором существует
            log.info("Обновлен элемент: {}", user);
            users.put(user.getId(), user);
            return user;
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
