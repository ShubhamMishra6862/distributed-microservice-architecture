package com.pm.billingservice.exception;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(
      GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationException(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(
        error -> errors.put(error.getField(), error.getDefaultMessage()));
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(BillNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleBillNotFoundException(
      BillNotFoundException ex) {
    log.warn("Bill not found {}", ex.getMessage());
    return buildError(HttpStatus.NOT_FOUND, "Bill not found");
  }

  @ExceptionHandler(InvalidBillingReferenceException.class)
  public ResponseEntity<Map<String, String>> handleInvalidBillingReferenceException(
      InvalidBillingReferenceException ex) {
    log.warn("Invalid billing reference {}", ex.getMessage());
    return buildError(HttpStatus.BAD_REQUEST,
        "Appointment does not belong to the supplied patient");
  }

  @ExceptionHandler(AppointmentReferenceAlreadyExistsException.class)
  public ResponseEntity<Map<String, String>> handleAppointmentReferenceAlreadyExistsException(
      AppointmentReferenceAlreadyExistsException ex) {
    log.warn("Appointment reference already exists {}", ex.getMessage());
    return buildError(HttpStatus.BAD_REQUEST,
        "Appointment reference already exists");
  }

  private ResponseEntity<Map<String, String>> buildError(HttpStatus status,
      String message) {
    Map<String, String> errors = new HashMap<>();
    errors.put("message", message);
    return ResponseEntity.status(status).body(errors);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, String>> handleHttpMessageNotReadableException(
          HttpMessageNotReadableException ex) {
    Map<String, String> errors = new HashMap<>();
    String message = "Malformed JSON request";
    Throwable cause = ex.getCause();
    if (cause != null && cause.getMessage() != null) {
      message = "Invalid request: " + cause.getMessage().split(":")[0];
    }
    errors.put("message", message);
    return ResponseEntity.badRequest().body(errors);
  }
}
