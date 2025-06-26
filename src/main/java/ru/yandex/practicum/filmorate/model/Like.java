package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Like {
    private Long idUser;
    private Long idFIlm;

    public Like(Long userId, Long filmId) {
        this.idUser = userId;
        this.idFIlm = filmId;
    }
}
