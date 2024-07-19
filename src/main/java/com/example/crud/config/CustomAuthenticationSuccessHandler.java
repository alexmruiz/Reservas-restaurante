package com.example.crud.config;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String redirectUrl = null;

        System.out.println("Authorities: " + authentication.getAuthorities());

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            System.out.println("Authority: " + authority.getAuthority());
            if (authority.getAuthority().equals("ROLE_REST")) {
                redirectUrl = "/admin-restaurante";
                break;
            } else if (authority.getAuthority().equals("ROLE_CLIENT")) {
                redirectUrl = "/reservas";
                break;
            }
        }

        if (redirectUrl == null) {
            // Si no se encontró ningún rol válido, se redirige a una página de error o a la página de inicio
            redirectUrl = "/error"; // Por ejemplo, redirigir a la página principal de reservas
        }

        System.out.println("Redirecting to: " + redirectUrl);
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}
