package com.reservas.sistema_reservas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.reservas.sistema_reservas.dto.DashboardDTO;
import com.reservas.sistema_reservas.repository.LaboratorioRepository;
import com.reservas.sistema_reservas.repository.UsuarioRepository;

@Service
public class DashboardService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LaboratorioRepository laboratorioRepository;

    public DashboardDTO obtenerDashboard() {

        Long totalUsuarios = usuarioRepository.count();

        Long totalLaboratorios = laboratorioRepository.count();

        return new DashboardDTO(totalUsuarios, totalLaboratorios);
    }
}