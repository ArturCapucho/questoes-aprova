package com.portfolio.questoes_aprova.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questoes")
@IdClass(Questao.QuestaoId.class)
@SQLDelete(sql = "UPDATE questoes SET ativo = false WHERE id = ? AND ano = ?")
@Where(clause = "ativo = true")
@Getter
@Setter
@NoArgsConstructor
public class Questao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Id
    private Integer ano;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(columnDefinition = "TEXT")
    private String enunciado;
    private String banca;
    private String orgao;

    @Column(nullable = false, length = 120)
    private String origem = "MANUAL";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @OneToMany(mappedBy = "questao", cascade = CascadeType.ALL)
    private List<Alternativa> alternativas;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestaoId implements Serializable {
        private Long id;
        private Integer ano;
    }
}
