package com.example.crud.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.crud.entity.Reserva;
import com.example.crud.repository.ReservaRepository;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> obtenerReservaPorId(int id) {
        return reservaRepository.findById(id);
    }

    public List<Reserva> obtenerReservasPorUsuario(long idUsuario) {
        return reservaRepository.findByAppUserId(idUsuario);
    }

    public Reserva guardarReserva(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public void eliminarReserva(int id) {
        reservaRepository.deleteById(id);
    }
}



