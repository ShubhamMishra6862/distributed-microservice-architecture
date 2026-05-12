package com.pm.patientservice.search;

import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;

public class PatientSearchMapper {
  public static PatientDocument toDocument(Patient patient) {
    PatientDocument document = new PatientDocument();
    document.setId(patient.getId().toString());
    document.setName(patient.getName());
    document.setEmail(patient.getEmail());
    document.setAddress(patient.getAddress());
    document.setDateOfBirth(patient.getDateOfBirth());
    document.setRegisteredDate(patient.getRegisteredDate());
    return document;
  }

  public static PatientResponseDTO toDTO(PatientDocument document) {
    PatientResponseDTO dto = new PatientResponseDTO();
    dto.setId(document.getId());
    dto.setName(document.getName());
    dto.setEmail(document.getEmail());
    dto.setAddress(document.getAddress());
    if (document.getDateOfBirth() != null) {
      dto.setDateOfBirth(document.getDateOfBirth());
    }
    return dto;
  }
}

