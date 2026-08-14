package com.henrique.gerenciador_de_tarefas.service;

import com.henrique.gerenciador_de_tarefas.domain.Usuario;
import com.henrique.gerenciador_de_tarefas.dto.LoginRequest;
import com.henrique.gerenciador_de_tarefas.dto.RegistrarRequest;
import com.henrique.gerenciador_de_tarefas.dto.TokenResponse;
import com.henrique.gerenciador_de_tarefas.exception.RegraDeNegocioException;
import com.henrique.gerenciador_de_tarefas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public TokenResponse registrar(RegistrarRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RegraDeNegocioException("E-mail já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha()));

        Usuario salvo = usuarioRepository.save(usuario);
        return new TokenResponse(jwtService.gerarToken(salvo), "Bearer");
    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.senha()));

        Usuario usuario = usuarioRepository.findByEmail(request.email()).orElseThrow();
        return new TokenResponse(jwtService.gerarToken(usuario), "Bearer");
    }
}
