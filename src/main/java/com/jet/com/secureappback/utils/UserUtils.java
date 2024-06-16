package com.jet.com.secureappback.utils;

import com.jet.com.secureappback.domain.UserPrincipal;
import com.jet.com.secureappback.dto.UserDTO;
import org.springframework.security.core.Authentication;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 12/06/2024
 */
public class UserUtils {
  private UserUtils() {}

  public static UserDTO getAuthenticatedUser(Authentication authentication) {
    return ((UserDTO) authentication.getPrincipal());
  }

  public static UserDTO getLoggedInUser(Authentication authentication) {
    return ((UserPrincipal) authentication.getPrincipal()).getUser();
  }
}
