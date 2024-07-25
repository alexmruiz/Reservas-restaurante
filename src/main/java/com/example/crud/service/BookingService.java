    package com.example.crud.service;

    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.time.Month;
    import java.time.temporal.WeekFields;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Locale;
    import java.util.Map;
    import java.util.Optional;
    import java.util.stream.Collectors;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import com.example.crud.entity.AppUser;
import com.example.crud.entity.BestCustomer;
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

        public List<Booking> findBookingsByDate(LocalDate date) {
            return bookingRepository.findByDate(date);
        }

        public List<Booking> getAllBookings() {
            return bookingRepository.findAll();
        }

        public List<Booking> getBookingsByDateAndZone(String date, String zone) {
            LocalDate localDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : null;
            if (localDate != null && zone != null && !zone.isEmpty()) {
                return bookingRepository.findByDateAndZone(localDate, zone);
            } else if (localDate != null) {
                return bookingRepository.findByDate(localDate);
            } else if (zone != null && !zone.isEmpty()) {
                return bookingRepository.findByZone(zone);
            } else {
                return bookingRepository.findAll();
            }
        }

        public List<String> getAllZones() {
            return bookingRepository.findDistinctZones();
        }

        public List<Booking> getBookingsWithTextNonNull() {
        // Obtener todas las reservas
        List<Booking> allBookings = bookingRepository.findAll();
        
        // Filtrar reservas donde el campo text no es null
        return allBookings.stream()
            .filter(booking -> booking.getText() != null)
            .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        return bookingRepository.findByDate(localDate).stream()
            
            .collect(Collectors.toList());
    }


    public List<Booking> getBookingsByDate(LocalDate localDate) {
            // Lógica para obtener reservas por fecha
            return bookingRepository.findByDate(localDate);
        }



        /********************CHARTS ********************/

        public List<Booking> findByYear(int year) {
            return bookingRepository.findByYear(year);
        }
    
        public Map<String, Long> getBookingsByWeek(int year) {
            List<Booking> bookings = findByYear(year);
            Map<String, Long> bookingsByWeek = new HashMap<>();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
    
            for (Booking booking : bookings) {
                int weekNumber = booking.getDate().get(weekFields.weekOfWeekBasedYear());
                String weekKey = "Semana " + weekNumber;
                bookingsByWeek.put(weekKey, bookingsByWeek.getOrDefault(weekKey, 0L) + 1);
            }
            return bookingsByWeek;
        }
    
        public Map<String, Long> getBookingsByMonth(int year) {
            List<Booking> bookings = findByYear(year);
            Map<String, Long> bookingsByMonth = new HashMap<>();
    
            for (Booking booking : bookings) {
                Month month = booking.getDate().getMonth();
                String monthKey = month.toString();
                bookingsByMonth.put(monthKey, bookingsByMonth.getOrDefault(monthKey, 0L) + 1);
            }
            return bookingsByMonth;
        }
    
        public Map<String, Long> getBookingsByDay(int year) {
            List<Booking> bookings = findByYear(year);
            Map<String, Long> bookingsByDay = new HashMap<>();
    
            for (Booking booking : bookings) {
                String dayKey = booking.getDate().toString();
                bookingsByDay.put(dayKey, bookingsByDay.getOrDefault(dayKey, 0L) + 1);
            }
            return bookingsByDay;
        }


        public List<BestCustomer> findTop5Customers(LocalDate startDate, LocalDate endDate) {
            List<Booking> bookings = bookingRepository.findByDateBetween(startDate, endDate);
        
            Map<AppUser, Long> customerBookingCount = bookings.stream()
                    .collect(Collectors.groupingBy(Booking::getAppUser, Collectors.counting()));
        
            return customerBookingCount.entrySet().stream()
                    .sorted(Map.Entry.<AppUser, Long>comparingByValue().reversed())
                    .limit(5)
                    .map(entry -> new BestCustomer(entry.getKey().getName(), entry.getValue()))
                    .collect(Collectors.toList());
        }
        
    }

    
