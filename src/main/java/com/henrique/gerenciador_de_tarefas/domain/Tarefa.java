package com.henrique.gerenciador_de_tarefas.domain;

import com.henrique.gerenciador_de_tarefas.enums.PrioridadeTarefa;
import com.henrique.gerenciador_de_tarefas.enums.StatusTarefa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tarefa")
@Getter
@Setter
@NoArgsConstructor
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusTarefa status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PrioridadeTarefa prioridade;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "projeto_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_tarefa_projeto")
    )
    private Projeto projeto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "responsavel_id",
            foreignKey = @ForeignKey(name = "fk_tarefa_responsavel")
    )
    private Usuario responsavel;

    private LocalDateTime prazo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;

    @Version
    private Long version;
}
