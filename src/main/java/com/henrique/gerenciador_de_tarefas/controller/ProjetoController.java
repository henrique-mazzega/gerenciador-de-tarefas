package com.henrique.gerenciador_de_tarefas.controller;

import com.henrique.gerenciador_de_tarefas.domain.Usuario;
import com.henrique.gerenciador_de_tarefas.dto.AdicionarMembroRequest;
import com.henrique.gerenciador_de_tarefas.dto.ProjetoRequest;
import com.henrique.gerenciador_de_tarefas.dto.ProjetoResponse;
import com.henrique.gerenciador_de_tarefas.service.ProjetoService;
import com.henrique.gerenciador_de_tarefas.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Projetos", description = "Criação e gestão de projetos e seus membros")
public class ProjetoController {

    private final ProjetoService projetoService;
    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Cria um novo projeto, tornando o usuário autenticado o dono")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<ProjetoResponse> criar(@Valid @RequestBody ProjetoRequest request,
                                                  Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projetoService.criar(request, autenticado(auth)));
    }

    @GetMapping
    @Operation(summary = "Lista os projetos em que o usuário autenticado é dono ou membro")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<List<ProjetoResponse>> listar(Authentication auth) {
        return ResponseEntity.ok(projetoService.listarDoUsuario(autenticado(auth)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um projeto pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projeto encontrado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem acesso ao projeto"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    public ResponseEntity<ProjetoResponse> buscarPorId(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(projetoService.buscarPorId(id, autenticado(auth)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza nome e descrição do projeto (apenas o dono)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projeto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Apenas o dono do projeto pode atualizá-lo"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    public ResponseEntity<ProjetoResponse> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody ProjetoRequest request,
                                                      Authentication auth) {
        return ResponseEntity.ok(projetoService.atualizar(id, request, autenticado(auth)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Exclui um projeto (apenas o dono)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Projeto excluído com sucesso"),
            @ApiResponse(responseCode = "403", description = "Apenas o dono do projeto pode excluí-lo"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    public ResponseEntity<Void> excluir(@PathVariable Long id, Authentication auth) {
        projetoService.excluir(id, autenticado(auth));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/membros")
    @Operation(summary = "Adiciona um usuário como membro do projeto (apenas o dono)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membro adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Apenas o dono do projeto pode adicionar membros"),
            @ApiResponse(responseCode = "404", description = "Projeto ou usuário não encontrado")
    })
    public ResponseEntity<ProjetoResponse> adicionarMembro(@PathVariable Long id,
                                                            @Valid @RequestBody AdicionarMembroRequest request,
                                                            Authentication auth) {
        return ResponseEntity.ok(projetoService.adicionarMembro(id, request, autenticado(auth)));
    }

    @DeleteMapping("/{id}/membros/{usuarioId}")
    @Operation(summary = "Remove um membro do projeto (apenas o dono)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Membro removido com sucesso"),
            @ApiResponse(responseCode = "403", description = "Apenas o dono do projeto pode remover membros"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado"),
            @ApiResponse(responseCode = "409", description = "O dono do projeto não pode ser removido dos membros")
    })
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
