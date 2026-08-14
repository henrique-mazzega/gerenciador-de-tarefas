package com.henrique.gerenciador_de_tarefas.dto;

import com.henrique.gerenciador_de_tarefas.enums.PrioridadeTarefa;
import com.henrique.gerenciador_de_tarefas.enums.StatusTarefa;

import java.time.LocalDateTime;

public record TarefaResponse(
        Long id,
        String titulo,
        String descricao,
        StatusTarefa status,
        PrioridadeTarefa prioridade,
        LocalDateTime prazo,
        UsuarioResumoResponse responsavel,
        Long projetoId,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {}
