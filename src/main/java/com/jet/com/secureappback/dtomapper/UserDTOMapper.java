package com.jet.com.secureappback.dtomapper;

import com.jet.com.secureappback.domain.Role;
import com.jet.com.secureappback.domain.User;
import com.jet.com.secureappback.dto.UserDTO;
import org.springframework.beans.BeanUtils;

public class UserDTOMapper {
  private UserDTOMapper() {}

  public static UserDTO fromUser(User user) {
    UserDTO userDTO = new UserDTO();
    BeanUtils.copyProperties(user, userDTO);
    return userDTO;
  }

  public static UserDTO fromUser(User user, Role role) {
    UserDTO userDTO = new UserDTO();
    BeanUtils.copyProperties(user, userDTO);
    userDTO.setRoleName(role.getName());
    userDTO.setPermissions(role.getPermission());
    return userDTO;
  }

  public static User toUser(UserDTO userDTO) {
    User user = new User();
    BeanUtils.copyProperties(userDTO, user);
    return user;
  }
}
