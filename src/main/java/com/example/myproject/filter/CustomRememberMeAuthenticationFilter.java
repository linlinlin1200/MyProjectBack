package com.example.myproject.filter;

import com.example.myproject.entity.TokenEntity;
import com.example.myproject.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

public class CustomRememberMeAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    public CustomRememberMeAuthenticationFilter(TokenService tokenService,
                                                UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Cookie tokenCookie = Arrays.stream(cookies)
                        .filter(cookie -> "rememberMeToken".equals(cookie.getName()))
                        .findFirst()
                        .orElse(null);
                if (tokenCookie != null) {
                    String tokenValue = tokenCookie.getValue();
                    TokenEntity tokenEntity = tokenService.findByToken(tokenValue);
                    if (tokenEntity != null && tokenEntity.getExpiryDate().isAfter(LocalDateTime.now())) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(tokenEntity.getUser().getUsername());
                        if (userDetails != null) {
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
