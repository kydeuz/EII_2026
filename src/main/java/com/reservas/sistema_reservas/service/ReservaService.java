package com.reservas.sistema_reservas.service;


import com.reservas.sistema_reservas.entity.*;
import com.reservas.sistema_reservas.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private LaboratorioRepository laboratorioRepository;
    
    // CREAR RESERVA — con validación anti-cruce
    public Reserva crearReserva(Long usuarioId, Long laboratorioId,
                                LocalDate fecha, LocalTime inicio, LocalTime fin) {

        // Validar que hora inicio sea antes que hora fin
        if (!inicio.isBefore(fin)) {
            throw new RuntimeException("La hora de inicio debe ser anterior a la hora de fin");
        }

        // Validar que no sea fecha pasada
        if (fecha.isBefore(LocalDate.now())) {
            throw new RuntimeException("No se puede reservar en una fecha pasada");
        }

        // Verificar cruce de horarios
        long cruces = reservaRepository.contarCruces(laboratorioId, fecha, inicio, fin);
        if (cruces > 0) {
            throw new RuntimeException("El laboratorio ya tiene una reserva en ese horario");
        }

        // Obtener entidades
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Laboratorio laboratorio = laboratorioRepository.findById(laboratorioId)
            .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));

        // Crear y guardar reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setLaboratorio(laboratorio);
        reserva.setFecha(fecha);
        reserva.setHoraInicio(inicio);
        reserva.setHoraFin(fin);
        reserva.setEstado(EstadoReserva.CONFIRMADA);

        return reservaRepository.save(reserva);
    }

    // CAMBIAR ESTADO — máquina de estados controlada
    public Reserva cambiarEstado(Long reservaId, EstadoReserva nuevoEstado) {
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        EstadoReserva actual = reserva.getEstado();

        // Validar transiciones legales
        boolean transicionValida = switch (actual) {
            case PENDIENTE -> nuevoEstado == EstadoReserva.CONFIRMADA
                           || nuevoEstado == EstadoReserva.RECHAZADA;
            case CONFIRMADA -> nuevoEstado == EstadoReserva.CANCELADA
                            || nuevoEstado == EstadoReserva.COMPLETADA;
            default -> false; // CANCELADA, COMPLETADA, RECHAZADA son estados finales
        };

        if (!transicionValida) {
            throw new RuntimeException(
                "Transición no permitida: " + actual + " → " + nuevoEstado
            );
        }

        reserva.setEstado(nuevoEstado);
        return reservaRepository.save(reserva);
    }

    // VERIFICAR DISPONIBILIDAD
    public List<Reserva> verificarDisponibilidad(Long laboratorioId,
                                                  LocalDate fecha,
                                                  LocalTime inicio,
                                                  LocalTime fin) {
        return reservaRepository.findCruces(laboratorioId, fecha, inicio, fin);
    }

    // CANCELAR RESERVA
    public Reserva cancelarReserva(Long reservaId) {
        return cambiarEstado(reservaId, EstadoReserva.CANCELADA);
    }
}