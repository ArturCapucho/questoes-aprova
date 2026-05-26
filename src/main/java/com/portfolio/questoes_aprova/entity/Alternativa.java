package com.portfolio.questoes_aprova.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alternativas")
@Getter
@Setter
@NoArgsConstructor
public class Alternativa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "questao_id", referencedColumnName = "id"),
            @JoinColumn(name = "questao_ano", referencedColumnName = "ano")
    })
    private Questao questao;

    @Column(nullable = false, length = 1)
    private Character letra;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Column(nullable = false)
    private Boolean correta = false;
}
