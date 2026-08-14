package com.henrique.gerenciador_de_tarefas.dto;

import jakarta.validation.constraints.NotNull;

public record AdicionarMembroRequest(
        @NotNull(message = "ID do usuário é obrigatório")
        Long usuarioId
) {}
