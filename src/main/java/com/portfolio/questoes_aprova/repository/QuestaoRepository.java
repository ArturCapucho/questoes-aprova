package com.portfolio.questoes_aprova.repository;

import com.portfolio.questoes_aprova.entity.Questao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestaoRepository extends JpaRepository<Questao, Questao.QuestaoId> {
    List<Questao> findByCategoriaSlug(String slug);
}
