package com.example.crud.controller.chart;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.crud.entity.user.AppUser;
import com.example.crud.service.chart.ChartService;
import com.example.crud.service.user.UserService;

@Controller
public class ChartController {

    @Autowired
    private ChartService chartService;

    @Autowired
    private UserService userService;

    @GetMapping("/estadisticas")
    public String showStatisticsForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        AppUser appUser = userService.findByEmail(email);
        model.addAttribute("appUser", appUser);
        return "chart";
    }

    @GetMapping("/estadisticas/resultados")
    public String showChart(
            @RequestParam("year") int year,
            @RequestParam("type") String type,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        AppUser appUser = userService.findByEmail(email);

        Map<String, Long> data = chartService.getChartDataByType(type, year);
        String title = chartService.getChartTitle(type);

        model.addAttribute("appUser", appUser);
        model.addAttribute("chartData", data);
        model.addAttribute("chartTitle", title);
        model.addAttribute("year", year);
        model.addAttribute("type", type);

        return "chart";
    }
}
