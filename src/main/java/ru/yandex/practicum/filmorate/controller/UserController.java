package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validator.Marker;
import java.util.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> findUserFriends(@PathVariable Long id) {
        return userService.findUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> findMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.findMutualFriends(id, otherId);
    }

    @PostMapping
    public UserDto create(@Validated(Marker.OnCreate.class) @RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @PutMapping
    public UserDto update(@RequestBody UserDto userDto) {
        userService.updateUser(userDto);
        return userDto;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
            userService.addFriends(id, friendId);
            return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.removeFriend(id,friendId);
        return ResponseEntity.ok().build();
    }
}
