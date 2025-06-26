package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.*;
import java.util.*;

@Component
@RequiredArgsConstructor
public class UserDtoMapper {
    private final FriendShipsRepository friendShipsRepository;

    public static User mapToUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setLogin(userDto.getLogin());
        user.setName(userDto.getName());
        user.setBirthday(userDto.getBirthday());
        return user;
    }

    public UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setLogin(user.getLogin());
        userDto.setName(user.getName());
        userDto.setBirthday(user.getBirthday());
        List<User> result = friendShipsRepository.findUserFriends(user.getId()).orElse(Collections.emptyList());
        userDto.setFriends(result .stream()
                .map(User::getId)
                .toList());
        return userDto;
    }
}
