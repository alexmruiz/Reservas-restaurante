package com.example.crud.controller;


import com.example.crud.entity.AppUser;
import com.example.crud.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private  UserService userService;



    
    //Endpoints para operaciones CRUD de usuario

    @GetMapping("/list")
    public String viewUser(Model model) {
        try {
            List<AppUser> users = userService.allUser();
            model.addAttribute("userFront", users);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al mostrar los usuarios: " + e.getMessage());
            return "error";
        }
        return "crudUser";
    }

    @PostMapping("/saveuser")
    public String saveUser(@ModelAttribute AppUser appUser) {
        userService.saveUser(appUser);
        return "redirect:/list";
    }

    @PostMapping("/updateuser")
    public String updateUser(@ModelAttribute AppUser appUser) {
        userService.updateUser(appUser.getId(), appUser);
        return "redirect:/list";
    }

    @PostMapping("/deleteuser")
    public String deleteUser(@ModelAttribute AppUser appUser) {
        userService.deleteUser(appUser.getId());
        return "redirect:/list";
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
}
