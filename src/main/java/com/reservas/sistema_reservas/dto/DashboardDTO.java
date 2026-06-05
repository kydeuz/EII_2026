package com.reservas.sistema_reservas.dto;

public class DashboardDTO {

    private Long totalUsuarios;
    private Long totalLaboratorios;

    public DashboardDTO(Long totalUsuarios, Long totalLaboratorios) {
        this.totalUsuarios = totalUsuarios;
        this.totalLaboratorios = totalLaboratorios;
    }

    public Long getTotalUsuarios() {
        return totalUsuarios;
    }

    public Long getTotalLaboratorios() {
        return totalLaboratorios;
    }
}