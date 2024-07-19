package com.example.crud.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.crud.entity.AppUser;
import com.example.crud.entity.Booking;
import com.example.crud.repository.BookingRepository;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserService userService;

    private static final int MAX_PERSONS_SALON = 80; //40 Mesas*2 personas
    private static final int MAX_PERSONS_TERRAZA = 30; //15 Mesas*2 personas
    
    public boolean isAvailable(String zone, LocalDate date, LocalTime time, int persons) {
        LocalTime endTime = time.plusMinutes(60); // Se asume que cada reserva dura una hora

        List<Booking> bookings = bookingRepository.findByZoneAndDateAndTimeBetween(zone, date, time, endTime);

        int totalPersons = bookings.stream().mapToInt(Booking::getPersons).sum();

        if (zone.equals("Salon")) {
            return (totalPersons + persons) <= MAX_PERSONS_SALON;
        } else if (zone.equals("Terraza")) {
            return (totalPersons + persons) <= MAX_PERSONS_TERRAZA;
        }

        return false;
    }

    public List<Booking> listAllBookings(){
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id){
        return bookingRepository.findById(id);
    }

    public Booking findBookingById(Long id){
        return bookingRepository.findById(id).orElse(null);
    }

    public void updateBooking(Booking booking){
        Booking existingBooking = bookingRepository.findById(booking.getId())
        .orElseThrow(() -> new RuntimeException("Booking not found with id " + booking.getId()));

        //Actualizar los campos permitidos
        existingBooking.setPersons(booking.getPersons());
        existingBooking.setZone(booking.getZone());
        existingBooking.setDate(booking.getDate());
        existingBooking.setTime(booking.getTime());

        //Guardar la entidad actualizada
        bookingRepository.save(booking);
    }
    
    @Transactional
    public Booking saveBooking(Booking booking) {
        if (isAvailable(booking.getZone(), booking.getDate(), booking.getTime(), booking.getPersons())) {
            booking.setEndTime(booking.getTime().plusMinutes(60)); // Establecer la hora de finalización
            
            // Lógica para establecer isten basado en el campo text
            if (booking.getText() == null || booking.getText().isEmpty()) {
                booking.setIsten(false);
            } else {
                booking.setIsten(true);
            }
            
            return bookingRepository.save(booking);
        } else {
            throw new RuntimeException("No hay disponibilidad para la hora y zona seleccionadas.");
        }
    }

    public void deleteBooking(List<Long> ids){
        bookingRepository.deleteAllById(ids);
    }

    public List<Booking> findBookingsForUser()
    {
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // Email del usuario autenticado

        
        if (email == null || email.isEmpty()) {
            System.out.println("Nombre de usuario no disponible");
            // Puedes manejar este caso según sea necesario
        } else {
            System.out.println("Email del usuario autenticado: " + email);
            // Continuar con el procesamiento
        }

        // Buscar al usuario por su email
        AppUser appUser = userService.findByEmail(email);

        System.out.println("El usuario es : " + appUser);
        System.out.println("El usuario es : id=" + appUser.getId() + ", email=" + appUser.getEmail() + ", firstName=" + appUser.getName() + ", lastName=" + appUser.getLastname() + ", hora=" +appUser.getEmail()+appUser.getCreateAt());

        // Buscar reservas por usuario
        return bookingRepository.findByAppUser(appUser);
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    // Método para obtener todas las reservas junto con los datos del usuario
    public List<Booking> getAllBookingsWithUserData() {
        List<Booking> bookings = bookingRepository.findAll();
        for (Booking booking : bookings) {
            // Cargar el usuario asociado a la reserva
            AppUser appUser = booking.getAppUser(); // Asegúrate de que la relación esté cargada correctamente

            System.out.println("Usuarios: "+ appUser);
            // Aquí puedes hacer operaciones con appUser si es necesario
        }
        return bookings;
    }

}
