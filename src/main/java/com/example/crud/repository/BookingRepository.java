package com.example.crud.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.crud.entity.AppUser;
import com.example.crud.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>{
    List<Booking>findByAppUser(AppUser appUser);

    //Metodo para buscar reservas por zona, fecha y rango de tiempo
    List<Booking> findByZoneAndDateAndTimeBetween(String zone, LocalDate date, LocalTime time, LocalTime endTime);
    

}
