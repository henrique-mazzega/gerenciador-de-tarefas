package com.henrique.gerenciador_de_tarefas.dto;

import com.henrique.gerenciador_de_tarefas.enums.PrioridadeTarefa;
import com.henrique.gerenciador_de_tarefas.enums.StatusTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TarefaRequest(
        @NotBlank(message = "Título é obrigatório")
        @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
        String titulo,

        String descricao,

        @NotNull(message = "Status é obrigatório")
        StatusTarefa status,

        @NotNull(message = "Prioridade é obrigatória")
        PrioridadeTarefa prioridade,

        LocalDateTime prazo,

        Long responsavelId
) {}
