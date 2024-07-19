package com.example.crud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.crud.entity.AppUser;


@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    public AppUser findByEmail(String email);// Método personalizado para encontrar por correo electrónico
     
   
}
