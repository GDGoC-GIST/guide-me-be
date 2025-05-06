package guideme.authservice.controller;

import guideme.authservice.infrastructure.dto.response.GlobalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponse<String>> handleGeneralException(Exception e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GlobalResponse.fail("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
