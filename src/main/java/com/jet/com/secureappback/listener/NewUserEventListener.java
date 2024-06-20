package com.jet.com.secureappback.listener;

import com.jet.com.secureappback.event.NewUserEvent;
import com.jet.com.secureappback.services.EventService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.jet.com.secureappback.utils.RequestUtils.getDevice;
import static com.jet.com.secureappback.utils.RequestUtils.getIpAddress;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 18/06/2024
 */

@Component
@RequiredArgsConstructor
public class NewUserEventListener {
    private final EventService eventService;
    private final HttpServletRequest request;

    @EventListener
    public void onNewUserEvent(NewUserEvent event) {
        eventService.addUserEvent(event.getEmail(), event.getType(), getDevice(request), getIpAddress(request));
    }
}
