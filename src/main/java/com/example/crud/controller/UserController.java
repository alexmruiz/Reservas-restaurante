package com.example.crud.controller;


import com.example.crud.entity.AppUser;
import com.example.crud.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
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

    //Endpoints 
    @GetMapping("/reservas")
    public String index() {
        logger.debug("Accediendo index");
        return "reservas"; // Vista para usuarios
    }


    @GetMapping("/dashboard")
    public String dashboardPage() {
        return "dashboard"; // Vista para administradores
    }
    
}
