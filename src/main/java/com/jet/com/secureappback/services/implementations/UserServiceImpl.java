package com.jet.com.secureappback.services.implementations;

import com.jet.com.secureappback.domain.Role;
import com.jet.com.secureappback.domain.User;
import com.jet.com.secureappback.dto.UserDTO;
import com.jet.com.secureappback.form.UpdateForm;
import com.jet.com.secureappback.repository.RoleRepository;
import com.jet.com.secureappback.repository.UserRepository;
import com.jet.com.secureappback.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.jet.com.secureappback.dtomapper.UserDTOMapper.fromUser;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository<User> userRepository;
  private final RoleRepository<Role> roleRoleRepository;

  @Override
  public UserDTO createUser(User user) {
    return mapToUserDTO(userRepository.create(user));
  }

  @Override
  public UserDTO getUserByEmail(String email) {
    return mapToUserDTO(userRepository.getUserByEmail(email));
  }

  @Override
  public void sendVerificationCode(UserDTO user) {
    userRepository.sendVerificationCode(user);
  }

  @Override
  public UserDTO verifyCode(String email, String code) {
    return mapToUserDTO(userRepository.verifyCode(email, code));
  }

  @Override
  public void resetPassword(String email) {
    userRepository.resetPassword(email);
  }

  @Override
  public void renewPassword(String key, String newpassword, String confirmPassword) {
    userRepository.renewPassword(key, newpassword, confirmPassword);
  }

  @Override
  public UserDTO verifyPasswordKey(String key) {
    return mapToUserDTO(userRepository.verifyPasswordKey(key));
  }

  @Override
  public void updatePassword(Long userId, String password, String confirmPassword) {
    userRepository.renewPassword(userId, password, confirmPassword);
  }

  @Override
  public UserDTO verifyAccountKey(String key) {
    return mapToUserDTO(userRepository.verifyAccountKey(key));
  }

  @Override
  public UserDTO updateUserDetails(UpdateForm user) {
    return mapToUserDTO(userRepository.updateUserDetails(user));
  }

  @Override
  public UserDTO getUserById(Long userId) {
    return mapToUserDTO(userRepository.get(userId));
  }

  @Override
  public void updatePassword(Long userId, String currentPassword, String newPassword, String confirmNewPassword) {
    userRepository.updatePassword(userId, currentPassword, newPassword, confirmNewPassword);
  }

  @Override
  public void updateUserRole(Long userId, String roleName) {
    roleRoleRepository.updateUserRole(userId, roleName);
  }

  @Override
  public void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked) {
    userRepository.updateAccountSettings(userId, enabled, notLocked);
  }

  @Override
  public UserDTO toggleMfa(String email) {
    return mapToUserDTO(userRepository.toggleMfa(email));
  }

  @Override
  public void updateImage(UserDTO user, MultipartFile image) {
    userRepository.updateImage(user, image);
  }

  private UserDTO mapToUserDTO(User user) {
    return fromUser(user, roleRoleRepository.getRoleByUserId(user.getId()));
  }
}
