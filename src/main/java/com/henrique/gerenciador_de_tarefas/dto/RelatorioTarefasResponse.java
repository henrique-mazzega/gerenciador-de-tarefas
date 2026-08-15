package com.henrique.gerenciador_de_tarefas.dto;

import com.henrique.gerenciador_de_tarefas.enums.PrioridadeTarefa;
import com.henrique.gerenciador_de_tarefas.enums.StatusTarefa;

import java.util.Map;

public record RelatorioTarefasResponse(
        Map<StatusTarefa, Long> byStatus,
        Map<PrioridadeTarefa, Long> byPriority
) {}
