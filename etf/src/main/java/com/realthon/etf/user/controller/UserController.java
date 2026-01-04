package com.realthon.etf.user.controller;

import com.realthon.etf.user.dto.request.CreateUserRequest;
import com.realthon.etf.user.dto.request.UpdateUserRequest;
import com.realthon.etf.user.dto.response.UserResponse;
import com.realthon.etf.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /*
    회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {
        String loginId = userDetails.getUsername();

        UserResponse response = userService.getMyProfileByLoginId(loginId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        String loginId = userDetails.getUsername();
        UserResponse response = userService.updateMyProfile(loginId, request);
        return ResponseEntity.ok(response);
    }
}
