package com.example.crud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
    // Obtener usuario autenticado
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName(); // Email del usuario autenticado

    // Buscar al usuario por su email
    AppUser appUser = userService.findByEmail(email);

    // Asignar el usuario a la reserva
    booking.setAppUser(appUser);

    // Guardar la reserva
    bookingService.saveBooking(booking);

    // Agregar mensaje de éxito para mostrar en la próxima vista
    redirectAttributes.addFlashAttribute("successMessage", "Reserva realizada con éxito");

    // Redirigir a una nueva URL utilizando PRG (Post-Redirect-Get)
    return "redirect:/reservas";
}

@GetMapping("/historial")
public String showBookingHistory(Model model) {
    // Obtener las reservas del usuario autenticado
    List<Booking> bookings = bookingService.findBookingsForUser();

    // Pasar las reservas al modelo
    model.addAttribute("bookings", bookings);

    return "historial"; // Nombre de la vista historial.html
}

}
