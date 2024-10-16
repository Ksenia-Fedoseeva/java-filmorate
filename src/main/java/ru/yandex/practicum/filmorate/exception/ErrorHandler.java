package ru.yandex.practicum.filmorate.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.model.Error;

import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Error> handleValidationException(Exception e) {
        log.error("Ошибка",e);
        Error error = new Error(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NotFoundException.class, NoSuchElementException.class})
    public ResponseEntity<Error> handleNotFoundException(NotFoundException e) {
        log.error("Ошибка",e);
        Error error = new Error(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGeneralException(Exception e) {
        log.error("Ошибка",e);
        Error error = new Error("Внутренняя ошибка сервера: " + e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
