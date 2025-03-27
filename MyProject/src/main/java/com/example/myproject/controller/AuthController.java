package com.example.myproject.controller;

import com.example.myproject.dto.UserDto;
import com.example.myproject.entity.UserEntity;
import com.example.myproject.service.TokenService;
import com.example.myproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDto userDto, HttpServletResponse response) {
        try {
            UserEntity user = userService.getUserByUsername(userDto.getUsername());
            if (!userService.checkPassword(user, userDto.getPassword())) {
                return ResponseEntity.status(401).body(Map.of("error", "Неверное имя пользователя или пароль"));
            }

            var tokenEntity = tokenService.createTokenForUser(user);

            Cookie cookie = new Cookie("rememberMeToken", tokenEntity.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60);
            cookie.setDomain("localhost");
            cookie.setSecure(false);
            response.addCookie(cookie);

            return ResponseEntity.ok(Map.of(
                    "username", user.getUsername(),
                    "score", user.getGameStats().getScore()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) {
        try {
            UserEntity user = userService.registerUser(userDto);
            return ResponseEntity.ok(Map.of("message", "Регистрация успешна"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rememberMeToken".equals(cookie.getName())) {
                    tokenService.removeToken(cookie.getValue());
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
        return ResponseEntity.ok("{\"message\": \"Вы вышли из аккаунта\"}");
    }
}
