package com.example.crud.service.chart;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.crud.service.booking.BookingService;

@Service
public class ChartService {

    @Autowired
    private BookingService bookingService;

    public Map<String, Long> getChartDataByType(String type, int year) {
        switch (type) {
            case "week":
                return bookingService.getBookingsByWeek(year);
            case "month":
                return bookingService.getBookingsByMonth(year);
            case "day":
                return bookingService.getBookingsByDay(year);
            default:
                throw new IllegalArgumentException("Tipo de estadística no soportado: " + type);
        }
    }

    public String getChartTitle(String type) {
        return switch (type) {
            case "week" -> "Reservas por Semana";
            case "month" -> "Reservas por Mes";
            case "day" -> "Reservas por Día";
            default -> "Estadísticas de Reservas";
        };
    }
}
