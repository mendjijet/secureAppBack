package com.jet.com.secureappback.controller;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath;

import com.jet.com.secureappback.domain.HttpResponse;
import com.jet.com.secureappback.domain.User;
import com.jet.com.secureappback.dto.UserDTO;
import com.jet.com.secureappback.services.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
@RequiredArgsConstructor
public class UserController {
    private static final String TOKEN_PREFIX = "Bearer ";
    private final UserService userService;
    //private final RoleService roleService;

//    @PostMapping("/login")
//    public ResponseEntity<HttpResponse> login(@RequestBody @Valid LoginForm loginForm) {
//        UserDTO user = authenticate(loginForm.getEmail(), loginForm.getPassword());
//        return user.isUsingMfa() ? sendVerificationCode(user) : sendResponse(user);
//    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> saveUser(@RequestBody @Valid User user) throws InterruptedException {
        TimeUnit.SECONDS.sleep(4);
        UserDTO userDto = userService.createUser(user);
        return ResponseEntity.created(getUri()).body(
                HttpResponse.builder()
                        .timeStamp(now().toString())
                        .data(of("user", userDto))
                        .message(String.format("User account created for user %s", user.getFirstName()))
                        .status(CREATED)
                        .statusCode(CREATED.value())
                        .build());
    }

    private URI getUri() {
        return URI.create(fromCurrentContextPath().path("/user/get/<userId>").toUriString());
    }
}
