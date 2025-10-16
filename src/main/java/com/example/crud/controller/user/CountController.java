package com.example.crud.controller.user;

import com.example.crud.dto.auth.LoginRequest;
import com.example.crud.dto.auth.RegisterDto;
import com.example.crud.entity.user.AppUser;
import com.example.crud.service.booking.BookingService;
import com.example.crud.service.user.UserService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class CountController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String login(Model model) {
        model.addAttribute("loginForm", new LoginRequest());
        return "login";
    }

    @GetMapping("/admin-restaurante")
    public String adminRestaurant(Model model,
                                  @RequestParam(required = false) String filterDate,
                                  @RequestParam(required = false) String filterZone,
                                  Authentication authentication) {

        String email = authentication.getName();
        AppUser appUser = userService.findByEmail(email);

        model.addAttribute("appUser", appUser);
        model.addAttribute("zones", bookingService.getAllZones());
        model.addAttribute("bookings", bookingService.getFilteredBookings(filterDate, filterZone));
        model.addAttribute("filterDate", bookingService.resolveDate(filterDate));
        model.addAttribute("filterZone", filterZone);

        return "admin";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerDto", new RegisterDto());
        model.addAttribute("success", false);
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            Model model,
            @Valid @ModelAttribute("registerDto") RegisterDto registerDto,
            BindingResult result) {

        if (userService.validateRegisterData(registerDto, result)) {
            userService.createNewUser(registerDto);
            model.addAttribute("registerDto", new RegisterDto());
            model.addAttribute("success", true);
        }

        return "register";
    }
}
