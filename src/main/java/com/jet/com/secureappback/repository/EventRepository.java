package com.jet.com.secureappback.repository;

import com.jet.com.secureappback.domain.UserEvent;
import com.jet.com.secureappback.enums.EventType;

import java.util.Collection;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 18/06/2024
 */
public interface EventRepository {
    Collection<UserEvent> getEventsByUserId(Long userId);
    void addUserEvent(String email, EventType eventType, String device, String ipAddress);
    void addUserEvent(Long userId, EventType eventType, String device, String ipAddress);
}
