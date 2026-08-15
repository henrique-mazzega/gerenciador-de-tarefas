package com.henrique.gerenciador_de_tarefas.controller;

import com.henrique.gerenciador_de_tarefas.domain.Usuario;
import com.henrique.gerenciador_de_tarefas.dto.PaginaResponse;
import com.henrique.gerenciador_de_tarefas.dto.RelatorioTarefasResponse;
import com.henrique.gerenciador_de_tarefas.dto.TarefaRequest;
import com.henrique.gerenciador_de_tarefas.dto.TarefaResponse;
import com.henrique.gerenciador_de_tarefas.enums.PrioridadeTarefa;
import com.henrique.gerenciador_de_tarefas.enums.StatusTarefa;
import com.henrique.gerenciador_de_tarefas.service.TarefaService;
import com.henrique.gerenciador_de_tarefas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class TarefaController {

    private final TarefaService tarefaService;
    private final UsuarioService usuarioService;

    @PostMapping("/projetos/{projetoId}/tarefas")
    public ResponseEntity<TarefaResponse> criar(@PathVariable Long projetoId,
                                                @Valid @RequestBody TarefaRequest request,
                                                Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tarefaService.criar(projetoId, request, autenticado(auth)));
    }

    @GetMapping("/projetos/{projetoId}/tarefas")
    public ResponseEntity<PaginaResponse<TarefaResponse>> listarPorProjeto(
            @PathVariable Long projetoId,
            @RequestParam(required = false) StatusTarefa status,
            @RequestParam(required = false) PrioridadeTarefa prioridade,
            @RequestParam(required = false) Long responsavelId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime prazoInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime prazoFim,
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication auth) {
        return ResponseEntity.ok(tarefaService.listarPorProjeto(
                projetoId, status, prioridade, responsavelId, prazoInicio, prazoFim, pageable, autenticado(auth)));
    }

    @GetMapping("/projetos/{projetoId}/tarefas/busca")
    public ResponseEntity<PaginaResponse<TarefaResponse>> buscarPorTexto(
            @PathVariable Long projetoId,
            @RequestParam String termo,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication auth) {
        return ResponseEntity.ok(tarefaService.buscarPorTexto(projetoId, termo, pageable, autenticado(auth)));
    }

    @GetMapping("/projetos/{projetoId}/relatorio")
    public ResponseEntity<RelatorioTarefasResponse> relatorio(@PathVariable Long projetoId, Authentication auth) {
        return ResponseEntity.ok(tarefaService.gerarRelatorio(projetoId, autenticado(auth)));
    }

    @GetMapping("/tarefas/{id}")
    public ResponseEntity<TarefaResponse> buscarPorId(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(tarefaService.buscarPorId(id, autenticado(auth)));
    }

    @PutMapping("/tarefas/{id}")
    public ResponseEntity<TarefaResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody TarefaRequest request,
                                                    Authentication auth) {
        return ResponseEntity.ok(tarefaService.atualizar(id, request, autenticado(auth)));
    }

    @DeleteMapping("/tarefas/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication auth) {
        tarefaService.excluir(id, autenticado(auth));
        return ResponseEntity.noContent().build();
    }

    private Usuario autenticado(Authentication auth) {
        return usuarioService.buscarPorEmail(auth.getName());
    }
}
