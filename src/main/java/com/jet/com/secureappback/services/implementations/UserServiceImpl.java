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
    return null;
  }

  @Override
  public void sendVerificationCode(UserDTO user) {

  }

  @Override
  public UserDTO verifyCode(String email, String code) {
    return null;
  }

  @Override
  public void resetPassword(String email) {

  }

  @Override
  public UserDTO verifyPasswordKey(String key) {
    return null;
  }

  @Override
  public void updatePassword(Long userId, String password, String confirmPassword) {

  }

  @Override
  public UserDTO verifyAccountKey(String key) {
    return null;
  }

  @Override
  public UserDTO updateUserDetails(UpdateForm user) {
    return null;
  }

  @Override
  public UserDTO getUserById(Long userId) {
    return null;
  }

  @Override
  public void updatePassword(Long userId, String currentPassword, String newPassword, String confirmNewPassword) {

  }

  @Override
  public void updateUserRole(Long userId, String roleName) {

  }

  @Override
  public void updateAccountSettings(Long userId, Boolean enabled, Boolean notLocked) {

  }

  @Override
  public UserDTO toggleMfa(String email) {
    return null;
  }

  @Override
  public void updateImage(UserDTO user, MultipartFile image) {

  }

  private UserDTO mapToUserDTO(User user) {
    return fromUser(user, roleRoleRepository.getRoleByUserId(user.getId()));
  }
}
