package com.example.crud.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.crud.entity.AppUser;
import com.example.crud.entity.BestCustomer;
import com.example.crud.service.BookingService;
import com.example.crud.service.UserService;

@Controller
public class ChartController {

        @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @GetMapping("/estadisticas")
    public String showStatisticsForm() {
        return "chart"; // Vista del formulario
    }

    @GetMapping("/estadisticas/resultados")
    public String chart(
            @RequestParam("year") int year,
            @RequestParam("type") String type,
            Model model) {
    
        Map<String, Long> data;
        String chartTitle;
        switch (type) {
            case "week":
                data = bookingService.getBookingsByWeek(year);
                chartTitle = "Reservas por Semana";
                break;
            case "month":
                data = bookingService.getBookingsByMonth(year);
                chartTitle = "Reservas por Mes";
                break;
            case "day":
                data = bookingService.getBookingsByDay(year);
                chartTitle = "Reservas por Día";
                break;
            default:
                throw new IllegalArgumentException("Tipo de estadística no soportado.");
        }
    
        model.addAttribute("chartData", data);
        model.addAttribute("chartTitle", chartTitle);
        model.addAttribute("year", year);
        model.addAttribute("type", type);
    
        // Intenta recuperar datos de mejor cliente del modelo, si existen
        if (!model.containsAttribute("bestCustomerData")) {
            model.addAttribute("bestCustomerData", new HashMap<>());
            model.addAttribute("bestCustomerTitle", "No hay datos del mejor cliente");
        }
    
        return "chart";
    }
    
    @GetMapping("/mejor_cliente")
    public String getBestCustomer(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
    
        BestCustomer bestCustomer = bookingService.findBestCustomer(startDate, endDate);
    
        Map<String, Long> bestCustomerData = new HashMap<>();
        bestCustomerData.put(bestCustomer.getName(), bestCustomer.getTotalBookings());
    
        model.addAttribute("bestCustomerData", bestCustomerData);
        model.addAttribute("bestCustomerTitle", "Mejor Cliente del " + startDate + " al " + endDate);
    
        // Intenta recuperar datos del gráfico de reservas del modelo, si existen
        if (!model.containsAttribute("chartData")) {
            model.addAttribute("chartData", new HashMap<>());
            model.addAttribute("chartTitle", "No hay datos de reservas");
        }
    
        return "chart";
    }
    
    /*************CLIENTES*********************** */

    @GetMapping("/clientes")
    public String showClientString(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AppUser> clientPage;

        if (search.isEmpty()) {
            clientPage = userService.findByRole("ROLE_CLIENT", pageable);
        } else {
            clientPage = userService.searchByName("ROLE_CLIENT", search, pageable);
        }

        model.addAttribute("clientPage", clientPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", clientPage.getTotalPages());
        model.addAttribute("search", search);
        return "client";
    }
    
}
