package com.pm.patientservice.search;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.pm.patientservice.dto.PatientSearchCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class PatientSearchQueryBuilder {

  public NativeQuery build(PatientSearchCriteria criteria, Pageable pageable) {
    Query query = buildQuery(criteria);

    return NativeQuery.builder()
        .withQuery(query)
        .withPageable(pageable)
        .build();
  }

  private Query buildQuery(PatientSearchCriteria criteria) {
    boolean hasAny = false;
    BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

    if (StringUtils.hasText(criteria.getQ())) {
      hasAny = true;
      boolBuilder.must(qb -> qb.multiMatch(mm -> mm
          .query(criteria.getQ())
          .fields("name", "email", "address")
          .fuzziness("AUTO")));
    }

    if (StringUtils.hasText(criteria.getName())) {
      hasAny = true;
      boolBuilder.filter(qb -> qb.match(m -> m
          .field("name")
          .query(criteria.getName())));
    }

    if (StringUtils.hasText(criteria.getEmail())) {
      hasAny = true;
      boolBuilder.filter(qb -> qb.term(t -> t
          .field("email")
          .value(criteria.getEmail())));
    }

    if (StringUtils.hasText(criteria.getAddress())) {
      hasAny = true;
      boolBuilder.filter(qb -> qb.match(m -> m
          .field("address")
          .query(criteria.getAddress())));
    }

    if (criteria.getRegisteredFrom() != null || criteria.getRegisteredTo() != null) {
      hasAny = true;
      boolBuilder.filter(qb -> qb.range(r -> r.date(d -> {
        d.field("registeredDate");
        if (criteria.getRegisteredFrom() != null) {
          d.gte(criteria.getRegisteredFrom().toString());
        }
        if (criteria.getRegisteredTo() != null) {
          d.lte(criteria.getRegisteredTo().toString());
        }
        return d;
      })));
    }

    if (criteria.getDobFrom() != null || criteria.getDobTo() != null) {
      hasAny = true;
      boolBuilder.filter(qb -> qb.range(r -> r.date(d -> {
        d.field("dateOfBirth");
        if (criteria.getDobFrom() != null) {
          d.gte(criteria.getDobFrom().toString());
        }
        if (criteria.getDobTo() != null) {
          d.lte(criteria.getDobTo().toString());
        }
        return d;
      })));
    }

    if (!hasAny) {
      return Query.of(q -> q.matchAll(m -> m));
    }

    return boolBuilder.build()._toQuery();
  }
}
