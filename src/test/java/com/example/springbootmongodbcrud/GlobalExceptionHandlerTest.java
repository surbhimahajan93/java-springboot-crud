package com.example.springbootmongodbcrud;

import com.example.springbootmongodbcrud.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleRuntimeException() {
        RuntimeException exception = new RuntimeException("Test runtime exception");
        
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            exceptionHandler.handleRuntimeException(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test runtime exception", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testHandleValidationExceptions() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("product", "name", "Name is required");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
        
        ResponseEntity<Map<String, String>> response = 
            exceptionHandler.handleValidationExceptions(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Name is required", response.getBody().get("name"));
    }

    @Test
    void testHandleGenericException() {
        Exception exception = new Exception("Generic test exception");
        
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            exceptionHandler.handleGenericException(exception);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Generic test exception"));
        assertEquals(500, response.getBody().getStatus());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void testErrorResponseConstructor() {
        int status = 404;
        String message = "Not found";
        LocalDateTime timestamp = LocalDateTime.now();
        
        GlobalExceptionHandler.ErrorResponse errorResponse = 
            new GlobalExceptionHandler.ErrorResponse(status, message, timestamp);
        
        assertEquals(status, errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(timestamp, errorResponse.getTimestamp());
    }

    @Test
    void testErrorResponseSetters() {
        GlobalExceptionHandler.ErrorResponse errorResponse = 
            new GlobalExceptionHandler.ErrorResponse(200, "OK", LocalDateTime.now());
        
        errorResponse.setStatus(500);
        errorResponse.setMessage("Internal Server Error");
        LocalDateTime newTimestamp = LocalDateTime.now();
        errorResponse.setTimestamp(newTimestamp);
        
        assertEquals(500, errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getMessage());
        assertEquals(newTimestamp, errorResponse.getTimestamp());
    }

    @Test
    void testHandleValidationExceptionsWithMultipleErrors() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("product", "name", "Name is required");
        FieldError fieldError2 = new FieldError("product", "price", "Price must be positive");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError1));
        
        ResponseEntity<Map<String, String>> response = 
            exceptionHandler.handleValidationExceptions(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Name is required", response.getBody().get("name"));
    }

    @Test
    void testHandleRuntimeExceptionWithNullMessage() {
        RuntimeException exception = new RuntimeException();
        
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = 
            exceptionHandler.handleRuntimeException(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
    }
} 