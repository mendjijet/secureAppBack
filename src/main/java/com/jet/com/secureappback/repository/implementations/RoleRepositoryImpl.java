package com.jet.com.secureappback.repository.implementations;

import static com.jet.com.secureappback.enums.RoleType.ROLE_USER;
import static com.jet.com.secureappback.query.RoleQuery.*;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;

import com.jet.com.secureappback.domain.Role;
import com.jet.com.secureappback.exception.ApiException;
import com.jet.com.secureappback.repository.RoleRepository;
import com.jet.com.secureappback.rowmapper.RoleRowMapper;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {
  private final NamedParameterJdbcTemplate jdbc;

  @Override
  public Role create(Role role) {
    return null;
  }

  @Override
  public Collection<Role> list() {
    log.info("Fetching all roles");
    try {
      return jdbc.query(SELECT_ROLES_QUERY, new RoleRowMapper());
    } catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again.");
    }
  }

  @Override
  public Role get(Long id) {
    return null;
  }

  @Override
  public Role update(Role role) {
    return null;
  }

  @Override
  public Boolean delete(Long id) {
    return null;
  }

  @Override
  public void addRoleToUser(Long userId, String roleName) {
    log.info("Adding role {} to user id: {}", roleName, userId);
    try {
      Role role =
          jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, of("name", roleName), new RoleRowMapper());
      jdbc.update(
          INSERT_ROLE_TO_USER_QUERY, of("userId", userId, "roleId", requireNonNull(role).getId()));
    } catch (EmptyResultDataAccessException exception) {
      throw new ApiException("No role found by name: " + ROLE_USER.name());

    } catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again.");
    }
  }

  @Override
  public Role getRoleByUserId(Long userId) {
    log.info("Fetching role for user id: {}", userId);
    try {
      return jdbc.queryForObject(SELECT_ROLE_BY_ID_QUERY, of("id", userId), new RoleRowMapper());
    } catch (EmptyResultDataAccessException exception) {
      throw new ApiException("No role found by name: " + ROLE_USER.name());
    } catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again.");
    }
  }

  @Override
  public Role getRoleByUserEmail(String email) {
    return null;
  }

  @Override
  public void updateUserRole(Long userId, String roleName) {
    log.info("Updating role for user id: {}", userId);
    try {
      Role role =
          jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, of("name", roleName), new RoleRowMapper());
      jdbc.update(UPDATE_USER_ROLE_QUERY, of("roleId", role.getId(), "userId", userId));
    } catch (EmptyResultDataAccessException exception) {
      throw new ApiException("No role found by name: " + roleName);
    } catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again.");
    }
  }
}
