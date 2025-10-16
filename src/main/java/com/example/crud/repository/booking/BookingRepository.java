package com.example.crud.repository.booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.crud.entity.booking.Booking;
import com.example.crud.entity.user.AppUser;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    @EntityGraph(attributePaths = {"appUser"})
    List<Booking> findAll();

    List<Booking> findByAppUser(AppUser appUser);

    List<Booking> findByZoneAndDateAndTimeBetween(String zone, LocalDate date, LocalTime time, LocalTime endTime);

    List<Booking> findByDate(LocalDate date);

    List<Booking> findByZone(String zone);

    List<Booking> findByDateAndZone(LocalDate date, String zone);

    @Query("SELECT DISTINCT b.zone FROM Booking b")
    List<String> findDistinctZones();

    List<Booking> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT b FROM Booking b WHERE YEAR(b.date) = ?1")
    List<Booking> findByYear(int year);

}
