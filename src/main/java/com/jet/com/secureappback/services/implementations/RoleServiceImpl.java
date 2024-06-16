package com.jet.com.secureappback.services.implementations;

import com.jet.com.secureappback.domain.Role;
import com.jet.com.secureappback.dto.UserDTO;
import com.jet.com.secureappback.form.UpdateForm;
import com.jet.com.secureappback.repository.RoleRepository;
import com.jet.com.secureappback.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 12/06/2024
 */

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository<Role> roleRoleRepository;

    @Override
    public Role getRoleByUserId(Long id) {
        return roleRoleRepository.getRoleByUserId(id);
    }

    @Override
    public Collection<Role> getRoles() {
        return roleRoleRepository.list();
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
}
