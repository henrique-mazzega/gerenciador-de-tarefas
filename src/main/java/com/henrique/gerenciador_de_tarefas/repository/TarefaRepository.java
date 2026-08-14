package com.henrique.gerenciador_de_tarefas.repository;

import com.henrique.gerenciador_de_tarefas.domain.Tarefa;
import com.henrique.gerenciador_de_tarefas.enums.StatusTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long>, JpaSpecificationExecutor<Tarefa> {

    List<Tarefa> findByProjetoId(Long projetoId);

    long countByResponsavelIdAndStatus(Long responsavelId, StatusTarefa status);

    @Query("""
            SELECT t FROM Tarefa t
            WHERE t.projeto.id = :projetoId
            AND (LOWER(t.titulo) LIKE LOWER(CONCAT('%', :termo, '%'))
              OR LOWER(t.descricao) LIKE LOWER(CONCAT('%', :termo, '%')))
            """)
    List<Tarefa> buscarPorTexto(@Param("projetoId") Long projetoId, @Param("termo") String termo);
}
