package com.example.crud.entity.user;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.crud.entity.booking.Booking;

@Entity
@Table(name="users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String lastname;
    
    @Column(unique = true, nullable = false)
    private String email; // Campo para el correo electrónico

    private String password; // Campo para la contraseña
    private String confirmPassword;
    private String role;
    private Date createAt;
    private String phone;
    private Boolean active = true;

    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL)
    private List<Booking> bookings = new ArrayList<>();
    
    public AppUser() {
    }

    // Getters y Setters

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public String toString() {
        return "AppUser [id=" + id + ", name=" + name + ", lastname=" + lastname + ", email=" + email + ", password="
                + password + ", confirmPassword=" + confirmPassword + ", role=" + role + ", createAt=" + createAt
                + ", phone=" + phone + ", bookings=" + bookings + "]";
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
