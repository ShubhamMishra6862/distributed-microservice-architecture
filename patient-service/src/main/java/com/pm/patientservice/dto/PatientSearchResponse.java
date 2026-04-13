package com.pm.patientservice.dto;

import java.util.List;

public class PatientSearchResponse {
  private List<PatientResponseDTO> items;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;

  public PatientSearchResponse() {
  }

  public PatientSearchResponse(List<PatientResponseDTO> items, int page, int size,
      long totalElements, int totalPages) {
    this.items = items;
    this.page = page;
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
  }

  public List<PatientResponseDTO> getItems() {
    return items;
  }

  public void setItems(List<PatientResponseDTO> items) {
    this.items = items;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(long totalElements) {
    this.totalElements = totalElements;
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }
}

