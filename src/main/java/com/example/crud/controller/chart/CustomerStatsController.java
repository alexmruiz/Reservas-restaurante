package com.example.crud.controller.chart;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.crud.service.chart.BestCustomerService;

@Controller
public class CustomerStatsController {

    @Autowired
    private BestCustomerService bestCustomerService;

    @GetMapping("/mejor_cliente")
    public String showTop5Customers(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {

        Map<String, Long> bestCustomerData = bestCustomerService.getTop5Customers(startDate, endDate);

        model.addAttribute("data", bestCustomerData);
        model.addAttribute("chartTitle", "Top 5 Clientes del " + startDate + " al " + endDate);

        return "chart";
    }
}
