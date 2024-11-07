package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    public Collection<MpaRating> getAllMpa() {
        log.info("Получение рейтингов");
        return mpaDbStorage.getAllMpa();
    }

    public MpaRating getMpaById(Long id) {
        log.info("Получение рейтинга – {}", id);
        return mpaDbStorage.getMpaById(id);
    }

    public void checkMpaExist(Long id) {
        mpaDbStorage.checkMpaExist(id);
    }
}
