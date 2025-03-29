package ru.yandex.practicum.filmorate.validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice // включает @ControllerAdvice и @ResponseBody
@Slf4j
public class ErrorHandlingControllerAdvice {
    // проверяю на уровне методов
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidException(
            MethodArgumentNotValidException e, WebRequest request
    ) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        for (Violation violation : violations) {
            log.error("Ошибка валидации : " + violation.getFieldName() + " - " + violation.getMessage());
        }
        return new ValidationErrorResponse(violations);
    }
}