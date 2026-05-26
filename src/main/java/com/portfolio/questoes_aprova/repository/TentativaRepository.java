package com.portfolio.questoes_aprova.repository;

import com.portfolio.questoes_aprova.entity.Tentativa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TentativaRepository extends JpaRepository<Tentativa, Long> {
    List<Tentativa> findByUsuarioIdOrderByRespondidoEmDesc(Long usuarioId);
}
