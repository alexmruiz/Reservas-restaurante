package com.example.crud.service.booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.crud.dto.booking.BestCustomer;
import com.example.crud.entity.booking.Booking;
import com.example.crud.entity.user.AppUser;
import com.example.crud.repository.booking.BookingRepository;
@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    private static final int MAX_PERSONS_SALON = 80;
    private static final int MAX_PERSONS_TERRAZA = 30;

    /* ======= CRUD ======= */

    /**
     * Crea una nueva reserva después de validar la disponibilidad.
     * @param booking
     * @return
     */
    @Transactional
    public Booking saveBooking(Booking booking) {
        validateAvailability(booking);
        setBookingEndTime(booking);
        setIstenFlag(booking);
        return bookingRepository.save(booking);
    }

    /**
     * Actualiza una reserva existente. Solo se pueden modificar ciertos campos.
     * @param booking
     */
    public void updateBooking(Booking booking) {
        Booking existing = bookingRepository.findById(booking.getId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        updateEditableFields(existing, booking);
        bookingRepository.save(existing);
    }

    /**
     * Elimina reservas por una lista de IDs.
     * @param ids
     */
    public void deleteBooking(List<Long> ids) {
        bookingRepository.deleteAllById(ids);
    }

    /**
     * Encuentra una reserva por su ID.
     * @param id
     * @return
     */
    public Booking findBookingById(Long id) {
        return bookingRepository.findById(id).orElse(null);
    }

    /**
     * Encuentra todas las reservas asociadas a un usuario específico.
     * @param user
     * @return
     */
    public List<Booking> findBookingsForUser(AppUser user) {
        return bookingRepository.findByAppUser(user);
    }

    /* ======= LÓGICA DE NEGOCIO ======= */

    /**
     * Valida si hay disponibilidad para la reserva.
     * @param booking
     */
    private void validateAvailability(Booking booking) {
        if (!isAvailable(booking.getZone(), booking.getDate(), booking.getTime(), booking.getPersons())) {
            throw new RuntimeException("No hay disponibilidad para la hora y zona seleccionadas.");
        }
    }

    /**
     * Establece la hora de finalización de la reserva sumando 60 minutos a la hora de inicio.
     * @param booking
     */
    private void setBookingEndTime(Booking booking) {
        booking.setEndTime(booking.getTime().plusMinutes(60));
    }

    /**
     * Establece el flag 'isten' basado en si el campo 'text' está vacío o no.
     * @param booking
     */
    private void setIstenFlag(Booking booking) {
        booking.setIsten(booking.getText() != null && !booking.getText().isEmpty());
    }

    /**
     * Actualiza solo los campos editables de una reserva.
     * @param existing
     * @param updated
     */
    private void updateEditableFields(Booking existing, Booking updated) {
        existing.setPersons(updated.getPersons());
        existing.setZone(updated.getZone());
        existing.setDate(updated.getDate());
        existing.setTime(updated.getTime());
    }

    /* ======= DISPONIBILIDAD ======= */

    /**
     * Verifica si hay disponibilidad para una reserva en una zona, fecha y hora específicas.
     * @param zone
     * @param date
     * @param time
     * @param persons
     * @return
     */
    public boolean isAvailable(String zone, LocalDate date, LocalTime time, int persons) {
        LocalTime endTime = time.plusMinutes(60);
        List<Booking> bookings = bookingRepository.findByZoneAndDateAndTimeBetween(zone, date, time, endTime);
        int totalPersons = bookings.stream().mapToInt(Booking::getPersons).sum();
        return switch (zone) {
            case "Salon" -> totalPersons + persons <= MAX_PERSONS_SALON;
            case "Terraza" -> totalPersons + persons <= MAX_PERSONS_TERRAZA;
            default -> false;
        };
    }

    /* ======= UTILIDADES ======= */

    /**
     * Genera una lista de horarios disponibles en intervalos de 30 minutos.
     * @return
     */
    public List<String> generateHorarios() {
        List<String> horarios = new ArrayList<>();
        addHorarios(horarios, LocalTime.of(12, 30), LocalTime.of(16, 30));
        addHorarios(horarios, LocalTime.of(19, 30), LocalTime.of(23, 00));
        return horarios;
    }

    /**
     * Añade horarios en intervalos de 30 minutos a la lista dada.
     * @param horarios
     * @param start
     * @param end
     */
    private void addHorarios(List<String> horarios, LocalTime start, LocalTime end) {
        while (!start.isAfter(end)) {
            horarios.add(start.toString());
            start = start.plusMinutes(30);
        }
    }

    /* ======= REPORTES ======= */

    /**
     * Agrupa las reservas por mes en un año específico.
     * @param year
     * @return
     */
    public Map<String, Long> getBookingsByMonth(int year) {
        return groupBookingsBy(year, b -> b.getDate().getMonth().toString());
    }

    /**
     * Agrupa las reservas por semana en un año específico.
     * @param year
     * @return
     */
    public Map<String, Long> getBookingsByWeek(int year) {
        WeekFields wf = WeekFields.of(Locale.getDefault());
        return groupBookingsBy(year, b -> "Semana " + b.getDate().get(wf.weekOfWeekBasedYear()));
    }

    /**
     * Agrupa las reservas por día en un año específico.
     * @param year
     * @return
     */
    public Map<String, Long> getBookingsByDay(int year) {
        return groupBookingsBy(year, b -> b.getDate().toString());
    }

    /**
     * Agrupa las reservas por una clave dada (mes, semana, día).
     * @param year
     * @param keyMapper
     * @return
     */
    private Map<String, Long> groupBookingsBy(int year, java.util.function.Function<Booking, String> keyMapper) {
        List<Booking> bookings = bookingRepository.findByYear(year);
        return bookings.stream()
                .collect(Collectors.groupingBy(keyMapper, Collectors.counting()));
    }

    /**
     * Encuentra los 5 mejores clientes en un rango de fechas.
     * @param startDate
     * @param endDate
     * @return
     */
    public List<BestCustomer> findTop5Customers(LocalDate startDate, LocalDate endDate) {
        Map<AppUser, Long> count = bookingRepository.findByDateBetween(startDate, endDate).stream()
                .collect(Collectors.groupingBy(Booking::getAppUser, Collectors.counting()));
        return count.entrySet().stream()
                .sorted(Map.Entry.<AppUser, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new BestCustomer(e.getKey().getName(), e.getValue()))
                .collect(Collectors.toList());
    }

    public Object getAllZones() {
        return Arrays.asList("Salon", "Terraza");
    }

    public Object getFilteredBookings(String filterDate, String filterZone) {
        return bookingRepository.findAll();
    }

    public Object resolveDate(String filterDate) {
        return filterDate;
    }
}
