package com.example.crud.dto.auth;

import jakarta.validation.constraints.*;

public class RegisterDto {

    @NotEmpty(message = "El nombre es obligatorio")
    private String name;

    private String lastname;

    @NotEmpty(message = "El correo es obligatorio")
    @Email(message = "Debe ser una dirección de correo electrónico válida")
    private String email;

    private String phone;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotEmpty(message = "Debe confirmar la contraseña")
    private String confirmPassword;

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
