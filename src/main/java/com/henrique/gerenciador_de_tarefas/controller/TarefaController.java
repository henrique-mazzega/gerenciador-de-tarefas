package com.henrique.gerenciador_de_tarefas.controller;

import com.henrique.gerenciador_de_tarefas.domain.Usuario;
import com.henrique.gerenciador_de_tarefas.dto.TarefaRequest;
import com.henrique.gerenciador_de_tarefas.dto.TarefaResponse;
import com.henrique.gerenciador_de_tarefas.service.TarefaService;
import com.henrique.gerenciador_de_tarefas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<TarefaResponse>> listarPorProjeto(@PathVariable Long projetoId, Authentication auth) {
        return ResponseEntity.ok(tarefaService.listarPorProjeto(projetoId, autenticado(auth)));
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
