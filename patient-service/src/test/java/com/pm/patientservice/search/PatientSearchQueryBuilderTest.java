package com.pm.patientservice.search;

import com.pm.patientservice.dto.PatientSearchCriteria;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PatientSearchQueryBuilderTest {

  @Test
  void buildQueryForEmptyCriteriaDoesNotFail() {
    PatientSearchQueryBuilder builder = new PatientSearchQueryBuilder();
    PatientSearchCriteria criteria = new PatientSearchCriteria();

    var query = builder.build(criteria, PageRequest.of(0, 10));

    assertNotNull(query);
  }

  @Test
  void buildQueryForFuzzySearchDoesNotFail() {
    PatientSearchQueryBuilder builder = new PatientSearchQueryBuilder();
    PatientSearchCriteria criteria = new PatientSearchCriteria();
    criteria.setQ("john doe");

    var query = builder.build(criteria, PageRequest.of(0, 10));

    assertNotNull(query);
  }

  @Test
  void buildQueryForDateRangesDoesNotFail() {
    PatientSearchQueryBuilder builder = new PatientSearchQueryBuilder();
    PatientSearchCriteria criteria = new PatientSearchCriteria();
    criteria.setRegisteredFrom(LocalDate.of(2024, 1, 1));
    criteria.setRegisteredTo(LocalDate.of(2024, 12, 31));
    criteria.setDobFrom(LocalDate.of(1990, 1, 1));
    criteria.setDobTo(LocalDate.of(2000, 12, 31));

    var query = builder.build(criteria, PageRequest.of(0, 10));

    assertNotNull(query);
  }
}
