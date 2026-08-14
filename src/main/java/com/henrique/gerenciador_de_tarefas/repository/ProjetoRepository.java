package com.henrique.gerenciador_de_tarefas.repository;

import com.henrique.gerenciador_de_tarefas.domain.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    List<Projeto> findByDonoId(Long donoId);

    @Query("""
            SELECT DISTINCT p FROM Projeto p
            LEFT JOIN p.membros m
            WHERE p.dono.id = :usuarioId OR m.id = :usuarioId
            """)
    List<Projeto> findProjetosDoUsuario(@Param("usuarioId") Long usuarioId);

    boolean existsByIdAndDonoId(Long projetoId, Long donoId);

    boolean existsByIdAndMembrosId(Long projetoId, Long usuarioId);
}
