package com.example.crud.service.user;

import com.example.crud.dto.auth.RegisterDto;
import com.example.crud.entity.user.AppUser;
import com.example.crud.repository.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.Date;
import java.util.NoSuchElementException;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Carga un usuario por su email para autenticación.
     * @param email
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = userRepository.findByEmail(email);
        if (appUser == null) throw new UsernameNotFoundException("User not found");

        return User.withUsername(appUser.getEmail())
                .password(appUser.getPassword())
                .authorities(Collections.singletonList(() -> appUser.getRole()))
                .build();
    }

    /**
     * Busca usuarios por rol con paginación.
     * @param role
     * @param pageable
     * @return
     */
    public Page<AppUser> findByRole(String role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    /**
     * Busca usuarios por rol y nombre (parcial, case insensitive) con paginación.
     * @param role
     * @param name
     * @param pageable
     * @return
     */
    public Page<AppUser> searchByName(String role, String name, Pageable pageable) {
        return userRepository.findByRoleAndNameContainingIgnoreCase(role, name, pageable);
    }

    /**
     * Guarda un nuevo usuario, encriptando su contraseña y estableciendo la fecha de creación.
     * @param appUser
     * @return
     */
    public AppUser saveUser(AppUser appUser) {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setCreateAt(new Date());
        return userRepository.save(appUser);
    }

    /**
     * Elimina un usuario por su ID.
     * @param id
     */
    public void deleteUser(long id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        userRepository.delete(user);
    }

    /**
     * Busca un usuario por su email.
     * @param email
     * @return
     */
    public AppUser findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Procesa la actualización del perfil y actualiza el modelo para la vista.
     * @param appUser
     * @param model
     * @return
     */
    public String processProfileUpdate(AppUser appUser, Model model) {
        try {
            AppUser updated = updateUser(appUser);
            model.addAttribute("appUser", updated);
            model.addAttribute("successMessage", "Perfil actualizado con éxito");
        } catch (IllegalArgumentException | NoSuchElementException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("appUser", appUser);
        }
        return "users-profile";
    }

    /**
     * Actualiza los datos del usuario, validando que el email no esté en uso por otro.
     * @param appUser
     * @return
     */
    private AppUser updateUser(AppUser appUser) {
        AppUser existing = userRepository.findById(appUser.getId())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        AppUser emailUser = userRepository.findByEmail(appUser.getEmail());
        if (emailUser != null && emailUser.getId() != appUser.getId()) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso.");
        }

        existing.setName(appUser.getName());
        existing.setLastname(appUser.getLastname());
        existing.setEmail(appUser.getEmail());
        existing.setPhone(appUser.getPhone());
        return userRepository.save(existing);
    }

    /**
     * Procesa el cambio de contraseña y actualiza el modelo para la vista.
     * @param email
     * @param currentPassword
     * @param newPassword
     * @param confirmPassword
     * @param model
     * @return
     */
    public String processPasswordChange(String email, String currentPassword,
                                        String newPassword, String confirmPassword, Model model) {
        String message = changePassword(email, currentPassword, newPassword, confirmPassword);
        AppUser updatedUser = findByEmail(email);
        model.addAttribute("appUser", updatedUser);
        if (message.equals("Contraseña actualizada con éxito"))
            model.addAttribute("successMessage", message);
        else
            model.addAttribute("errorMessage", message);
        return "users-profile";
    }

    /**
     * Cambia la contraseña de un usuario si la actual es correcta y las nuevas coinciden.
     * @param email
     * @param currentPassword
     * @param newPassword
     * @param confirmPassword
     * @return
     */
    private String changePassword(String email, String currentPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) return "Las nuevas contraseñas no coinciden";

        AppUser appUser = userRepository.findByEmail(email);
        if (appUser == null || !passwordEncoder.matches(currentPassword, appUser.getPassword()))
            return "La contraseña actual es incorrecta";

        appUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(appUser);
        return "Contraseña actualizada con éxito";
    }

    /**
     * Valida los datos del formulario de registro.
     * @param dto
     * @param result
     * @return
     */
    public boolean validateRegisterData(RegisterDto dto, BindingResult result) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.addError(new FieldError("registerDto", "confirmPassword", "No coinciden las contraseñas"));
        }
        if (userRepository.findByEmail(dto.getEmail()) != null) {
            result.addError(new FieldError("registerDto", "email", "Este email ya está en uso"));
        }
        return !result.hasErrors();
    }

    /**
     * Crea un nuevo usuario con rol CLIENTE a partir de los datos del DTO.
     * @param dto
     */
    public void createNewUser(RegisterDto dto) {
        AppUser user = new AppUser();
        user.setName(dto.getName());
        user.setLastname(dto.getLastname());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole("ROLE_CLIENT");
        user.setCreateAt(new Date());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(user);
    }

    /**
     * Crea un usuario por defecto para el restaurante si no existe.
     * Se puede llamar al iniciar la aplicación.
     */
    public void createRestauranteDefaultUser() {
        AppUser user = new AppUser();
        user.setName("RestauranteFGZ");
        user.setLastname("Restaurante");
        user.setEmail("rest@rest.com");
        user.setPassword(passwordEncoder.encode("987654321"));
        user.setRole("ROLE_REST");
        user.setCreateAt(new Date());
        user.setPhone("123456789");
        userRepository.save(user);
    }

    /**
     * Activa o desactiva un usuario.
     * @param userId
     * @param active
     * @return
     */
    public ResponseEntity<String> toggleUserActive(long userId, boolean active) {
        try {
            AppUser user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            user.setActive(active);
            userRepository.save(user);
            String message = active ? "Usuario activado con éxito" : "Usuario desactivado con éxito";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al cambiar el estado del usuario");
        }
    }
}
