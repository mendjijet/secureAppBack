package com.jet.com.secureappback.repository.implementations;

import static com.jet.com.secureappback.enums.RoleType.ROLE_USER;
import static com.jet.com.secureappback.enums.VerificationType.ACCOUNT;
import static com.jet.com.secureappback.enums.VerificationType.PASSWORD;
import static com.jet.com.secureappback.query.UserQuery.*;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

import com.jet.com.secureappback.domain.Role;
import com.jet.com.secureappback.domain.User;
import com.jet.com.secureappback.domain.UserPrincipal;
import com.jet.com.secureappback.dto.UserDTO;
import com.jet.com.secureappback.enums.VerificationType;
import com.jet.com.secureappback.exception.ApiException;
import com.jet.com.secureappback.form.UpdateForm;
import com.jet.com.secureappback.repository.RoleRepository;
import com.jet.com.secureappback.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.jet.com.secureappback.rowmapper.UserRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.time.DateFormatUtils.format;
import static org.apache.commons.lang3.time.DateUtils.addDays;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User> , UserDetailsService {
  private static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
  private static final String USERS_COLUMN_ID = "ID";
  private final NamedParameterJdbcTemplate jdbc;
  private final RoleRepository<Role> roleRepository;
  private final BCryptPasswordEncoder encoder;

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
    try {
      return jdbc.queryForObject(SELECT_USER_BY_ID_QUERY, of("id", id), new UserRowMapper());
    } catch (EmptyResultDataAccessException exception) {
      throw new ApiException("No User found by id: " + id);
    } catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again.");
    }
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

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = getUserByEmail(email);
    if(user == null) {
      log.error("User not found in the database");
      throw new UsernameNotFoundException("User not found in the database");
    } else {
      log.info("User found in the database: {}", email);
      return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()));
    }
  }

  @Override
  public User getUserByEmail(String email) {
    try {
        return jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, of("email", email), new UserRowMapper());
    } catch (EmptyResultDataAccessException exception) {
      throw new ApiException("No User found by email: " + email);
    } catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again.");
    }
  }

  @Override
  public User verifyCode(String email, String code) {
    if(isVerificationCodeExpired(code)) throw new ApiException("This code has expired. Please login again.");
    try {
      User userByCode = jdbc.queryForObject(SELECT_USER_BY_USER_CODE_QUERY, of("code", code), new UserRowMapper());
      User userByEmail = jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, of("email", email), new UserRowMapper());
      if(userByCode.getEmail().equalsIgnoreCase(userByEmail.getEmail())) {
        jdbc.update(DELETE_CODE, of("code", code));
        return userByCode;
      } else {
        throw new ApiException("Code is invalid. Please try again.");
      }
    } catch (EmptyResultDataAccessException exception) {
      throw new ApiException("Could not find record");
    } catch (Exception exception) {
      throw new ApiException("An error occurred. Please try again.");
    }
  }

  @Override
  public User verifyAccountKey(String key) {
    try {
      User user = jdbc.queryForObject(SELECT_USER_BY_ACCOUNT_URL_QUERY, of("url", getVerificationUrl(key, ACCOUNT.getType())), new UserRowMapper());
      jdbc.update(UPDATE_USER_ENABLED_QUERY, of("enabled", true, "id", user.getId()));
      // Delete after updating - depends on your requirements
      return user;
    } catch (EmptyResultDataAccessException exception) {
      throw new ApiException("This link is not valid.");
    } catch (Exception exception) {
      throw new ApiException("An error occurred. Please try again.");
    }
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

  @Override
  public void sendVerificationCode(UserDTO user) {
    LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);
    String verificationCode = randomAlphabetic(8).toUpperCase();
    try {
      jdbc.update(DELETE_VERIFICATION_CODE_BY_USER_ID, of("id", user.getId()));
      jdbc.update(INSERT_VERIFICATION_CODE_QUERY, of("userId", user.getId(), "code", verificationCode, "expirationDate", expirationDate));
      //sendSMS(user.getPhone(), "From: SecureCapita \nVerification code\n" + verificationCode);
      log.info("Verification Code: {}", verificationCode);
    } catch (Exception exception) {
      exception.printStackTrace();
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again.");
    }
  }

  @Override
  public void resetPassword(String email) {
    if(getEmailCount(email.trim().toLowerCase()) <= 0) throw new ApiException("There is no account for this email address.");
    try {
      LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);
      User user = getUserByEmail(email);
      String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), PASSWORD.getType());
      jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, of("userId",  user.getId()));
      jdbc.update(INSERT_PASSWORD_VERIFICATION_QUERY, of("userId",  user.getId(), "url", verificationUrl, "expirationDate", expirationDate));
      sendEmail(user.getFirstName(), email, verificationUrl, PASSWORD);
      log.info("Verification URL: {}", verificationUrl);
    } catch (Exception exception) {
      throw new ApiException("An error occurred. Please try again.");
    }
  }

  @Override
  public User verifyPasswordKey(String key) {
    if(isLinkExpired(key, PASSWORD)) throw new ApiException("This link has expired. Please reset your password again.");
    try {
      User user = jdbc.queryForObject(SELECT_USER_BY_PASSWORD_URL_QUERY, of("url", getVerificationUrl(key, PASSWORD.getType())), new UserRowMapper());
      //jdbc.update("DELETE_USER_FROM_PASSWORD_VERIFICATION_QUERY", of("id", user.getId())); //Depends on use case / developer or business
      return user;
    } catch (EmptyResultDataAccessException exception) {
      log.error(exception.getMessage());
      throw new ApiException("This link is not valid. Please reset your password again.");
    } catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again.");
    }
  }

  @Override
  public void renewPassword(String key, String password, String confirmPassword) {
    if(!password.equals(confirmPassword)) throw new ApiException("Passwords don't match. Please try again.");
    if(isLinkExist(key, PASSWORD)==0) throw new ApiException("This link has expired. Please reset your password again.");
    if (Boolean.TRUE.equals(isLinkExpired(key, PASSWORD)))
      throw new ApiException("This link has expired. Please reset your password again.");
    try {
      jdbc.update(UPDATE_USER_PASSWORD_BY_URL_QUERY, of("password", encoder.encode(password), "url", getVerificationUrl(key, PASSWORD.getType())));
      jdbc.update(DELETE_VERIFICATION_BY_URL_QUERY, of("url", getVerificationUrl(key, PASSWORD.getType())));
    } catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again.");
    }
  }
  @Override
  public void renewPassword(Long userId, String password, String confirmPassword) {
    if(!password.equals(confirmPassword)) throw new ApiException("Passwords don't match. Please try again.");
    try {
      jdbc.update(UPDATE_USER_PASSWORD_BY_USER_ID_QUERY, of("id", userId, "password", encoder.encode(password)));
      //jdbc.update(DELETE_PASSWORD_VERIFICATION_BY_USER_ID_QUERY, of("userId", userId));
    } catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again.");
    }
  }

  @Override
  public User updateUserDetails(UpdateForm user) {
    try {
      jdbc.update(UPDATE_USER_DETAILS_QUERY, getUserDetailsSqlParameterSource(user));
      return get(user.getId());
    }catch (EmptyResultDataAccessException exception) {
      throw new ApiException("No User found by id: " + user.getId());
    } catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again.");
    }
  }

  @Override
  public void updatePassword(Long id, String currentPassword, String newPassword, String confirmNewPassword) {
    if(!newPassword.equals(confirmNewPassword)) { throw new ApiException("Passwords don't match. Please try again."); }
    User user = get(id);
    if(encoder.matches(currentPassword, user.getPassword())) {
      try {
        jdbc.update(UPDATE_USER_PASSWORD_BY_ID_QUERY, of("userId", id, "password", encoder.encode(newPassword)));
      }  catch (Exception exception) {
        throw new ApiException("An error occurred. Please try again.");
      }
    } else {
      throw new ApiException("Incorrect current password. Please try again.");
    }
  }

  private Boolean isLinkExpired(String key, VerificationType password) {
    try {
      return jdbc.queryForObject(SELECT_EXPIRATION_BY_URL, of("url", getVerificationUrl(key, password.getType())), Boolean.class);
    } catch (EmptyResultDataAccessException exception) {
      log.error(exception.getMessage());
      throw new ApiException("This link is not valid. Please reset your password again");
    } catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again");
    }
  }

  private Integer isLinkExist(String key, VerificationType password) {
    try {
      return jdbc.queryForObject(SELECT_EXIST_BY_URL_QUERY, of("url", getVerificationUrl(key, password.getType())), Integer.class);
    } catch (EmptyResultDataAccessException exception) {
      log.error(exception.getMessage());
      throw new ApiException("This link is not valid. Please reset your password again");
    } catch (Exception exception) {
      log.error(exception.getMessage());
      throw new ApiException("An error occurred. Please try again");
    }
  }

  private boolean isVerificationCodeExpired(String code) {
    try {
      return Boolean.TRUE.equals(
          jdbc.queryForObject(SELECT_CODE_EXPIRATION_QUERY, of("code", code), Boolean.class));
    } catch (EmptyResultDataAccessException exception) {
      throw new ApiException("This code is not valid. Please login again.");
    } catch (Exception exception) {
      throw new ApiException("An error occurred. Please try again.");
    }
  }

  private SqlParameterSource getUserDetailsSqlParameterSource(UpdateForm user) {
    return new MapSqlParameterSource()
            .addValue("id", user.getId())
            .addValue("firstName", user.getFirstName())
            .addValue("lastName", user.getLastName())
            .addValue("email", user.getEmail())
            .addValue("phone", user.getPhone())
            .addValue("address", user.getAddress())
            .addValue("title", user.getTitle())
            .addValue("bio", user.getBio());
  }

  private void sendEmail(String firstName, String email, String verificationUrl, VerificationType verificationType) {
    //CompletableFuture.runAsync(() -> emailService.sendVerificationEmail(firstName, email, verificationUrl, verificationType));

        /*CompletableFuture.runAsync(() -> {
            try {
                emailService.sendVerificationEmail(firstName, email, verificationUrl, verificationType);
            } catch (Exception exception) {
                throw new ApiException("Unable to send email");
            }
        });*/

        /*CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                emailService.sendVerificationEmail(firstName, email, verificationUrl, verificationType);
            } catch (Exception exception) {
                throw new ApiException("Unable to send email");
            }
        });*/

        /*CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    emailService.sendVerificationEmail(firstName, email, verificationUrl, verificationType);
                } catch (Exception exception) {
                    throw new ApiException("Unable to send email");
                }
            }
        });*/
  }
}
