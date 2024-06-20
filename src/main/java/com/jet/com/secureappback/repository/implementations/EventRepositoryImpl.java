package com.jet.com.secureappback.repository.implementations;

import static com.jet.com.secureappback.query.EventQuery.INSERT_EVENT_BY_USER_EMAIL_QUERY;
import static com.jet.com.secureappback.query.EventQuery.SELECT_EVENTS_BY_USER_ID_QUERY;
import static java.util.Map.of;

import com.jet.com.secureappback.domain.UserEvent;
import com.jet.com.secureappback.enums.EventType;
import com.jet.com.secureappback.repository.EventRepository;
import com.jet.com.secureappback.rowmapper.UserEventRowMapper;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 18/06/2024
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class EventRepositoryImpl implements EventRepository {
  private final NamedParameterJdbcTemplate jdbc;

  @Override
  public Collection<UserEvent> getEventsByUserId(Long userId) {
    return jdbc.query(SELECT_EVENTS_BY_USER_ID_QUERY, of("id", userId), new UserEventRowMapper());
  }

  @Override
  public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
    jdbc.update(
        INSERT_EVENT_BY_USER_EMAIL_QUERY,
        of("email", email, "type", eventType.toString(), "device", device, "ipAddress", ipAddress));
  }

  @Override
  public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {}
}
