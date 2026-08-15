package com.henrique.gerenciador_de_tarefas.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginaResponse<T>(
        List<T> conteudo,
        long totalElementos,
        int paginaAtual,
        int totalPaginas
) {
    public static <T> PaginaResponse<T> de(Page<T> pagina) {
        return new PaginaResponse<>(
                pagina.getContent(),
                pagina.getTotalElements(),
                pagina.getNumber(),
                pagina.getTotalPages()
        );
    }
}
