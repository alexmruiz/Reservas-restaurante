package com.example.crud.controller.chart;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.crud.dto.booking.BestCustomer;
import com.example.crud.entity.user.AppUser;
import com.example.crud.service.booking.BookingService;
import com.example.crud.service.user.UserService;

@Controller
public class ChartController {

        @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @GetMapping("/estadisticas")
    public String showStatisticsForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // Use email as the username
        AppUser appUser = userService.findByEmail(email); // Find user by email
        model.addAttribute("appUser", appUser);
        return "chart"; // Vista del formulario
    }

    @GetMapping("/estadisticas/resultados")
    public String chart(
            @RequestParam("year") int year,
            @RequestParam("type") String type,
            Model model) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName(); // Use email as the username
                AppUser appUser = userService.findByEmail(email); // Find user by email
                model.addAttribute("appUser", appUser);

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
public String getTop5Customers(
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        Model model) {

    List<BestCustomer> top5Customers = bookingService.findTop5Customers(startDate, endDate);

    Map<String, Long> bestCustomerData = top5Customers.stream()
            .collect(Collectors.toMap(BestCustomer::getName, BestCustomer::getTotalBookings));

    model.addAttribute("data", bestCustomerData);
    model.addAttribute("chartTitle", "Top 5 Clientes del " + startDate + " al " + endDate);

    // Simulate the chartData for the reservations chart as well
    Map<String, Long> reservationsData = new HashMap<>();
    // Add your logic to populate the reservationsData map
    // Example: reservationsData.put("Customer1", 10);

    model.addAttribute("chartData", reservationsData);

    return "chart";
}


    

    
    /*************CLIENTES*********************** */

    @GetMapping("/clientes")
    public String showClientString(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            Model model) {
        
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String email = authentication.getName(); // Use email as the username
                AppUser appUser = userService.findByEmail(email); // Find user by email
                model.addAttribute("appUser", appUser);
        
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
