package com.example.crud.service.chart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.crud.entity.user.AppUser;
import com.example.crud.service.user.UserService;

@Service
public class ClientService {

    @Autowired
    private UserService userService;

    public Page<AppUser> getClients(String search, Pageable pageable) {
        if (search == null || search.isEmpty()) {
            return userService.findByRole("ROLE_CLIENT", pageable);
        }
        return userService.searchByName("ROLE_CLIENT", search, pageable);
    }
}
