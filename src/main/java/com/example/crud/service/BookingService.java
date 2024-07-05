package com.example.crud.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.crud.entity.AppUser;
import com.example.crud.entity.Booking;
import com.example.crud.repository.BookingRepository;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserService userService;

    public List<Booking> listAllBookings(){
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id){
        return bookingRepository.findById(id);
    }

    public Booking saveBooking(Booking booking){
        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id){
        bookingRepository.deleteById(id);
    }

    public List<Booking> findBookingsForUser()
    {
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // Email del usuario autenticado

        // Buscar al usuario por su email
        AppUser appUser = userService.findByEmail(email);

        // Buscar reservas por usuario
        return bookingRepository.findByAppUser(appUser);
    }

}
