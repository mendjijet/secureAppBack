package com.jet.com.secureappback.repository.implementations;

import static com.jet.com.secureappback.enums.RoleType.ROLE_USER;
import static com.jet.com.secureappback.enums.VerificationType.ACCOUNT;
import static com.jet.com.secureappback.query.UserQuery.*;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

import com.jet.com.secureappback.domain.Role;
import com.jet.com.secureappback.domain.User;
import com.jet.com.secureappback.exception.ApiException;
import com.jet.com.secureappback.repository.RoleRepository;
import com.jet.com.secureappback.repository.UserRepository;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User> {
  private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
  private final NamedParameterJdbcTemplate jdbc;
  private final RoleRepository<Role> roleRepository;
  private final BCryptPasswordEncoder encoder;
  private static final String USERS_COLUMN_ID = "ID";

  @Override
  public User create(User user) {
    // Check the email is unique
    if (getEmailCount(user.getEmail().trim().toLowerCase()) > 0)
      throw new ApiException("Email already in use. Please use a different email and try again.");
    // Save new User
    try {
      KeyHolder holder = new GeneratedKeyHolder();
      SqlParameterSource parameters = getSqlParameterSource(user);
      jdbc.update(INSERT_USER_QUERY, parameters, holder);
      //user.setId(requireNonNull(holder.getKey()).longValue());
      user.setId((Long) requireNonNull(requireNonNull(holder.getKeys()).get(USERS_COLUMN_ID)));
      // Add role to the user
      roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
      // Send verification URL
      String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());
      // Save URL in verification table
      jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY, of("userId", user.getId(), "url", verificationUrl));
      // Send email to user with verification URL
      //sendEmail(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT);
      user.setEnabled(false);
      user.setNotLocked(true);
      // Return the newly created user
      return user;
      // If any errors, throw exception with proper message
    }
    catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again.");
    }






  }

  @Override
  public Collection<User> list(int page, int pageSize) {
    return List.of();
  }

  @Override
  public User get(Long id) {
    return null;
  }

  @Override
  public User update(User data) {
    return null;
  }

  @Override
  public Boolean delete(Long id) {
    return null;
  }

  private Integer getEmailCount(String email) {
    return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, of("email", email), Integer.class);
  }

  private SqlParameterSource getSqlParameterSource(User user) {
    return new MapSqlParameterSource()
        .addValue("firstName", user.getFirstName())
        .addValue("lastName", user.getLastName())
        .addValue("email", user.getEmail())
        .addValue("password", encoder.encode(user.getPassword()));
  }

  private String getVerificationUrl(String key, String type) {
    return fromCurrentContextPath().path("/user/verify/" + type + "/" + key).toUriString();
  }
}
