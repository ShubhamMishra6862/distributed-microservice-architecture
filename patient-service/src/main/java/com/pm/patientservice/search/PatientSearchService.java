package com.pm.patientservice.search;

import com.pm.patientservice.dto.PatientSearchCriteria;
import com.pm.patientservice.dto.PatientSearchResponse;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

@Service
public class PatientSearchService {
  private final ElasticsearchOperations operations;
  private final PatientSearchQueryBuilder queryBuilder;

  public PatientSearchService(ElasticsearchOperations operations,
      PatientSearchQueryBuilder queryBuilder) {
    this.operations = operations;
    this.queryBuilder = queryBuilder;
  }

  public void save(Patient patient) {
    operations.save(PatientSearchMapper.toDocument(patient));
  }

  public void delete(UUID id) {
    operations.delete(id.toString(), IndexCoordinates.of("patients"));
  }

  public PatientSearchResponse search(PatientSearchCriteria criteria) {
    Sort sort = Sort.by(parseDirection(criteria.getSortDir()), criteria.getSortBy());
    Pageable pageable = PageRequest.of(
        Math.max(0, criteria.getPage()),
        Math.max(1, criteria.getSize()),
        sort);

    var query = queryBuilder.build(criteria, pageable);
    SearchHits<PatientDocument> hits = operations.search(query, PatientDocument.class);

    List<PatientResponseDTO> items = hits.getSearchHits().stream()
        .map(SearchHit::getContent)
        .map(PatientSearchMapper::toDTO)
        .toList();

    long totalElements = hits.getTotalHits();
    int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

    return new PatientSearchResponse(items, pageable.getPageNumber(),
        pageable.getPageSize(), totalElements, totalPages);
  }

  private Sort.Direction parseDirection(String dir) {
    if (dir == null) {
      return Sort.Direction.DESC;
    }
    return "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
  }
}

