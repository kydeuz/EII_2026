package com.reservas.sistema_reservas.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "laboratorios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Laboratorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String ubicacion;
    private Integer capacidad;
    
    public String getNombre() { return nombre; }
    
}