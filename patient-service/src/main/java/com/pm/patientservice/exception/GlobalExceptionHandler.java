package com.pm.patientservice.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  //Common method to build response
  private ResponseEntity<Map<String, String>> buildErrorResponse(Map<String, String> errors) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }

  //Email already exists
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(
          EmailAlreadyExistsException ex) {

    log.warn("Email already exists: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    errors.put("email", "Email address already exists");

    return buildErrorResponse(errors);
  }

  //Patient not found
  @ExceptionHandler(PatientNotFoundException.class)
  public ResponseEntity<Map<String, String>> handlePatientNotFoundException(
          PatientNotFoundException ex) {

    log.warn("Patient not found: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    errors.put("patient", "Patient not found");

    return buildErrorResponse(errors);
  }

  // Validation errors (Bean Validation)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationException(
          MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

    return buildErrorResponse(errors);
  }

  //Datatype mismatch + malformed JSON
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(
          HttpMessageNotReadableException ex) {

    Map<String, String> errors = new HashMap<>();

    Throwable cause = ex.getCause();

    if (cause instanceof InvalidFormatException invalidFormatException) {

      invalidFormatException.getPath().forEach(field -> {
        String fieldName = field.getFieldName();

        if (fieldName != null) {
          if (fieldName.equals("weight") || fieldName.equals("height")) {
            errors.put(fieldName, "Must be a valid number");
          } else if (fieldName.equals("dateOfBirth") || fieldName.equals("registeredDate")) {
            errors.put(fieldName, "Invalid date format. Use dd-MM-yyyy");
          } else {
            errors.put(fieldName, "Invalid value");
          }
        }
      });

    } else {
      errors.put("error", "Malformed JSON request");
    }

    return buildErrorResponse(errors);
  }
}