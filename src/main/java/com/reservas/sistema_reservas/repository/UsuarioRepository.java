package com.reservas.sistema_reservas.repository;


import com.reservas.sistema_reservas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	
	
}