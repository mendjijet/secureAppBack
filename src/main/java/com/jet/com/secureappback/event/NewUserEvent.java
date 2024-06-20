package com.jet.com.secureappback.event;

import com.jet.com.secureappback.enums.EventType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 18/06/2024
 */

@Getter
@Setter
public class NewUserEvent extends ApplicationEvent {
    private EventType type;
    private String email;

    public NewUserEvent(String email, EventType type) {
        super(email);
        this.type = type;
        this.email = email;
    }}
