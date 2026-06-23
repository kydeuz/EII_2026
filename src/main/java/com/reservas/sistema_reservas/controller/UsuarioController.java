package com.reservas.sistema_reservas.controller;

import com.reservas.sistema_reservas.entity.Usuario;
import com.reservas.sistema_reservas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
 
    // CREAR USUARIO
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    // LISTAR TODOS
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscar(@PathVariable Long id) {
        return usuarioRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return usuarioRepository.findById(id)
            .map(usuario -> {
                // Actualizar campos básicos
                if (body.containsKey("nombre")) {
                    usuario.setNombre((String) body.get("nombre"));
                }
                if (body.containsKey("email")) {
                    usuario.setEmail((String) body.get("email"));
                }
                if (body.containsKey("rol")) {
                    usuario.setRol((String) body.get("rol"));
                }
                // Actualizar contraseña solo si se envía un valor no vacío
                if (body.containsKey("password") && body.get("password") != null 
                    && !body.get("password").toString().isBlank()) {
                    usuario.setPassword(passwordEncoder.encode(body.get("password").toString()));
                }
                usuarioRepository.save(usuario);
                return ResponseEntity.ok(usuario);
            })
            .orElse(ResponseEntity.notFound().build());
    }
}