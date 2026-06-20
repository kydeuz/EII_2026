package com.reservas.sistema_reservas.controller;


import com.reservas.sistema_reservas.entity.Laboratorio;
import com.reservas.sistema_reservas.repository.LaboratorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/laboratorios")
public class LaboratorioController {

    @Autowired
    private LaboratorioRepository laboratorioRepository;

    @PostMapping
    public ResponseEntity<Laboratorio> crear(@RequestBody Laboratorio laboratorio) {
        return ResponseEntity.ok(laboratorioRepository.save(laboratorio));
    }

    @GetMapping
    public ResponseEntity<List<Laboratorio>> listar() {
        return ResponseEntity.ok(laboratorioRepository.findAll());
    }
}
