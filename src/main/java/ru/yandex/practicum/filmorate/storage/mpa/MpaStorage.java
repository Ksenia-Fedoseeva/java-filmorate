package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;

public interface MpaStorage {
    Collection<MpaRating> getAllMpa();

    MpaRating getMpaById(Long id);

    void checkMpaExist(Long id);
}
