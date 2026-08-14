package com.henrique.gerenciador_de_tarefas.dto;

import java.util.List;

public record ProjetoResponse(
        Long id,
        String nome,
        String descricao,
        UsuarioResumoResponse dono,
        List<UsuarioResumoResponse> membros
) {}
