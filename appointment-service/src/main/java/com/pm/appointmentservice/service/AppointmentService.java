package com.pm.appointmentservice.service;

import com.pm.appointmentservice.dto.AppointmentRequestDTO;
import com.pm.appointmentservice.dto.AppointmentResponseDTO;
import com.pm.appointmentservice.dto.UpdateStatusDTO;
import com.pm.appointmentservice.exception.AppointmentNotFoundException;
import com.pm.appointmentservice.exception.PatientValidationException;
import com.pm.appointmentservice.grpc.PatientValidationClient;
import com.pm.appointmentservice.kafka.KafkaProducer;
import com.pm.appointmentservice.mapper.AppointmentMapper;
import com.pm.appointmentservice.model.Appointment;
import com.pm.appointmentservice.repository.AppointmentRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

  private final AppointmentRepository appointmentRepository;
  private final KafkaProducer kafkaProducer;
  private final PatientValidationClient patientValidationClient;

  public AppointmentService(AppointmentRepository appointmentRepository,
      KafkaProducer kafkaProducer, PatientValidationClient patientValidationClient) {
    this.appointmentRepository = appointmentRepository;
    this.kafkaProducer = kafkaProducer;
    this.patientValidationClient = patientValidationClient;
  }

  public List<AppointmentResponseDTO> getAppointments() {
    List<Appointment> appointments = appointmentRepository.findAll();
    return appointments.stream().map(AppointmentMapper::toDTO).toList();
  }

  public AppointmentResponseDTO createAppointment(AppointmentRequestDTO requestDTO) {
    validatePatient(requestDTO.getPatientId());
    Appointment appointment = AppointmentMapper.toModel(requestDTO);
    Appointment saved = appointmentRepository.save(appointment);
    kafkaProducer.sendAppointmentEvent(saved.getId(), saved.getPatientId(), "CREATED");
    return AppointmentMapper.toDTO(saved);
  }

  public AppointmentResponseDTO updateAppointmentDetails(UUID id, AppointmentRequestDTO requestDTO) {
    Appointment appointment = appointmentRepository.findById(id)
        .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + id));
    validatePatient(requestDTO.getPatientId());
    appointment.setPatientId(requestDTO.getPatientId());
    appointment.setDetails(requestDTO.getDetails());
    appointment.setUpdatedAt(java.time.LocalDateTime.now());
    Appointment saved = appointmentRepository.save(appointment);
    kafkaProducer.sendAppointmentEvent(saved.getId(), saved.getPatientId(), "UPDATED");
    return AppointmentMapper.toDTO(saved);
  }

  public AppointmentResponseDTO updateAppointmentStatus(UUID id, UpdateStatusDTO statusDTO) {
    Appointment appointment = appointmentRepository.findById(id)
        .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + id));
    appointment.setStatus(statusDTO.getStatus());
    Appointment saved = appointmentRepository.save(appointment);
    kafkaProducer.sendAppointmentEvent(saved.getId(), saved.getPatientId(), "STATUS_UPDATED");
    return AppointmentMapper.toDTO(saved);
  }

  public void deleteAppointment(UUID id) {
    Appointment appointment = appointmentRepository.findById(id)
        .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found with ID: " + id));
    appointmentRepository.deleteById(id);
    kafkaProducer.sendAppointmentEvent(appointment.getId(), appointment.getPatientId(), "DELETED");
  }

  private void validatePatient(UUID patientId) {
    boolean exists = patientValidationClient.validatePatient(patientId);
    if (!exists) {
      throw new PatientValidationException("Patient not found or invalid: " + patientId);
    }
  }
}


