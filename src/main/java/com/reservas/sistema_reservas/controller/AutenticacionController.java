package com.reservas.sistema_reservas.controller;

import com.reservas.sistema_reservas.config.JwtUtil;
import com.reservas.sistema_reservas.entity.Usuario;
import com.reservas.sistema_reservas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ENDPOINT EXTRA: Para registrar usuarios con contraseña encriptada
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(guardado);
    }

    // LOGIN REST
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        return usuarioRepository.findAll().stream()
            .filter(u -> u.getEmail().equals(body.get("email")))
            .findFirst()
            .map(u -> {
            	if (passwordEncoder.matches(body.get("password"), u.getPassword())) {
            	    return ResponseEntity.ok(Map.of(
            	        "mensaje", "Autenticación exitosa",
            	        "token", jwtUtil.generarToken(u.getEmail(), u.getRol()),
            	        "usuario", u.getNombre(),
            	        "rol", u.getRol()
            	    ));
            	}
                return ResponseEntity.status(401).body(Map.of("error", "Contraseña incorrecta"));
            })
            .orElse(ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado")));
    }
}