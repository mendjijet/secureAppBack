package com.jet.com.secureappback.controller;

import static com.jet.com.secureappback.dtomapper.UserDTOMapper.toUser;
import static com.jet.com.secureappback.enums.EventType.*;
import static com.jet.com.secureappback.utils.ExceptionUtils.processError;
import static com.jet.com.secureappback.utils.SecureAppBackApplicationConst.TOKEN_PREFIX;
import static com.jet.com.secureappback.utils.UserUtils.getAuthenticatedUser;
import static com.jet.com.secureappback.utils.UserUtils.getLoggedInUser;
import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

import com.jet.com.secureappback.domain.HttpResponse;
import com.jet.com.secureappback.domain.User;
import com.jet.com.secureappback.domain.UserPrincipal;
import com.jet.com.secureappback.dto.UserDTO;
import com.jet.com.secureappback.event.NewUserEvent;
import com.jet.com.secureappback.exception.ApiException;
import com.jet.com.secureappback.form.LoginForm;
import com.jet.com.secureappback.form.SettingsForm;
import com.jet.com.secureappback.form.UpdateForm;
import com.jet.com.secureappback.form.UpdatePasswordForm;
import com.jet.com.secureappback.provider.TokenProvider;
import com.jet.com.secureappback.services.EventService;
import com.jet.com.secureappback.services.RoleService;
import com.jet.com.secureappback.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final RoleService roleService;
  private final EventService eventService;
  private final AuthenticationManager authenticationManager;
  private final TokenProvider tokenProvider;
  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final ApplicationEventPublisher publisher;

  @PostMapping("/login")
  public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
    UserDTO userDTO = authenticate(loginForm.getEmail(), loginForm.getPassword());
    return userDTO.isUsingMfa() ? sendVerificationCode(userDTO) : sendResponse(userDTO);
  }

  @PostMapping("/register")
  public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user) {
    UserDTO userDto = userService.createUser(user);
    return ResponseEntity.created(getUri())
        .body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .data(of("user", userDto))
                .message(String.format("User account created for user %s", user.getFirstName()))
                .status(CREATED)
                .statusCode(CREATED.value())
                .build());
  }

  @GetMapping("/profile")
  public ResponseEntity<HttpResponse> profile(Authentication authentication) {
    UserDTO user = userService.getUserByEmail(getAuthenticatedUser(authentication).getEmail());
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .data(
                    of(
                        "user",
                        user,
                        "events",
                        eventService.getEventsByUserId(user.getId()),
                        "roles",
                        roleService.getRoles()))
                .message("Profile Retrieved")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }

  @PatchMapping("/update")
  public ResponseEntity<HttpResponse> updateUser(@RequestBody @Valid UpdateForm user) {
    UserDTO updatedUser = userService.updateUserDetails(user);
    publisher.publishEvent(new NewUserEvent(updatedUser.getEmail(), PROFILE_UPDATE));
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .data(
                    of(
                        "user",
                        updatedUser,
                        "events",
                        eventService.getEventsByUserId(user.getId()),
                        "roles",
                        roleService.getRoles()))
                .message("User updated")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }

  @PatchMapping("/update/role/{roleName}")
  public ResponseEntity<HttpResponse> updateUserRole(
      Authentication authentication, @PathVariable("roleName") String roleName) {
    UserDTO userDTO = getAuthenticatedUser(authentication);
    userService.updateUserRole(userDTO.getId(), roleName);
    publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), ROLE_UPDATE));
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .data(
                    of(
                        "user",
                        userService.getUserById(userDTO.getId()),
                        "events",
                        eventService.getEventsByUserId(userDTO.getId()),
                        "roles",
                        roleService.getRoles()))
                .timeStamp(now().toString())
                .message("Role updated successfully")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }

  @PatchMapping("/update/settings")
  public ResponseEntity<HttpResponse> updateAccountSettings(
      Authentication authentication, @RequestBody @Valid SettingsForm form) {
    UserDTO userDTO = getAuthenticatedUser(authentication);
    userService.updateAccountSettings(userDTO.getId(), form.getEnabled(), form.getNotLocked());
    publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), ACCOUNT_SETTINGS_UPDATE));
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .data(
                    of(
                        "user",
                        userService.getUserById(userDTO.getId()),
                        "events",
                        eventService.getEventsByUserId(userDTO.getId()),
                        "roles",
                        roleService.getRoles()))
                .timeStamp(now().toString())
                .message("Account settings updated successfully")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }

  @PatchMapping("/togglemfa")
  public ResponseEntity<HttpResponse> toggleMfa(Authentication authentication) {
    UserDTO user = userService.toggleMfa(getAuthenticatedUser(authentication).getEmail());
    publisher.publishEvent(new NewUserEvent(user.getEmail(), MFA_UPDATE));
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .data(
                    of(
                        "user",
                        user,
                        "events",
                        eventService.getEventsByUserId(user.getId()),
                        "roles",
                        roleService.getRoles()))
                .timeStamp(now().toString())
                .message("Multi-Factor Authentication updated")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }

  @PatchMapping(value = "/update/image")
  public ResponseEntity<HttpResponse> updateProfileImage(
      Authentication authentication, @RequestParam("image") MultipartFile image) {
    UserDTO user = getAuthenticatedUser(authentication);
    userService.updateImage(user, image);
    publisher.publishEvent(new NewUserEvent(user.getEmail(), PROFILE_PICTURE_UPDATE));
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .data(
                    of(
                        "user",
                        userService.getUserById(user.getId()),
                        "events",
                        eventService.getEventsByUserId(user.getId()),
                        "roles",
                        roleService.getRoles()))
                .timeStamp(now().toString())
                .message("Profile image updated")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }

  @GetMapping(value = "/image/{fileName}", produces = IMAGE_PNG_VALUE)
  public byte[] getProfileImage(@PathVariable("fileName") String fileName) throws Exception {
    return Files.readAllBytes(
        Paths.get(System.getProperty("user.home") + "/Downloads/images/" + fileName));
  }

  @GetMapping("/verify/code/{email}/{code}")
  public ResponseEntity<HttpResponse> verifyCode(
      @PathVariable("email") String email, @PathVariable("code") String code) {
    UserDTO user = userService.verifyCode(email, code);
    publisher.publishEvent(new NewUserEvent(user.getEmail(), LOGIN_ATTEMPT_SUCCESS));
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .data(
                    of(
                        "user",
                        user,
                        "access_token",
                        tokenProvider.createAccessToken(getUserPrincipal(user)),
                        "refresh_token",
                        tokenProvider.createRefreshToken(getUserPrincipal(user))))
                .message("Login Success")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }

  // START - To reset password when user is not logged in

  @GetMapping("/resetpassword/{email}")
  public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) {
    userService.resetPassword(email);
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .message("Email sent. Please check your email to reset your password.")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }

  @GetMapping("/verify/account/{key}")
  public ResponseEntity<HttpResponse> verifyAccount(@PathVariable("key") String key) {
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .message(
                    userService.verifyAccountKey(key).isEnabled()
                        ? "Account already verified"
                        : "Account verified")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }

  @GetMapping("/verify/password/{key}")
  public ResponseEntity<HttpResponse> verifyPasswordUrl(@PathVariable("key") String key) {
    UserDTO user = userService.verifyPasswordKey(key);
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .data(of("user", user))
                .message("Please enter a new password")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }

  @PostMapping("/resetpassword/{key}/{newpassword}/{confirmPassword}")
  public ResponseEntity<HttpResponse> resetPasswordWithKey(
      @PathVariable("key") String key,
      @PathVariable("newpassword") String newpassword,
      @PathVariable("confirmPassword") String confirmPassword) {
    userService.renewPassword(key, newpassword, confirmPassword);
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .message("Password reset successfully")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }

  // END - To reset password when user is not logged in

  @PatchMapping("/update/password")
  public ResponseEntity<HttpResponse> updatePassword(
      Authentication authentication, @RequestBody @Valid UpdatePasswordForm form) {
    UserDTO userDTO = getAuthenticatedUser(authentication);
    userService.updatePassword(
        userDTO.getId(),
        form.getCurrentPassword(),
        form.getNewPassword(),
        form.getConfirmNewPassword());
    publisher.publishEvent(new NewUserEvent(userDTO.getEmail(), PASSWORD_UPDATE));
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .data(
                    of(
                        "user",
                        userService.getUserById(userDTO.getId()),
                        "events",
                        eventService.getEventsByUserId(userDTO.getId()),
                        "roles",
                        roleService.getRoles()))
                .message("Password updated successfully")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }

  @GetMapping("/refresh/token")
  public ResponseEntity<HttpResponse> refreshToken(HttpServletRequest request) {
    if (isHeaderAndTokenValid(request)) {
      String token = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
      UserDTO user = userService.getUserById(tokenProvider.getSubject(token, request));
      return ResponseEntity.ok()
          .body(
              HttpResponse.builder()
                  .timeStamp(now().toString())
                  .data(
                      of(
                          "user",
                          user,
                          "access_token",
                          tokenProvider.createAccessToken(getUserPrincipal(user)),
                          "refresh_token",
                          token))
                  .message("Token refreshed")
                  .status(OK)
                  .statusCode(OK.value())
                  .build());
    } else {
      return ResponseEntity.badRequest()
          .body(
              HttpResponse.builder()
                  .timeStamp(now().toString())
                  .reason("Refresh Token missing or invalid")
                  .developerMessage("Refresh Token missing or invalid")
                  .status(BAD_REQUEST)
                  .statusCode(BAD_REQUEST.value())
                  .build());
    }
  }

  private boolean isHeaderAndTokenValid(HttpServletRequest request) {
    return request.getHeader(AUTHORIZATION) != null
        && request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
        && tokenProvider.isTokenValid(
            tokenProvider.getSubject(
                request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()), request),
            request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()));
  }

  @RequestMapping("/error")
  public ResponseEntity<HttpResponse> handleError(HttpServletRequest request) {
    return ResponseEntity.badRequest()
        .body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .reason(
                    "There is no mapping for a "
                        + request.getMethod()
                        + " request for this path on the server")
                .status(BAD_REQUEST)
                .statusCode(BAD_REQUEST.value())
                .build());
  }

  private UserDTO authenticate(String email, String password) {
    UserDTO userByEmail = userService.getUserByEmail(email);
    try {
      if (null != userByEmail) {
        publisher.publishEvent(new NewUserEvent(email, LOGIN_ATTEMPT));
      }
      Authentication authentication =
          authenticationManager.authenticate(unauthenticated(email, password));
      UserDTO loggedInUser = getLoggedInUser(authentication);
      if (!loggedInUser.isUsingMfa()) {
        publisher.publishEvent(new NewUserEvent(email, LOGIN_ATTEMPT_SUCCESS));
      }
      return loggedInUser;
    } catch (Exception exception) {
      if (null != userByEmail) {
        publisher.publishEvent(new NewUserEvent(email, LOGIN_ATTEMPT_FAILURE));
      }
      processError(request, response, exception);
      throw new ApiException(exception.getMessage());
    }
  }

  private URI getUri() {
    return URI.create(fromCurrentContextPath().path("/user/get/<userId>").toUriString());
  }

  private ResponseEntity<HttpResponse> sendResponse(UserDTO user) {
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .data(
                    of(
                        "user",
                        user,
                        "access_token",
                        tokenProvider.createAccessToken(getUserPrincipal(user)),
                        "refresh_token",
                        tokenProvider.createRefreshToken(getUserPrincipal(user))))
                .message("Login Success")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }

  private UserPrincipal getUserPrincipal(UserDTO user) {
    return new UserPrincipal(
        toUser(userService.getUserByEmail(user.getEmail())),
        roleService.getRoleByUserId(user.getId()));
  }

  private ResponseEntity<HttpResponse> sendVerificationCode(UserDTO user) {
    userService.sendVerificationCode(user);
    return ResponseEntity.ok()
        .body(
            HttpResponse.builder()
                .timeStamp(now().toString())
                .data(of("user", user))
                .message("Verification Code Sent")
                .status(OK)
                .statusCode(OK.value())
                .build());
  }
}
