package com.reservas.sistema_reservas.repository;


import com.reservas.sistema_reservas.entity.EstadoReserva;
import com.reservas.sistema_reservas.entity.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Query anti-cruce — el núcleo del sistema
    @Query("""
        SELECT COUNT(r) FROM Reserva r
        WHERE r.laboratorio.id = :labId
        AND r.fecha = :fecha
        AND r.estado = 'CONFIRMADA'
        AND NOT (r.horaFin <= :inicio OR r.horaInicio >= :fin)
        """)
    long contarCruces(@Param("labId") Long labId,
                      @Param("fecha") LocalDate fecha,
                      @Param("inicio") LocalTime inicio,
                      @Param("fin") LocalTime fin);

    // Para el endpoint de disponibilidad (retorna las reservas que cruzan)
    @Query("""
        SELECT r FROM Reserva r
        WHERE r.laboratorio.id = :labId
        AND r.fecha = :fecha
        AND r.estado = 'CONFIRMADA'
        AND NOT (r.horaFin <= :inicio OR r.horaInicio >= :fin)
        """)
    List<Reserva> findCruces(@Param("labId") Long labId,
                             @Param("fecha") LocalDate fecha,
                             @Param("inicio") LocalTime inicio,
                             @Param("fin") LocalTime fin);

    // Historial con filtros opcionales + paginación
    @Query("""
        SELECT r FROM Reserva r
        WHERE (:usuarioId IS NULL OR r.usuario.id = :usuarioId)
        AND (:laboratorioId IS NULL OR r.laboratorio.id = :laboratorioId)
        AND (:fecha IS NULL OR r.fecha = :fecha)
        ORDER BY r.fecha DESC, r.horaInicio DESC
        """)
    Page<Reserva> findByFiltros(@Param("usuarioId") Long usuarioId,
                                @Param("laboratorioId") Long laboratorioId,
                                @Param("fecha") LocalDate fecha,
                                Pageable pageable);
}