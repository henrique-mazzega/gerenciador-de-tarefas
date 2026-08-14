package com.henrique.gerenciador_de_tarefas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projeto")
@Getter
@Setter
@NoArgsConstructor
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "dono_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_projeto_dono")
    )
    private Usuario dono;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "projeto_membro",
            joinColumns = @JoinColumn(
                    name = "projeto_id",
                    foreignKey = @ForeignKey(name = "fk_projeto_membro_projeto")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "usuario_id",
                    foreignKey = @ForeignKey(name = "fk_projeto_membro_usuario")
            )
    )
    private Set<Usuario> membros = new HashSet<>();
}
