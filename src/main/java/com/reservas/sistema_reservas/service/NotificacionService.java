package com.reservas.sistema_reservas.service;

import com.reservas.sistema_reservas.entity.Reserva;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    // Registra alertas instantáneas en los logs del servidor web
    public void enviarAlertaReserva(Reserva reserva, String accion) {
        String mensaje = String.format(
            "NOTIFICACIÓN SISTEMA SYSTEM-LOG -> Acción: %s | Reserva ID: %d | Usuario: %s | Laboratorio: %s | Estado Actual: %s",
            accion, 
            reserva.getId(), 
            reserva.getUsuario().getNombre(), 
            reserva.getLaboratorio().getNombre(),
            reserva.getEstado().name()
        );
        System.out.println(mensaje);
    }
}