package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();
    Optional<User> findById(Long id);
    void addUser(User user);
    //void updateUser(User user);
}
