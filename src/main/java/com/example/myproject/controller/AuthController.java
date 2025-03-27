package com.example.myproject.controller;

import com.example.myproject.dto.UserDto;
import com.example.myproject.entity.UserEntity;
import com.example.myproject.service.TokenService;
import com.example.myproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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
            // Получаем пользователя и проверяем пароль
            UserEntity user = userService.getUserByUsername(userDto.getUsername());
            if (!userService.checkPassword(user, userDto.getPassword())) {
                return ResponseEntity.badRequest().body("{\"error\": \"Неверное имя пользователя или пароль\"}");
            }

            // Генерируем токен для пользователя
            var tokenEntity = tokenService.createTokenForUser(user);

            // Создаем HTTP-only cookie с токеном
            Cookie cookie = new Cookie("rememberMeToken", tokenEntity.getToken());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60); // время жизни cookie - 7 дней
            response.addCookie(cookie);

            // Возвращаем данные пользователя (например, username и score)
            return ResponseEntity.ok().body("{\"username\": \"" + user.getUsername() + "\", \"score\": " + user.getGameStats().getScore() + "}");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"Неверное имя пользователя или пароль\"}");
        }
    }
}
