package com.example.crud.controller;

import com.example.crud.entity.RegisterDto;
import com.example.crud.entity.AppUser;
import com.example.crud.entity.Booking;
import com.example.crud.entity.LoginForm;
import com.example.crud.repository.UserRepository;
import com.example.crud.service.BookingService;

import jakarta.validation.Valid;


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
import java.util.List;


@Controller
public class CountController {

    
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BookingService bookingService;



    

    //Endpoints para autenticación y registro
    @GetMapping("/")
    public String login(Model model) {
        model.addAttribute("loginForm", new LoginForm()); // Agrega un nuevo objeto LoginForm al modelo
        return "login"; // Devuelve la vista del formulario de inicio de sesión
    }

    @GetMapping("/admin-restaurante")
    public String admin(Model model) {
        List<Booking> bookings = bookingService.getAllBookingsWithUserData();
        model.addAttribute("bookings", bookings);
        return "admin";
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
            newUser.setRole("ROLE_CLIENT");
            newUser.setCreateAt(new Date());
            newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));

            userRepository.save(newUser);

            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success", true);
            return "register";

        } catch (Exception ex) {
            result.addError(new FieldError("registerDto", "name", ex.getMessage()));
            return "register";
        }
    }
  

}
