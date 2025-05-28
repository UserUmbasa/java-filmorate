package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.Marker;

import java.util.*;

/**
 * будет отвечать за такие операции с пользователями, как добавление в друзья, удаление из друзей,
 * вывод списка общих друзей
 */
@Service
@Slf4j // private final static Logger log
public class UserService {
    private final UserStorage userStorage;
    private final Validator validator;

    @Autowired
    public UserService(InMemoryUserStorage userStorage, Validator validator) {
        this.userStorage = userStorage;
        this.validator = validator;
    }

    //возврат коллекции пользователей
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    //возврат пользователя по айди
    public User findById(Long id) {
        // log.error("User update не выполнен - Не валидный Id");
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    // возвращаем список пользователей, являющихся его друзьями по айди
    public List<User> findUserFriends(Long id) {
        List<User> usersFriends = new ArrayList<>();
        User user = findById(id);
        for (Long ids : user.getFriends()) {
            usersFriends.add(findById(ids));
        }
        return usersFriends;
    }

    // возврат списка друзей, общих с другим пользователем.
    public List<User> findMutualFriends(Long id, Long otherId) {
        Set<Long> set1 = findById(id).getFriends();
        Set<Long> set2 = findById(otherId).getFriends();
        Set<Long> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        List<User> result = new ArrayList<>();
        for (User user : userStorage.findAll()) {
            if (intersection.contains(user.getId())) {
                result.add(user);
            }
        }
        return result;
    }

    // добавление пользователя
    public void addUser(User user) {
        userStorage.addUser(user);
    }

    // добавление друга по айди
    public void addFriends(Long id, Long friendId) {
        User user1 = findById(id);
        User user2 = findById(friendId);
        user1.getFriends().add(friendId);
        user2.getFriends().add(id);
    }

    // обновление данных пользователя
    public void updateUser(User user) {
        User userUpdate = findById(user.getId());
        // Валидация user
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
                userUpdate.setEmail(user.getEmail());
            }

            if (!fieldErrors.containsKey("login")) {
                userUpdate.setLogin(user.getLogin());
            }

            if (!fieldErrors.containsKey("name")) {
                userUpdate.setName(user.getName());
            }

            if (!fieldErrors.containsKey("birthday")) {
                userUpdate.setBirthday(user.getBirthday());
            }
            log.info("Обновлен элемент частично: {}", userUpdate);
        } else {
            //если все поля валидные, то обновляем все
            userUpdate = user;
            log.info("Обновлен элемент: {}", user);
        }
    }

    // удаление из друзей по айди
    public void removeFriend(Long id, Long friendId) {
        User user1 = findById(id);
        User user2 = findById(friendId);
        user1.getFriends().remove(friendId);
        user2.getFriends().remove(id);
    }
}
