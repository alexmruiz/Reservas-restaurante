package com.example.crud.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "app_user_id", nullable = false)
    private AppUser appUser;

    private String name;
    private String lastName;
    private int persons;
    private String zone;
    private LocalDate date;
    private LocalTime time;
    private LocalDateTime createAt;

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }
    
    //Constructor
    public Booking() {
        this.createAt = LocalDateTime.now();
    }

    //Getters y setters
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public int getPersons() {
        return persons;
    }


    public void setPersons(int persons) {
        this.persons = persons;
    }


    public String getZone() {
        return zone;
    }


    public void setZone(String zone) {
        this.zone = zone;
    }


    public LocalDate getDate() {
        return date;
    }


    public void setDate(LocalDate date) {
        this.date = date;
    }


    public LocalTime getTime() {
        return time;
    }


    public void setTime(LocalTime time) {
        this.time = time;
    }


    public LocalDateTime getCreateAt() {
        return createAt;
    }


    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    

    
}
