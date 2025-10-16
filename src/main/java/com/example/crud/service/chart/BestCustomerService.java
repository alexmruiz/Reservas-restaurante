package com.example.crud.service.chart;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.crud.dto.booking.BestCustomer;
import com.example.crud.service.booking.BookingService;

@Service
public class BestCustomerService {

    @Autowired
    private BookingService bookingService;

    public Map<String, Long> getTop5Customers(LocalDate startDate, LocalDate endDate) {
        List<BestCustomer> top5Customers = bookingService.findTop5Customers(startDate, endDate);
        return top5Customers.stream()
                .collect(Collectors.toMap(BestCustomer::getName, BestCustomer::getTotalBookings));
    }
}
