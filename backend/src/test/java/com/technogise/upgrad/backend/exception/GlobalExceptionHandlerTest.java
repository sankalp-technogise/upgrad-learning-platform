package com.technogise.upgrad.backend.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

  @Test
  void shouldHandleAuthenticationException() {
    AuthenticationException ex = new AuthenticationException("Invalid credentials");

    ResponseEntity<String> response = exceptionHandler.handleAuthenticationException(ex);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Authentication failed", response.getBody());
  }

  @Test
  void shouldHandleGenericException() {
    Exception ex = new Exception("Internal error");

    ResponseEntity<String> response = exceptionHandler.handleGenericException(ex);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals("An unexpected error occurred", response.getBody());
  }

  @Test
  void shouldHandleValidationException() {
    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    BindingResult bindingResult = mock(BindingResult.class);
    FieldError fieldError = new FieldError("objectName", "fieldName", "must not be null");

    org.mockito.Mockito.when(ex.getBindingResult()).thenReturn(bindingResult);
    org.mockito.Mockito.when(bindingResult.getFieldErrors())
        .thenReturn(java.util.Collections.singletonList(fieldError));

    ResponseEntity<String> response = exceptionHandler.handleValidationException(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals("fieldName: must not be null", response.getBody());
  }
}
