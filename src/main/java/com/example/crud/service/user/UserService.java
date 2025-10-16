package com.example.crud.service.user;

import com.example.crud.entity.user.AppUser;
import com.example.crud.repository.user.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
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

        if (appUser == null) {
            throw new UsernameNotFoundException("User not found");
        }

        var springUser = User.withUsername(appUser.getEmail())
                .password(appUser.getPassword())
                .authorities(Collections.singleton(new SimpleGrantedAuthority(appUser.getRole())))
                .build();

        return springUser;
    }
    /************************Métodos para ver los clientes******** */
    public Page<AppUser> findByRole(String role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    public Page<AppUser> searchByName(String role, String name, Pageable pageable) {
        return userRepository.findByRoleAndNameContainingIgnoreCase(role, name, pageable);
    }
    /******************************************************************* */
    // Guarda un nuevo usuario
    public AppUser saveUser(AppUser appUser) {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword())); // Encriptar la contraseña antes de guardar
        return userRepository.save(appUser);
    }

  // Actualiza un usuario existente
    public AppUser updateUser(AppUser appUser) {
    AppUser userExist = userRepository.findById(appUser.getId())
            .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

    // Verificar si el correo electrónico ya existe y pertenece a otro usuario
    AppUser existingUser = userRepository.findByEmail(appUser.getEmail());
    if (existingUser != null && existingUser.getId() != appUser.getId()) {
        throw new IllegalArgumentException("El correo electrónico ya está en uso.");
    }

    userExist.setName(appUser.getName());
    userExist.setLastname(appUser.getLastname());
    userExist.setEmail(appUser.getEmail());
    userExist.setPhone(appUser.getPhone()); // Asegúrate de tener este campo en la entidad

    return userRepository.save(userExist);
}


    // Elimina un usuario
    public void deleteUser(long id) {
        AppUser userExist = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        userRepository.delete(userExist);
    }

    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public String changePassword(String email, String currentPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            return "Las nuevas contraseñas no coinciden";
        }

        AppUser appUser = userRepository.findByEmail(email);
        if (appUser == null || !passwordEncoder.matches(currentPassword, appUser.getPassword())) {
            return "La contraseña actual es incorrecta";
        }

        appUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(appUser);
        return "Contraseña actualizada con éxito";
    }

    public AppUser saveUserr(AppUser appUser) {
        // Codificar la contraseña antes de guardar
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));

        return userRepository.save(appUser);
    }

    /*******************************************DESACTIVAR USUARIO************************** */
    
    public void setActiveStatus(long userId, boolean active) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setActive(active);
        userRepository.save(user);
    }


    

    
    



   
}
