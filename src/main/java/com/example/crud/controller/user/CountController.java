package com.example.crud.controller.user;

import com.example.crud.entity.booking.Booking;
import com.example.crud.entity.user.AppUser;
import com.example.crud.repository.user.UserRepository;
import com.example.crud.dto.auth.LoginRequest;
import com.example.crud.dto.auth.RegisterDto;
import com.example.crud.service.booking.BookingService;
import com.example.crud.service.user.UserService;

import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private UserService userService;


    //Endpoints para autenticación y registro
    @GetMapping("/")
    public String login(Model model) {
        model.addAttribute("loginForm", new LoginRequest()); // Agrega un nuevo objeto LoginForm al modelo
        return "login"; // Devuelve la vista del formulario de inicio de sesión
    }

    
    @GetMapping("/admin-restaurante")
    public String adminRestaurant(Model model, 
                                  @RequestParam(required = false) String filterDate, 
                                  @RequestParam(required = false) String filterZone) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Use email as the username
        AppUser appUser = userService.findByEmail(email); // Find user by email
        model.addAttribute("appUser", appUser);
    
        List<String> zones = bookingService.getAllZones(); // Assuming you have a method to get all zones
        model.addAttribute("zones", zones);
    
        // If no date is provided, use today's date
        if (filterDate == null || filterDate.isEmpty()) {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            filterDate = today.format(formatter);
        }
    
        List<Booking> bookings;
        if (filterZone == null || filterZone.isEmpty()) {
            bookings = bookingService.getBookingsByDate(filterDate);
        } else {
            bookings = bookingService.getBookingsByDateAndZone(filterDate, filterZone);
        }
    
        model.addAttribute("bookings", bookings);
        model.addAttribute("filterDate", filterDate);
        model.addAttribute("filterZone", filterZone);
    
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
            newUser.setPhone(registerDto.getPhone());
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
