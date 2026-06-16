package bg.uniplov.fitness.advisor.web;

import bg.uniplov.fitness.advisor.dto.ApiErrorResponse;
import bg.uniplov.fitness.advisor.model.ProfileValidationException;
import bg.uniplov.fitness.advisor.ontology.OntologyLoadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> validation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .distinct()
                .toList();
        return error(HttpStatus.BAD_REQUEST, "Невалидни входни данни", details);
    }

    @ExceptionHandler(ProfileValidationException.class)
    public ResponseEntity<ApiErrorResponse> profileValidation(ProfileValidationException ex) {
        return error(HttpStatus.BAD_REQUEST, "Невалиден потребителски профил", List.of(ex.getMessage()));
    }

    @ExceptionHandler(OntologyLoadException.class)
    public ResponseEntity<ApiErrorResponse> ontology(OntologyLoadException ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Проблем при зареждане на онтологията", List.of(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> generic(Exception ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Не може да се генерира препоръка", List.of(ex.getMessage()));
    }

    private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message, List<String> details) {
        return ResponseEntity.status(status).body(new ApiErrorResponse(
                Instant.now(),
                status.value(),
                message,
                details
        ));
    }
}
