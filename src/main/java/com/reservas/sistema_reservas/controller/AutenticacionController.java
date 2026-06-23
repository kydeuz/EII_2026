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
                    // Construir un Map<String, Object> explícitamente
                    Map<String, Object> respuesta = new java.util.LinkedHashMap<>();
                    respuesta.put("mensaje", "Autenticación exitosa");
                    respuesta.put("token", jwtUtil.generarToken(u.getEmail(), u.getRol()));
                    respuesta.put("usuario", u.getNombre());
                    respuesta.put("usuarioId", u.getId());
                    respuesta.put("rol", u.getRol());
                    return ResponseEntity.ok(respuesta);
                } else {
                    // Ahora también devolvemos un Map<String, Object> para que coincida el tipo
                    return ResponseEntity.status(401).body(Map.of("error", (Object) "Contraseña incorrecta"));
                }
            })
            .orElse(ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado")));
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String nuevaPassword = body.get("nuevaPassword");

        if (email == null || nuevaPassword == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Email y nueva contraseña son obligatorios"));
        }

        return usuarioRepository.findAll().stream()
            .filter(u -> u.getEmail().equals(email))
            .findFirst()
            .map(u -> {
                u.setPassword(passwordEncoder.encode(nuevaPassword));
                usuarioRepository.save(u);
                return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
            })
            .orElse(ResponseEntity.status(404)
                .body(Map.of("error", "Usuario no encontrado")));
    }
}