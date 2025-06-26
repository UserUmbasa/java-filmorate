package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserDtoMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.FriendShipsRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.validator.Marker;
import java.util.*;
import java.util.stream.Collectors;

/**
 * будет отвечать за такие операции с пользователями, как добавление в друзья, удаление из друзей,
 * вывод списка общих друзей
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FriendShipsRepository friendShipsRepository;
    private final Validator validator;
    private final UserDtoMapper userDtoMapper;

    public UserDto findById(Long userId) {
        if (!checkUserExists(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не корректный ID");
        }
        return userDtoMapper.mapToUserDto(userRepository.findById(userId));
    }

    public List<UserDto> findUserFriends(Long id) {
        if (!checkUserExists(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ID пользователя не может быть null.");
        }
        List<User> result = friendShipsRepository.findUserFriends(id).orElse(new ArrayList<>());
        return mapToUserDtoList(result);
    }

    public List<UserDto> findMutualFriends(Long id, Long otherId) {
        if(!checkUserExists(id) || !checkUserExists(otherId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не корректный ID пользователя");
        }
        List<User> result = friendShipsRepository.findMutualFriends(id, otherId);
        return mapToUserDtoList(result);
    }

    public void addFriends(Long id, Long friendId) {
        if(!checkUserExists(id) || !checkUserExists(friendId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не корректный ID пользователя");
        }
        friendShipsRepository.addFriends(id,friendId);
    }

    public void updateUser(UserDto userDto) {
        if (!checkUserExists(userDto.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Некорректный ID пользователя");
        }
        UserDto existingUser = findById(userDto.getId());
        try {
            validateUser(userDto);
            existingUser.setEmail(userDto.getEmail());
            existingUser.setLogin(userDto.getLogin());
            existingUser.setName(userDto.getName());
            existingUser.setBirthday(userDto.getBirthday());
            log.info("Пользователь успешно обновлен: {}", existingUser);
        } catch (ValidationException e) {
            log.error("Ошибка валидации при обновлении пользователя: {}", e.getErrors());
            throw e;  // Перебрасываем исключение, чтобы контроллер мог его обработать
        }
        User updatedUser = userDtoMapper.mapToUser(existingUser);
        updatedUser.setId(userDto.getId());
        userRepository.updateUser(updatedUser);
    }

    public void removeFriend(Long id, Long friendId) {
        if(!checkUserExists(id) || !checkUserExists(friendId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не корректный ID");
        }
        friendShipsRepository.removeFriend(id, friendId);
    }

    public UserDto addUser(UserDto userDto) {
        User user = UserDtoMapper.mapToUser(userDto);
        log.info("Добавлен элемент: {}", userDto);
        return findById(userRepository.save(user));
    }

    public Collection<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userDtoMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    //------------------вспомогательные методы---------------------------
    public boolean checkUserExists(Long userId) {
        if(userId == null) {
            return false;
        }
        return userRepository.existsUserById(userId);
    }

    private List<UserDto> mapToUserDtoList(List<User> result) {
        return result.stream()
                .map(userDtoMapper::mapToUserDto)  // Или .map(user -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private void validateUser(UserDto userDto) {
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto, Marker.OnCreate.class);  // Используем вашу группу валидации
        if (!violations.isEmpty()) {
            Map<String, String> errors = extractValidationErrors(violations);
            throw new ValidationException("Ошибки валидации при обновлении пользователя", errors);
        }
    }

    private Map<String, String> extractValidationErrors(Set<ConstraintViolation<UserDto>> violations) {
        return violations.stream().collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage
        ));
    }
}
