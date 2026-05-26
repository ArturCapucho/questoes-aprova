package com.portfolio.questoes_aprova.repository;

import com.portfolio.questoes_aprova.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Repository Pattern: isola o acesso ao banco e deixa a camada de servico falar em operacoes de dominio.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByUuid(UUID uuid);

    boolean existsByEmail(String email);
}
