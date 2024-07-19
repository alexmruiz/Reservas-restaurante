package com.example.crud.controller;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.crud.entity.AppUser;
import com.example.crud.entity.Booking;
import com.example.crud.service.BookingService;
import com.example.crud.service.UserService;


@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public String createBooking(@ModelAttribute("booking") Booking booking, Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        AppUser appUser = userService.findByEmail(email);
        booking.setAppUser(appUser);

        try {
            bookingService.saveBooking(booking);
            return "redirect:/historial";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "reservas";
        }
    }

//Endpoints 
@GetMapping("/reservas")
public String index(Model model) {
    // Obtener usuario autenticado
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    AppUser appUser = userService.findByEmail(email);

    // Agregar appUser al modelo
    model.addAttribute("appUser", appUser);

      // Horarios para el select
      List<String> horarios = generateHorarios();
      model.addAttribute("horarios", horarios);

    return "reservas"; // Vista para usuarios
}

@GetMapping("/historial")
public String showBookingHistory(Model model) {
    //Obtener al usuario
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    AppUser appUser = userService.findByEmail(email);
    // Obtener las reservas del usuario autenticado
    List<Booking> bookings = bookingService.findBookingsForUser();
    System.out.println("Reservas asociadas al usuario: " + bookings);

      // Agregar appUser y bookings al modelo
      model.addAttribute("appUser", appUser);
      model.addAttribute("bookings", bookings);
      
    System.out.println("Todas las reservas de usuario son: " + bookings);
    return "historial"; // Nombre de la vista historial.html
}

@GetMapping("/deleteBookings")
public String deleteBookings(@RequestParam("ids") List<Long> ids){
    bookingService.deleteBooking(ids);
    return "redirect:/historial";
}

@GetMapping("editBooking")
public String editBooking(@RequestParam("id") Long id, Model model) {
    Booking booking = bookingService.findBookingById(id);
    // Horarios para el select
    List<String> horarios = generateHorarios();
    model.addAttribute("horarios", horarios);
    model.addAttribute("booking", booking);
    return "editBooking";
}

@PostMapping("/updateBooking")
public String updateBooking(@ModelAttribute("booking") Booking booking) {

    //Obtener usuario
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    AppUser appUser = userService.findByEmail(email);
    //Establecer usuario en la reserva
    booking.setAppUser(appUser);

    bookingService.updateBooking(booking);
    return "redirect:/historial";
}

private List<String> generateHorarios() {
        List<String> horarios = new ArrayList<>();
        LocalTime startTime = LocalTime.of(12, 30);
        LocalTime endTime = LocalTime.of(16, 30);

        while (!startTime.isAfter(endTime)) {
            horarios.add(startTime.toString());
            startTime = startTime.plusMinutes(30);
        }

        startTime = LocalTime.of(19, 30);
        endTime = LocalTime.of(23, 00);

        while (!startTime.isAfter(endTime)) {
            horarios.add(startTime.toString());
            startTime = startTime.plusMinutes(30);
        }

        return horarios;
    }

   
    
}
