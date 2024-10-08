package com.techphantomexample.usermicroservice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techphantomexample.usermicroservice.model.CreateResponse;
import com.techphantomexample.usermicroservice.services.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserOperationException.class)
    public ResponseEntity<CreateResponse> handleUserOperationException(UserOperationException ex) {
        CreateResponse createResponse = new CreateResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(),null);
        return new ResponseEntity<CreateResponse>(createResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CreateResponse> handleNotFoundException(NotFoundException ex) {
        CreateResponse createResponse = new CreateResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value(), null);
        return new ResponseEntity<>(createResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleJsonProcessingException(JsonProcessingException ex) {
        logger.error("JsonProcessingException occurred: {}", ex.getMessage());
        return "Error processing JSON: " + ex.getMessage();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return new ResponseEntity<>("User not found: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<CreateResponse> handleAuthenticationException(AuthenticationException ex) {
        CreateResponse createResponse = new CreateResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), null);
        return new ResponseEntity<>(createResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<CreateResponse> handleProductException(ProductException ex) {
        CreateResponse createResponse = new CreateResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value(),null);
        return new ResponseEntity<>(createResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CreateResponse> handleGlobalException(Exception ex, WebRequest request) {
        CreateResponse createResponse = new CreateResponse("An unexpected error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(),null);
        return new ResponseEntity<>(createResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
