package com.portfolio.questoes_aprova.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Entity
@Table(name = "usuarios")
@SQLDelete(sql = "UPDATE usuarios SET ativo = false WHERE id = ?")
@Where(clause = "ativo = true")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID uuid = UUID.randomUUID();

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role = Role.ALUNO;

    @Column(nullable = false)
    private Boolean ativo = true;

    public enum Role {
        ALUNO,
        PROFESSOR,
        ADMIN
    }
}
