package com.example.crud.controller;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConexionController {

    private final JdbcTemplate jdbcTemplate;

    
    public ConexionController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/pruebaconexion")
    public String pruebaConexionBaseDatos() {
        try {
            jdbcTemplate.execute("SELECT 1");
            return "La conexión fue exitosa";
        } catch (Exception e) {
            return "Error en la conexión: " + e.getMessage();
        }
    }
}
