package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class MPA {
    private Long id;
    private String name;

    public MPA(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
