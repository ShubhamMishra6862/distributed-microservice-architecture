package com.pm.appointmentservice.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class PatientValidationException extends RuntimeException {
  public PatientValidationException(String message) {
    super(message);
  }
}
