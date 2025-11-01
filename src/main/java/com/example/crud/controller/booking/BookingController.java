package com.example.crud.controller.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.crud.entity.booking.Booking;
import com.example.crud.entity.user.AppUser;
import com.example.crud.service.booking.BookingService;
import com.example.crud.service.user.UserService;

import java.util.List;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;

    /** Crear reserva */
    @PostMapping("/create")
    public String createBooking(@ModelAttribute("booking") Booking booking, 
                                Model model, RedirectAttributes redirectAttributes) {
        AppUser user = getAuthenticatedUser();
        booking.setAppUser(user);

        try {
            bookingService.saveBooking(booking);
            return "redirect:/historial";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("appUser", user);
            model.addAttribute("horarios", bookingService.generateHorarios());
            return "reservas";
        }
    }

    /** Página de reservas */
    @GetMapping("/reservas")
    public String index(Model model) {
        AppUser user = getAuthenticatedUser();
        model.addAttribute("appUser", user);
        model.addAttribute("horarios", bookingService.generateHorarios());
        return "reservas";
    }

    /**
     * Historial de reservas
     * @param model
     * @return
     */
    @GetMapping("/historial")
    public String showBookingHistory(Model model) {
        AppUser user = getAuthenticatedUser();
        List<Booking> bookings = bookingService.findBookingsForUser(user);
        model.addAttribute("appUser", user);
        model.addAttribute("bookings", bookings);
        return "historial";
    }

    /** Eliminar reservas */
    @GetMapping("/deleteBookings")
    public String deleteBookings(@RequestParam("ids") List<Long> ids) {
        bookingService.deleteBooking(ids);
        return "redirect:/historial";
    }

    /** Editar reserva */
    @GetMapping("/editBooking")
    public String editBooking(@RequestParam("id") Long id, Model model) {
        Booking booking = bookingService.findBookingById(id);
        model.addAttribute("booking", booking);
        model.addAttribute("horarios", bookingService.generateHorarios());
        return "editBooking";
    }

    /** Actualizar reserva */
    @PostMapping("/updateBooking")
    public String updateBooking(@ModelAttribute("booking") Booking booking) {
        AppUser user = getAuthenticatedUser();
        booking.setAppUser(user);
        bookingService.updateBooking(booking);
        return "redirect:/historial";
    }

    /** Obtiene el usuario autenticado actual */
    private AppUser getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByEmail(auth.getName());
    }
}
