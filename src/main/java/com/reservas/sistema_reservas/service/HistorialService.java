package com.reservas.sistema_reservas.service;

import com.reservas.sistema_reservas.entity.Reserva;
import com.reservas.sistema_reservas.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class HistorialService {

    @Autowired
    private ReservaRepository reservaRepository;

    public Page<Reserva> filtrar(Long usuarioId,
                                  Long laboratorioId,
                                  LocalDate fecha,
                                  Pageable pageable) {
        return reservaRepository.findByFiltros(usuarioId, laboratorioId, fecha, pageable);
    }
}