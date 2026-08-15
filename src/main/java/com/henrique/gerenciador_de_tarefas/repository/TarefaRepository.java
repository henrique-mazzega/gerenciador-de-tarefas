package com.henrique.gerenciador_de_tarefas.repository;

import com.henrique.gerenciador_de_tarefas.domain.Tarefa;
import com.henrique.gerenciador_de_tarefas.enums.StatusTarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long>, JpaSpecificationExecutor<Tarefa> {

    long countByResponsavelIdAndStatus(Long responsavelId, StatusTarefa status);

    @Query("SELECT t.status, COUNT(t) FROM Tarefa t WHERE t.projeto.id = :projetoId GROUP BY t.status")
    List<Object[]> contarPorStatus(@Param("projetoId") Long projetoId);

    @Query("SELECT t.prioridade, COUNT(t) FROM Tarefa t WHERE t.projeto.id = :projetoId GROUP BY t.prioridade")
    List<Object[]> contarPorPrioridade(@Param("projetoId") Long projetoId);

    @Query(value = """
            SELECT * FROM tarefa t
            WHERE t.projeto_id = :projetoId
              AND (t.titulo ILIKE CONCAT('%', :termo, '%')
                OR t.descricao ILIKE CONCAT('%', :termo, '%'))
            """,
            countQuery = """
            SELECT COUNT(*) FROM tarefa t
            WHERE t.projeto_id = :projetoId
              AND (t.titulo ILIKE CONCAT('%', :termo, '%')
                OR t.descricao ILIKE CONCAT('%', :termo, '%'))
            """,
            nativeQuery = true)
    Page<Tarefa> buscarPorTexto(@Param("projetoId") Long projetoId, @Param("termo") String termo, Pageable pageable);
}
