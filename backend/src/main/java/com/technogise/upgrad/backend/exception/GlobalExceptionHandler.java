package com.technogise.upgrad.backend.exception;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<String> handleMessageNotReadable(final HttpMessageNotReadableException ex) {
    logger.warn("Malformed request body: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request body");
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<String> handleAuthenticationException(final AuthenticationException ex) {
    logger.error("Authentication failure", ex);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidationException(
      final MethodArgumentNotValidException ex) {
    final String errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(final IllegalArgumentException ex) {
    logger.warn("Invalid argument: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(RateLimitExceededException.class)
  public ResponseEntity<String> handleRateLimitException(final RateLimitExceededException ex) {
    logger.warn("Rate limit exceeded: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage());
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<String> handleResourceNotFoundException(
      final ResourceNotFoundException ex) {
    logger.warn("Resource not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGenericException(final Exception ex) {
    logger.error("Unexpected error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred");
  }
}
