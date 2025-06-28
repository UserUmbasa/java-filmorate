package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FriendShipsRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
//@RequiredArgsConstructor не работает с mapstruct
public abstract class UserMapper {

    @Autowired
    protected FriendShipsRepository friendShipsRepository;

    public abstract User mapToUser(UserDto userDto);

    @Mappings({
            @Mapping(target = "friends", expression = "java(mapFriends(user))")
    })
    public abstract UserDto mapToUserDto(User user);

    protected List<Long> mapFriends(User user) {  // protected
        return friendShipsRepository.findUserFriends(user.getId())
                .orElse(Collections.emptyList())
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}

