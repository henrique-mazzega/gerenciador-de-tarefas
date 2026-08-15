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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tarefas", description = "Criação, consulta, atualização e relatórios de tarefas de um projeto")
public class TarefaController {

    private final TarefaService tarefaService;
    private final UsuarioService usuarioService;

    @PostMapping("/projetos/{projetoId}/tarefas")
    @Operation(summary = "Cria uma tarefa em um projeto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Usuário sem acesso ao projeto, ou tentativa de criar tarefa crítica já concluída sem ser o dono"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
            @ApiResponse(responseCode = "409", description = "Responsável já atingiu o limite de tarefas em andamento")
    })
    public ResponseEntity<TarefaResponse> criar(@PathVariable Long projetoId,
                                                @Valid @RequestBody TarefaRequest request,
                                                Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tarefaService.criar(projetoId, request, autenticado(auth)));
    }

    @GetMapping("/projetos/{projetoId}/tarefas")
    @Operation(summary = "Lista as tarefas de um projeto, com filtros, ordenação e paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário sem acesso ao projeto"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
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
    @Operation(summary = "Busca textual paginada por título ou descrição da tarefa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário sem acesso ao projeto"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    public ResponseEntity<PaginaResponse<TarefaResponse>> buscarPorTexto(
            @PathVariable Long projetoId,
            @RequestParam String termo,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication auth) {
        return ResponseEntity.ok(tarefaService.buscarPorTexto(projetoId, termo, pageable, autenticado(auth)));
    }

    @GetMapping("/projetos/{projetoId}/relatorio")
    @Operation(summary = "Gera o relatório de contagem de tarefas por status e prioridade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário sem acesso ao projeto"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    public ResponseEntity<RelatorioTarefasResponse> relatorio(@PathVariable Long projetoId, Authentication auth) {
        return ResponseEntity.ok(tarefaService.gerarRelatorio(projetoId, autenticado(auth)));
    }

    @GetMapping("/tarefas/{id}")
    @Operation(summary = "Busca uma tarefa pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada"),
            @ApiResponse(responseCode = "403", description = "Usuário sem acesso ao projeto da tarefa"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ResponseEntity<TarefaResponse> buscarPorId(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(tarefaService.buscarPorId(id, autenticado(auth)));
    }

    @PutMapping("/tarefas/{id}")
    @Operation(summary = "Atualiza uma tarefa, incluindo transição de status e troca de responsável")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Usuário sem acesso ao projeto, ou tentativa de concluir tarefa crítica sem ser o dono"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
            @ApiResponse(responseCode = "409", description = "Transição de status inválida (DONE para TODO) ou limite de tarefas em andamento atingido")
    })
    public ResponseEntity<TarefaResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody TarefaRequest request,
                                                    Authentication auth) {
        return ResponseEntity.ok(tarefaService.atualizar(id, request, autenticado(auth)));
    }

    @DeleteMapping("/tarefas/{id}")
    @Operation(summary = "Exclui uma tarefa")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarefa excluída com sucesso"),
            @ApiResponse(responseCode = "403", description = "Usuário sem acesso ao projeto da tarefa"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication auth) {
        tarefaService.excluir(id, autenticado(auth));
        return ResponseEntity.noContent().build();
    }

    private Usuario autenticado(Authentication auth) {
        return usuarioService.buscarPorEmail(auth.getName());
    }
}
