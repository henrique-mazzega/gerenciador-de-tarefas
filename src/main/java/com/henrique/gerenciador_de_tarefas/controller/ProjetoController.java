package com.henrique.gerenciador_de_tarefas.controller;

import com.henrique.gerenciador_de_tarefas.domain.Usuario;
import com.henrique.gerenciador_de_tarefas.dto.AdicionarMembroRequest;
import com.henrique.gerenciador_de_tarefas.dto.ProjetoRequest;
import com.henrique.gerenciador_de_tarefas.dto.ProjetoResponse;
import com.henrique.gerenciador_de_tarefas.service.ProjetoService;
import com.henrique.gerenciador_de_tarefas.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService projetoService;
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<ProjetoResponse> criar(@Valid @RequestBody ProjetoRequest request,
                                                  Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projetoService.criar(request, autenticado(auth)));
    }

    @GetMapping
    public ResponseEntity<List<ProjetoResponse>> listar(Authentication auth) {
        return ResponseEntity.ok(projetoService.listarDoUsuario(autenticado(auth)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponse> buscarPorId(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(projetoService.buscarPorId(id, autenticado(auth)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetoResponse> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody ProjetoRequest request,
                                                      Authentication auth) {
        return ResponseEntity.ok(projetoService.atualizar(id, request, autenticado(auth)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication auth) {
        projetoService.excluir(id, autenticado(auth));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/membros")
    public ResponseEntity<ProjetoResponse> adicionarMembro(@PathVariable Long id,
                                                            @Valid @RequestBody AdicionarMembroRequest request,
                                                            Authentication auth) {
        return ResponseEntity.ok(projetoService.adicionarMembro(id, request, autenticado(auth)));
    }

    @DeleteMapping("/{id}/membros/{usuarioId}")
    public ResponseEntity<Void> removerMembro(@PathVariable Long id,
                                               @PathVariable Long usuarioId,
                                               Authentication auth) {
        projetoService.removerMembro(id, usuarioId, autenticado(auth));
        return ResponseEntity.noContent().build();
    }

    private Usuario autenticado(Authentication auth) {
        return usuarioService.buscarPorEmail(auth.getName());
    }
}
