package com.henrique.gerenciador_de_tarefas.repository.specification;

import com.henrique.gerenciador_de_tarefas.domain.Tarefa;
import com.henrique.gerenciador_de_tarefas.enums.PrioridadeTarefa;
import com.henrique.gerenciador_de_tarefas.enums.StatusTarefa;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

public class TarefaSpecification {

    private TarefaSpecification() {
    }

    public static Specification<Tarefa> comProjetoId(Long projetoId) {
        return (root, query, cb) -> cb.equal(root.get("projeto").get("id"), projetoId);
    }

    public static Specification<Tarefa> comStatus(StatusTarefa status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Tarefa> comPrioridade(PrioridadeTarefa prioridade) {
        return (root, query, cb) -> prioridade == null ? null : cb.equal(root.get("prioridade"), prioridade);
    }

    public static Specification<Tarefa> comResponsavelId(Long responsavelId) {
        return (root, query, cb) -> responsavelId == null ? null : cb.equal(root.get("responsavel").get("id"), responsavelId);
    }

    public static Specification<Tarefa> comPrazoApos(LocalDateTime prazoInicio) {
        return (root, query, cb) -> prazoInicio == null ? null : cb.greaterThanOrEqualTo(root.get("prazo"), prazoInicio);
    }

    public static Specification<Tarefa> comPrazoAntes(LocalDateTime prazoFim) {
        return (root, query, cb) -> prazoFim == null ? null : cb.lessThanOrEqualTo(root.get("prazo"), prazoFim);
    }

    public static Specification<Tarefa> comFiltros(Long projetoId,
                                                   StatusTarefa status,
                                                   PrioridadeTarefa prioridade,
                                                   Long responsavelId,
                                                   LocalDateTime prazoInicio,
                                                   LocalDateTime prazoFim) {
        return comProjetoId(projetoId)
                .and(comStatus(status))
                .and(comPrioridade(prioridade))
                .and(comResponsavelId(responsavelId))
                .and(comPrazoApos(prazoInicio))
                .and(comPrazoAntes(prazoFim));
    }

    public static Specification<Tarefa> ordenadoPor(Sort sort) {
        return (root, query, cb) -> {
            if (sort.isSorted()) {
                List<Order> orders = sort.stream()
                        .map(order -> {
                            Expression<?> expressao = "prioridade".equals(order.getProperty())
                                    ? ordemLogicaPrioridade(root, cb)
                                    : root.get(order.getProperty());
                            return order.isAscending() ? cb.asc(expressao) : cb.desc(expressao);
                        })
                        .toList();
                query.orderBy(orders);
            }
            return null;
        };
    }

    private static Expression<Integer> ordemLogicaPrioridade(Root<Tarefa> root, jakarta.persistence.criteria.CriteriaBuilder cb) {
        return cb.<PrioridadeTarefa, Integer>selectCase(root.get("prioridade"))
                .when(PrioridadeTarefa.LOW, 1)
                .when(PrioridadeTarefa.MEDIUM, 2)
                .when(PrioridadeTarefa.HIGH, 3)
                .when(PrioridadeTarefa.CRITICAL, 4)
                .otherwise(0);
    }
}
