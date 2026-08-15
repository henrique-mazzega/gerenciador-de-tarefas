package com.henrique.gerenciador_de_tarefas.controller;

import com.henrique.gerenciador_de_tarefas.dto.LoginRequest;
import com.henrique.gerenciador_de_tarefas.dto.RegistrarRequest;
import com.henrique.gerenciador_de_tarefas.dto.TokenResponse;
import com.henrique.gerenciador_de_tarefas.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Registro e login de usuários")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registrar")
    @Operation(summary = "Registra um novo usuário e retorna o token de acesso")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    public ResponseEntity<TokenResponse> registrar(@Valid @RequestBody RegistrarRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica um usuário existente e retorna o token de acesso")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
