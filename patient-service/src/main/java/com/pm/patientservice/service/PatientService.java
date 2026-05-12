package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistsException;
import com.pm.patientservice.exception.GlobalExceptionHandler;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;

import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private final PatientRepository patientRepository;
  private final com.pm.patientservice.search.PatientSearchService patientSearchService;

  public PatientService(PatientRepository patientRepository,
      com.pm.patientservice.search.PatientSearchService patientSearchService) {
    this.patientRepository = patientRepository;
    this.patientSearchService = patientSearchService;
  }

  public List<PatientResponseDTO> getPatients() {
    List<Patient> patients = patientRepository.findAll();

    return patients.stream().map(PatientMapper::toDTO).toList();
  }
  public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {

    if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
      throw new EmailAlreadyExistsException(
          "A patient with this email " + "already exists"
              + patientRequestDTO.getEmail());
    }
    try {
      Patient newPatient = patientRepository.save(
              PatientMapper.toModel(patientRequestDTO));

      patientSearchService.save(newPatient);

      return PatientMapper.toDTO(newPatient);
    } catch (Exception e) {log.error("Error creating patient", e);

      throw new RuntimeException("Failed to create patient", e);
    }
  }

  public PatientResponseDTO updatePatient(UUID id,
      PatientRequestDTO patientRequestDTO) {

    Patient patient = patientRepository.findById(id).orElseThrow(
        () -> new PatientNotFoundException("Patient not found with ID: " + id));

    if (patientRequestDTO.getEmail() != null &&
            patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
      throw new EmailAlreadyExistsException(
              "A patient with this email already exists: " + patientRequestDTO.getEmail());
    }


    if (patientRequestDTO.getName() != null) {
      patient.setName(patientRequestDTO.getName());
    }

    if (patientRequestDTO.getAddress() != null) {
      patient.setAddress(patientRequestDTO.getAddress());
    }

    if (patientRequestDTO.getEmail() != null) {
      patient.setEmail(patientRequestDTO.getEmail());
    }

    if (patientRequestDTO.getWeight() != null) {
      patient.setWeight(patientRequestDTO.getWeight());
    }

    if (patientRequestDTO.getHeight() != null) {
      patient.setHeight(patientRequestDTO.getHeight());
    }

    if (patientRequestDTO.getDateOfBirth() != null) {
      patient.setDateOfBirth(patientRequestDTO.getDateOfBirth());
    }

    if (patientRequestDTO.getRegisteredDate() != null) {
      patient.setRegisteredDate(patientRequestDTO.getRegisteredDate());
    }

    Patient updatedPatient = patientRepository.save(patient);

    patientSearchService.save(updatedPatient);

    return PatientMapper.toDTO(updatedPatient);
  }

  public void deletePatient(UUID id) {
    patientRepository.deleteById(id);
    patientSearchService.delete(id);
  }

  public com.pm.patientservice.dto.PatientSearchResponse searchPatients(
      com.pm.patientservice.dto.PatientSearchCriteria criteria) {
    return patientSearchService.search(criteria);
  }

  public PatientResponseDTO getPatient(UUID id) {
    Patient patient = patientRepository.findById(id).orElseThrow(
        () -> new PatientNotFoundException("Patient not found with ID: " + id));
    return PatientMapper.toDTO(patient);
  }
}
