package com.henrique.gerenciador_de_tarefas.service;

import com.henrique.gerenciador_de_tarefas.domain.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String segredo;

    @Value("${jwt.expiration}")
    private long expiracao;

    public String gerarToken(Usuario usuario) {
        Date agora = new Date();
        Date expira = new Date(agora.getTime() + expiracao);

        return Jwts.builder()
                .subject(usuario.getEmail())
                .issuedAt(agora)
                .expiration(expira)
                .signWith(chaveAssinatura(), Jwts.SIG.HS256)
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairPayload(token).getSubject();
    }

    public boolean tokenValido(String token) {
        try {
            extrairPayload(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims extrairPayload(String token) {
        return Jwts.parser()
                .verifyWith(chaveAssinatura())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey chaveAssinatura() {
        return Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
    }
}
