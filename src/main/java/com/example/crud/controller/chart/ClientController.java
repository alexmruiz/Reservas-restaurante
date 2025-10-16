package com.example.crud.controller.chart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.crud.entity.user.AppUser;
import com.example.crud.service.chart.ClientService;
import com.example.crud.service.user.UserService;

@Controller
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserService userService;

    @GetMapping("/clientes")
    public String showClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        AppUser appUser = userService.findByEmail(email);

        Pageable pageable = PageRequest.of(page, size);
        Page<AppUser> clientPage = clientService.getClients(search, pageable);

        model.addAttribute("appUser", appUser);
        model.addAttribute("clientPage", clientPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", clientPage.getTotalPages());
        model.addAttribute("search", search);

        return "client";
    }
}
