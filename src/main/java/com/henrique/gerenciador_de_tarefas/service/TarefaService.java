package com.henrique.gerenciador_de_tarefas.service;

import com.henrique.gerenciador_de_tarefas.domain.Projeto;
import com.henrique.gerenciador_de_tarefas.domain.Tarefa;
import com.henrique.gerenciador_de_tarefas.domain.Usuario;
import com.henrique.gerenciador_de_tarefas.dto.PaginaResponse;
import com.henrique.gerenciador_de_tarefas.dto.RelatorioTarefasResponse;
import com.henrique.gerenciador_de_tarefas.dto.TarefaRequest;
import com.henrique.gerenciador_de_tarefas.dto.TarefaResponse;
import com.henrique.gerenciador_de_tarefas.dto.UsuarioResumoResponse;
import com.henrique.gerenciador_de_tarefas.enums.PrioridadeTarefa;
import com.henrique.gerenciador_de_tarefas.enums.StatusTarefa;
import com.henrique.gerenciador_de_tarefas.exception.AcessoNegadoException;
import com.henrique.gerenciador_de_tarefas.exception.RecursoNaoEncontradoException;
import com.henrique.gerenciador_de_tarefas.exception.RegraDeNegocioException;
import com.henrique.gerenciador_de_tarefas.repository.TarefaRepository;
import com.henrique.gerenciador_de_tarefas.repository.specification.TarefaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private static final int LIMITE_TAREFAS_EM_ANDAMENTO = 5;

    private final TarefaRepository tarefaRepository;
    private final ProjetoService projetoService;
    private final UsuarioService usuarioService;

    @Transactional
    public TarefaResponse criar(Long projetoId, TarefaRequest request, Usuario autenticado) {
        Projeto projeto = projetoService.buscarProjetoComAcesso(projetoId, autenticado);

        validarFechamentoDeTarefaCritica(request.prioridade(), request.status(), projeto, autenticado);

        if (request.responsavelId() != null) {
            validarResponsavelEhMembro(projeto, request.responsavelId());
        }

        validarLimiteDeTarefasEmAndamento(request.responsavelId(), null, request.status());

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());
        tarefa.setStatus(request.status());
        tarefa.setPrioridade(request.prioridade());
        tarefa.setPrazo(request.prazo());
        tarefa.setProjeto(projeto);

        if (request.responsavelId() != null) {
            tarefa.setResponsavel(usuarioService.buscarPorId(request.responsavelId()));
        }

        return toResponse(tarefaRepository.save(tarefa));
    }

    @Transactional(readOnly = true)
    public TarefaResponse buscarPorId(Long tarefaId, Usuario autenticado) {
        Tarefa tarefa = buscarOuLancarErro(tarefaId);
        projetoService.buscarProjetoComAcesso(tarefa.getProjeto().getId(), autenticado);
        return toResponse(tarefa);
    }

    @Transactional
    public TarefaResponse atualizar(Long tarefaId, TarefaRequest request, Usuario autenticado) {
        Tarefa tarefa = buscarOuLancarErro(tarefaId);
        Projeto projeto = projetoService.buscarProjetoComAcesso(tarefa.getProjeto().getId(), autenticado);

        validarTransicaoDeStatus(tarefa.getStatus(), request.status());

        validarFechamentoDeTarefaCritica(request.prioridade(), request.status(), projeto, autenticado);

        if (request.responsavelId() != null) {
            validarResponsavelEhMembro(projeto, request.responsavelId());
        }

        validarLimiteDeTarefasEmAndamento(request.responsavelId(), tarefa, request.status());

        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());
        tarefa.setStatus(request.status());
        tarefa.setPrioridade(request.prioridade());
        tarefa.setPrazo(request.prazo());

        if (request.responsavelId() != null) {
            tarefa.setResponsavel(usuarioService.buscarPorId(request.responsavelId()));
        } else {
            tarefa.setResponsavel(null);
        }

        return toResponse(tarefaRepository.save(tarefa));
    }

    @Transactional
    public void excluir(Long tarefaId, Usuario autenticado) {
        Tarefa tarefa = buscarOuLancarErro(tarefaId);
        projetoService.buscarProjetoComAcesso(tarefa.getProjeto().getId(), autenticado);
        tarefaRepository.delete(tarefa);
    }

    @Transactional(readOnly = true)
    public PaginaResponse<TarefaResponse> listarPorProjeto(Long projetoId,
                                                            StatusTarefa status,
                                                            PrioridadeTarefa prioridade,
                                                            Long responsavelId,
                                                            LocalDateTime prazoInicio,
                                                            LocalDateTime prazoFim,
                                                            Pageable pageable,
                                                            Usuario autenticado) {
        projetoService.buscarProjetoComAcesso(projetoId, autenticado);

        Specification<Tarefa> spec = TarefaSpecification
                .comFiltros(projetoId, status, prioridade, responsavelId, prazoInicio, prazoFim)
                .and(TarefaSpecification.ordenadoPor(pageable.getSort()));

        Pageable paginacaoSemOrdenacao = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Page<Tarefa> pagina = tarefaRepository.findAll(spec, paginacaoSemOrdenacao);
        return PaginaResponse.de(pagina.map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public PaginaResponse<TarefaResponse> buscarPorTexto(Long projetoId, String termo, Pageable pageable, Usuario autenticado) {
        projetoService.buscarProjetoComAcesso(projetoId, autenticado);

        Pageable paginacaoSemOrdenacao = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Page<Tarefa> pagina = tarefaRepository.buscarPorTexto(projetoId, termo, paginacaoSemOrdenacao);
        return PaginaResponse.de(pagina.map(this::toResponse));
    }

    @Transactional(readOnly = true)
    public RelatorioTarefasResponse gerarRelatorio(Long projetoId, Usuario autenticado) {
        projetoService.buscarProjetoComAcesso(projetoId, autenticado);

        Map<StatusTarefa, Long> byStatus = new EnumMap<>(StatusTarefa.class);
        for (StatusTarefa status : StatusTarefa.values()) {
            byStatus.put(status, 0L);
        }
        for (Object[] linha : tarefaRepository.contarPorStatus(projetoId)) {
            byStatus.put((StatusTarefa) linha[0], (Long) linha[1]);
        }

        Map<PrioridadeTarefa, Long> byPriority = new EnumMap<>(PrioridadeTarefa.class);
        for (PrioridadeTarefa prioridade : PrioridadeTarefa.values()) {
            byPriority.put(prioridade, 0L);
        }
        for (Object[] linha : tarefaRepository.contarPorPrioridade(projetoId)) {
            byPriority.put((PrioridadeTarefa) linha[0], (Long) linha[1]);
        }

        return new RelatorioTarefasResponse(byStatus, byPriority);
    }

    /**
     * tarefa DONE não pode retornar para TODO, apenas para IN_PROGRESS.
     */
    private void validarTransicaoDeStatus(StatusTarefa atual, StatusTarefa novo) {
        if (atual == StatusTarefa.DONE && novo == StatusTarefa.TODO) {
            throw new RegraDeNegocioException(
                    "Tarefa concluída (DONE) não pode retornar para TODO. " +
                    "A transição permitida é DONE → IN_PROGRESS");
        }
    }

    /**
     * Tarefa com prioridade CRITICAL só pode ser movida para DONE pelo dono do projeto.
     */
    private void validarFechamentoDeTarefaCritica(PrioridadeTarefa prioridade,
                                                   StatusTarefa novoStatus,
                                                   Projeto projeto,
                                                   Usuario autenticado) {
        if (prioridade != PrioridadeTarefa.CRITICAL || novoStatus != StatusTarefa.DONE) {
            return;
        }
        if (!projeto.getDono().getId().equals(autenticado.getId())) {
            throw new AcessoNegadoException(
                    "Apenas o administrador do projeto pode concluir tarefas críticas");
        }
    }

    /**
     * Um responsável não pode ter mais que o LIMITE_TAREFAS_EM_ANDAMENTO.
     * Não contabiliza a própria tarefa sendo atualizada quando ela já está IN_PROGRESS
     * com o mesmo responsável.
     */
    private void validarLimiteDeTarefasEmAndamento(Long responsavelId,
                                                    Tarefa tarefaAtual,
                                                    StatusTarefa novoStatus) {
        if (novoStatus != StatusTarefa.IN_PROGRESS || responsavelId == null) {
            return;
        }

        // Se a tarefa já está IN_PROGRESS para o mesmo responsável, o slot já está ocupado no banco.
        boolean slotJaOcupado = tarefaAtual != null
                && tarefaAtual.getStatus() == StatusTarefa.IN_PROGRESS
                && tarefaAtual.getResponsavel() != null
                && tarefaAtual.getResponsavel().getId().equals(responsavelId);

        if (!slotJaOcupado) {
            long total = tarefaRepository.countByResponsavelIdAndStatus(responsavelId, StatusTarefa.IN_PROGRESS);
            if (total >= LIMITE_TAREFAS_EM_ANDAMENTO) {
                throw new RegraDeNegocioException(
                        "Limite de " + LIMITE_TAREFAS_EM_ANDAMENTO + " tarefas em andamento atingido para este responsável");
            }
        }
    }

    private void validarResponsavelEhMembro(Projeto projeto, Long responsavelId) {
        boolean ehMembro = projeto.getMembros().stream().anyMatch(m -> m.getId().equals(responsavelId));
        if (!ehMembro) {
            throw new RegraDeNegocioException(
                    "O responsável deve ser membro do projeto");
        }
    }

    private Tarefa buscarOuLancarErro(Long id) {
        return tarefaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa não encontrada"));
    }

    private TarefaResponse toResponse(Tarefa tarefa) {
        UsuarioResumoResponse responsavelDto = null;
        if (tarefa.getResponsavel() != null) {
            Usuario r = tarefa.getResponsavel();
            responsavelDto = new UsuarioResumoResponse(r.getId(), r.getNome(), r.getEmail());
        }
        return new TarefaResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getPrioridade(),
                tarefa.getPrazo(),
                responsavelDto,
                tarefa.getProjeto().getId(),
                tarefa.getDataCriacao(),
                tarefa.getDataAtualizacao()
        );
    }
}
