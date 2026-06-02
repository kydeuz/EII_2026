package com.reservas.sistema_reservas.controller;

import com.reservas.sistema_reservas.entity.Reserva;
import com.reservas.sistema_reservas.service.HistorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/historial")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    @GetMapping
    public ResponseEntity<Page<Reserva>> obtenerHistorial(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long laboratorioId,
            @RequestParam(required = false) String fecha,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        LocalDate fechaParsed = fecha != null ? LocalDate.parse(fecha) : null;
        Pageable pageable = PageRequest.of(page, size);

        Page<Reserva> resultado = historialService.filtrar(
            usuarioId, laboratorioId, fechaParsed, pageable
        );

        return ResponseEntity.ok(resultado);
    }
}