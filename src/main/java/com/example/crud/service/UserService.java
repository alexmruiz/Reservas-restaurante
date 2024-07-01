package com.example.crud.service;

import com.example.crud.entity.AppUser;
import com.example.crud.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;


import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;

    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByEmail(email);

        if (appUser != null) {
           var springUser = User.withUsername(appUser.getEmail())
                .password(appUser.getPassword())
                .roles(appUser.getRole())
                .build();
                return springUser;            
    }

    return null;
        
    }
    
    // Obtiene todos los usuarios
    public List<AppUser> allUser() {
        return userRepository.findAll();
    }

    // Guarda un nuevo usuario
    public AppUser saveUser(AppUser appUser) {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword())); // Encriptar la contraseña antes de guardar
        return userRepository.save(appUser);
    }

    // Actualiza un usuario existente
    public AppUser updateUser(long id, AppUser appUser) {
        AppUser userExist = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        userExist.setName(appUser.getName());
        userExist.setLastname(appUser.getLastname());
        userExist.setEmail(appUser.getEmail());
        // Encriptar la contraseña solo si es nueva
        if (!appUser.getPassword().equals(userExist.getPassword())) {
            userExist.setPassword(passwordEncoder.encode(appUser.getPassword()));
        }
        return userRepository.save(userExist);
    }

    // Elimina un usuario
    public void deleteUser(long id) {
        AppUser userExist = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        userRepository.delete(userExist);
    }

    
    



   
}
