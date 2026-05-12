package com.pm.billingservice.mapper;

import com.pm.billingservice.dto.BillRequestDTO;
import com.pm.billingservice.dto.BillResponseDTO;
import com.pm.billingservice.model.Bill;

public final class BillingMapper {

  private BillingMapper() {
  }

  public static Bill toModel(BillRequestDTO requestDTO) {
    Bill bill = new Bill();
    updateModel(bill, requestDTO);
    return bill;
  }

  public static void updateModel(Bill bill, BillRequestDTO requestDTO) {
    bill.setPatientId(requestDTO.getPatientId());
    bill.setAppointmentId(requestDTO.getAppointmentId());
    bill.setAmount(requestDTO.getAmount());
    bill.setDescription(requestDTO.getDescription());
    bill.setStatus(requestDTO.getStatus());
  }

  public static BillResponseDTO toDTO(Bill bill) {
    BillResponseDTO responseDTO = new BillResponseDTO();
    responseDTO.setId(bill.getId());
    responseDTO.setPatientId(bill.getPatientId());
    responseDTO.setAppointmentId(bill.getAppointmentId());
    responseDTO.setAmount(bill.getAmount());
    responseDTO.setDescription(bill.getDescription());
    responseDTO.setStatus(bill.getStatus());
    responseDTO.setCreatedAt(bill.getCreatedAt());
    responseDTO.setUpdatedAt(bill.getUpdatedAt());
    return responseDTO;
  }
}
