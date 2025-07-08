package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.MPA;

@Mapper(componentModel = "spring")
public abstract class MpaMapper {
    public abstract MpaDto mapToMpaDTO(MPA mpa);
}
