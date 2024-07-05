package com.example.crud.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;

import com.example.crud.entity.Reserva;
import com.example.crud.service.ReservaService;




@Controller
public class ReservaController {

    @Autowired
    private ReservaService reservaService;


  

    @GetMapping("/listar")
    public List<Reserva> listarReservas() {
        return reservaService.listarReservas();
    }

    @GetMapping("/{id}")
    public Reserva obtenerReservaPorId(@PathVariable int id) {
        return reservaService.obtenerReservaPorId(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Reserva> obtenerReservasPorUsuario(@PathVariable long idUsuario) {
        return reservaService.obtenerReservasPorUsuario(idUsuario);
    }



    @DeleteMapping("/{id}")
    public void eliminarReserva(@PathVariable int id) {
        reservaService.eliminarReserva(id);
    }

}
