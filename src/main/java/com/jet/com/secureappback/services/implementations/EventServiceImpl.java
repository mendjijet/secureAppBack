package com.jet.com.secureappback.services.implementations;

import com.jet.com.secureappback.domain.UserEvent;
import com.jet.com.secureappback.enums.EventType;
import com.jet.com.secureappback.repository.EventRepository;
import com.jet.com.secureappback.services.EventService;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 18/06/2024
 */
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
  private final EventRepository eventRepository;

  @Override
  public Collection<UserEvent> getEventsByUserId(Long userId) {
    return eventRepository.getEventsByUserId(userId);
  }

  @Override
  public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
    eventRepository.addUserEvent(email, eventType, device, ipAddress);
  }

  @Override
  public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {}
}
