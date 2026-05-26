package com.portfolio.questoes_aprova.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tentativas")
@Getter
@Setter
@NoArgsConstructor
public class Tentativa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "questao_id", referencedColumnName = "id"),
            @JoinColumn(name = "questao_ano", referencedColumnName = "ano")
    })
    private Questao questao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alternativa_escolhida_id")
    private Alternativa alternativaEscolhida;

    @Column(nullable = false)
    private Boolean correta;

    @Column(columnDefinition = "TEXT")
    private String explicacaoIa;

    @Column(nullable = false)
    private OffsetDateTime respondidoEm = OffsetDateTime.now();
}
