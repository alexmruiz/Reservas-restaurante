package com.example.crud.entity;

import jakarta.validation.constraints.NotEmpty;

public class LoginForm {
    @NotEmpty(message = "El correo electrónico es obligatorio")
    private String email;

    @NotEmpty(message = "La contraseña es obligatoria")
    private String password;

    // Getters y setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

