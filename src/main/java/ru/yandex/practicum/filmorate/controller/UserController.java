package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validator.Marker;
import java.util.*;

@Validated
@RestController
// @RequiredArgsConstructor автоинжентинг - пока не хочу использовать
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //возврат коллекции пользователей
    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    //возврат пользователя по айди
    @GetMapping("/{id}")
    public User findUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    //  возвращаем список пользователей, являющихся его друзьями по айди
    @GetMapping("/{id}/friends")
    public List<User> findUserFriends(@PathVariable Long id) {
        return userService.findUserFriends(id);
    }

    // возврат списка друзей, общих с другим пользователем
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.findMutualFriends(id, otherId);
    }

    // добавление пользователя
    @PostMapping
    public User create(@Validated(Marker.OnCreate.class) @RequestBody User user) {
        userService.addUser(user);
        return user;
    }

    // обновление данных пользователя
    @PutMapping
    public User update(@Validated(Marker.OnUpdate.class) @RequestBody User user) {
        userService.updateUser(user);
        // return userService.findById(user.getId()); для самопроверки
        return user;
    }

    //добавляем пользователю (id) друга с friendId и наоборот
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.addFriends(id, friendId);
        return ResponseEntity.ok().build();
    }

    //удаляем у пользователя (id) друга с friendId и наоборот
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id, friendId);
        return ResponseEntity.ok().build();
    }
}
