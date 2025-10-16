package com.example.crud.controller.user;

import com.example.crud.entity.user.AppUser;
import com.example.crud.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

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
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/perfil")
    public String profile(Authentication authentication, Model model) {
        AppUser appUser = userService.findByEmail(authentication.getName());
        model.addAttribute("appUser", appUser);
        return "users-profile";
    }

    @PostMapping("/actualizar")
    public String updateProfile(@ModelAttribute("appUser") AppUser appUser, Model model) {
        return userService.processProfileUpdate(appUser, model);
    }

    @PostMapping("/cambiar-password")
    public String changePassword(Authentication authentication,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("password") String password,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model) {
        String email = authentication.getName();
        return userService.processPasswordChange(email, currentPassword, password, confirmPassword, model);
    }

    @GetMapping("/formulario")
    public String form(Authentication authentication, Model model) {
        model.addAttribute("appUser", userService.findByEmail(authentication.getName()));
        return "form";
    }

    @GetMapping("/history")
    public String insertUserWithRoleRest() {
        userService.createRestauranteDefaultUser();
        return "Usuario con ROLE_REST insertado correctamente";
    }

    @PostMapping("/usuarios/desactivar")
    @ResponseBody
    public ResponseEntity<String> deactivateUser(@RequestParam long userId, @RequestParam boolean active) {
        return userService.toggleUserActive(userId, active);
    }
}
