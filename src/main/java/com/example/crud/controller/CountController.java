package com.example.crud.controller;

import com.example.crud.entity.RegisterDto;
import com.example.crud.entity.AppUser;
import com.example.crud.entity.LoginForm;
import com.example.crud.repository.UserRepository;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.Date;


@Controller
public class CountController {

    
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    //Endpoints para autenticación y registro
    @GetMapping("/")
    public String login(Model model) {
        logger.debug("Accediendo a la página de inicio de sesión.");
        model.addAttribute("loginForm", new LoginForm()); // Agrega un nuevo objeto LoginForm al modelo
        return "login"; // Devuelve la vista del formulario de inicio de sesión
    }
    

    @GetMapping("/register")
    public String register(Model model) {
        RegisterDto registerDto = new RegisterDto();
        model.addAttribute(registerDto);
        model.addAttribute("success", false);
        return "register";//Devuelve la vista de registro
    }

    @PostMapping("/register")
    public String register(
            Model model,
            @Valid @ModelAttribute("registerDto") RegisterDto registerDto,
            BindingResult result
    ) {
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            result.addError(new FieldError("registerDto", "confirmPassword", "No coinciden las dos contraseñas"));
        }

        AppUser appUser = userRepository.findByEmail(registerDto.getEmail());
        if (appUser != null) {
            result.addError(new FieldError("registerDto", "email", "Este email ya está en uso"));
        }

        if (result.hasErrors()) {
            return "register";
        }

        try {
            // Crea nueva cuenta
            AppUser newUser = new AppUser();
            newUser.setName(registerDto.getName());
            newUser.setLastname(registerDto.getLastname());
            newUser.setEmail(registerDto.getEmail());
            newUser.setRole("client");
            newUser.setCreateAt(new Date());
            newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));

            userRepository.save(newUser);

            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success", true);
            return "redirect:/";

        } catch (Exception ex) {
            result.addError(new FieldError("registerDto", "name", ex.getMessage()));
            return "register";
        }
    }
  

}
