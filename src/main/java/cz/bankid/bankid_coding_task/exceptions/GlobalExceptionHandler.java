package cz.bankid.bankid_coding_task.exceptions;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Hidden
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private record ExceptionConfig(HttpStatus status, String errorMessage) {}

    private ResponseEntity<Map<String, Object>> createErrorResponse(Exception ex, HttpStatus status, String error) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("timestamp", Instant.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, status);
    }

    private final Map<Class<? extends Exception>, ExceptionConfig> exceptionMappings = Map.of(
            CurrencyNotFoundException.class,
            new ExceptionConfig(HttpStatus.NOT_FOUND, "Currency Not Found"),

            ParseExchangeRatesException.class,
            new ExceptionConfig(HttpStatus.BAD_REQUEST, "Unable to parse exchange rates"),

            InvalidCurrencyDataException.class,
            new ExceptionConfig(HttpStatus.BAD_REQUEST, "Cannot process loadedData")

            // add new exceptions here
    );

    @ExceptionHandler({
            CurrencyNotFoundException.class,
            ParseExchangeRatesException.class,
            InvalidCurrencyDataException.class
    })
    public ResponseEntity<Map<String, Object>> handleCustomExceptions(Exception ex) {
        ExceptionConfig config = exceptionMappings.getOrDefault(
                ex.getClass(),
                new ExceptionConfig(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error")
        );
        return createErrorResponse(ex, config.status, config.errorMessage);
    }
}