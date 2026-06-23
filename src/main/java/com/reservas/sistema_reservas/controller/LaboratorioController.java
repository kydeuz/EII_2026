package com.reservas.sistema_reservas.controller;


import com.reservas.sistema_reservas.entity.Laboratorio;
import com.reservas.sistema_reservas.repository.LaboratorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

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
    
    @PutMapping("/{id}")
    public ResponseEntity<Laboratorio> actualizar(@PathVariable Long id, @RequestBody Laboratorio laboratorio) {
        return laboratorioRepository.findById(id)
            .map(l -> {
                l.setNombre(laboratorio.getNombre());
                l.setUbicacion(laboratorio.getUbicacion());
                l.setCapacidad(laboratorio.getCapacidad());
                return ResponseEntity.ok(laboratorioRepository.save(l));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (!laboratorioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            laboratorioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "No se puede eliminar el laboratorio porque tiene reservas asociadas. Primero cancele o elimine esas reservas."));
        }
    }
    
}
