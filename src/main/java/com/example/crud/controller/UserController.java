package com.example.crud.controller;


import com.example.crud.entity.AppUser;
import com.example.crud.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.NoSuchElementException;


@Controller
public class UserController {
    @Autowired
    private  UserService userService;
 


    
    //Endpoints para operaciones CRUD de usuario



    @PostMapping("/saveuser")
    public String saveUser(@ModelAttribute AppUser appUser) {
        userService.saveUser(appUser);
        return "redirect:/list";
    }

    @PostMapping("/deleteuser")
    public String deleteUser(@ModelAttribute AppUser appUser) {
        userService.deleteUser(appUser.getId());
        return "redirect:/";
    }


    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard"; // Vista para administradores
    }
    

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalidar la sesión actual
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Redirigir a la página de inicio de sesión u otra página
        return "redirect:/"; // ajusta el redireccionamiento según tu aplicación
    }

     @GetMapping("/perfil")
    public String profile(Authentication authentication, Model model){
        String email = authentication.getName(); // Obtener email del usuario
        AppUser appUser = userService.findByEmail(email);
        model.addAttribute("appUser", appUser);
        return "users-profile";
    }
  @PostMapping("/actualizar")
public String updateProfile(@ModelAttribute("appUser") AppUser appUser, Model model) {
    try {
        userService.updateUser(appUser);
        model.addAttribute("appUser", appUser);
        model.addAttribute("successMessage", "Perfil actualizado con éxito");
    } catch (IllegalArgumentException ex) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("appUser", appUser);
        return "users-profile";
    } catch (NoSuchElementException ex) {
        model.addAttribute("errorMessage", "Usuario no encontrado");
        model.addAttribute("appUser", appUser);
        return "users-profile";
    }

    return "users-profile";
}

    @PostMapping("/cambiar-password")
    public String changePassword(Authentication authentication,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("password") String password,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model) {
        String email = authentication.getName();

        // Llamar al servicio para cambiar la contraseña y obtener el mensaje de resultado
        String message = userService.changePassword(email, currentPassword, password, confirmPassword);

        // Cargar el usuario actualizado independientemente del resultado
        AppUser updatedUser = userService.findByEmail(email);
        if (updatedUser != null) {
            model.addAttribute("appUser", updatedUser);
        } else {
            model.addAttribute("errorMessage", "No se pudo cargar el usuario actualizado");
        }

        // Agregar el mensaje de éxito o error al modelo
        if (message.equals("Contraseña actualizada con éxito")) {
            model.addAttribute("successMessage", message);
        } else {
            model.addAttribute("errorMessage", message);
        }

        return "users-profile";
    }

    @GetMapping("/formulario")
    public String form(Authentication authentication, Model model){
        String email = authentication.getName(); // Obtener email del usuario
        AppUser appUser = userService.findByEmail(email);
        model.addAttribute("appUser", appUser);
        return"form";
    }

    @GetMapping("/history")
    public String insertUserWithRoleRest() {
        AppUser newUser = new AppUser();
        newUser.setName("RestauranteFGZ");
        newUser.setLastname("Restaurante");
        newUser.setEmail("rest@rest.com");
        newUser.setPassword("987654321"); // La contraseña se codificará automáticamente en el servicio
        newUser.setRole("ROLE_REST");
        newUser.setCreateAt(new Date());
        newUser.setPhone("123456789");

        userService.saveUserr(newUser);

        return "Usuario con ROLE_REST insertado correctamente";
    }

   @PostMapping("/usuarios/desactivar")
@ResponseBody
public ResponseEntity<String> deactivateUser(@RequestParam long userId, @RequestParam boolean active) {
    try {
        userService.setActiveStatus(userId, active);
        String message = active ? "Usuario activado con éxito" : "Usuario desactivado con éxito";
        return ResponseEntity.ok(message);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cambiar el estado del usuario");
    }
}
}




