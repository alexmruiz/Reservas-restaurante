package com.example.crud.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import com.example.crud.entity.AppUser;


@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    public AppUser findByEmail(String email);// Método personalizado para encontrar por correo electrónico
    
    Page<AppUser> findByRole(String role, Pageable pageable);
    Page<AppUser> findByRoleAndNameContainingIgnoreCase(String role, String name, Pageable pageable);
}
