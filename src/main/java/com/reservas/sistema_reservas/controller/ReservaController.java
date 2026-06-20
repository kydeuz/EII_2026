package com.reservas.sistema_reservas.controller;


import com.reservas.sistema_reservas.entity.EstadoReserva;
import com.reservas.sistema_reservas.entity.Reserva;
import com.reservas.sistema_reservas.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    // CREAR RESERVA
    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody Map<String, String> body) {
        try {
            Reserva reserva = reservaService.crearReserva(
                Long.parseLong(body.get("usuarioId")),
                Long.parseLong(body.get("laboratorioId")),
                LocalDate.parse(body.get("fecha")),
                LocalTime.parse(body.get("horaInicio")),
                LocalTime.parse(body.get("horaFin"))
            );
            return ResponseEntity.ok(Map.of(
                "mensaje", "Reserva creada exitosamente",
                "reservaId", reserva.getId(),
                "estado", reserva.getEstado()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // VERIFICAR DISPONIBILIDAD
    @GetMapping("/disponibilidad")
    public ResponseEntity<?> verificarDisponibilidad(
            @RequestParam Long laboratorioId,
            @RequestParam String fecha,
            @RequestParam String inicio,
            @RequestParam String fin) {

        List<Reserva> cruces = reservaService.verificarDisponibilidad(
            laboratorioId,
            LocalDate.parse(fecha),
            LocalTime.parse(inicio),
            LocalTime.parse(fin)
        );

        return ResponseEntity.ok(Map.of(
            "disponible", cruces.isEmpty(),
            "cruces", cruces.size()
        ));
    }

    // CANCELAR RESERVA
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) {
        try {
            Reserva reserva = reservaService.cancelarReserva(id);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Reserva cancelada",
                "estado", reserva.getEstado()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // CAMBIAR ESTADO
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id,
                                           @RequestParam String estado) {
        try {
            Reserva reserva = reservaService.cambiarEstado(id, EstadoReserva.valueOf(estado));
            return ResponseEntity.ok(Map.of(
                "mensaje", "Estado actualizado",
                "estado", reserva.getEstado()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
