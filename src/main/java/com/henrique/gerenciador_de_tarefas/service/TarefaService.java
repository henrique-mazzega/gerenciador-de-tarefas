package com.henrique.gerenciador_de_tarefas.service;

import com.henrique.gerenciador_de_tarefas.domain.Projeto;
import com.henrique.gerenciador_de_tarefas.domain.Tarefa;
import com.henrique.gerenciador_de_tarefas.domain.Usuario;
import com.henrique.gerenciador_de_tarefas.dto.TarefaRequest;
import com.henrique.gerenciador_de_tarefas.dto.TarefaResponse;
import com.henrique.gerenciador_de_tarefas.dto.UsuarioResumoResponse;
import com.henrique.gerenciador_de_tarefas.exception.RecursoNaoEncontradoException;
import com.henrique.gerenciador_de_tarefas.exception.RegraDeNegocioException;
import com.henrique.gerenciador_de_tarefas.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final ProjetoService projetoService;
    private final UsuarioService usuarioService;

    @Transactional
    public TarefaResponse criar(Long projetoId, TarefaRequest request, Usuario autenticado) {
        Projeto projeto = projetoService.buscarProjetoComAcesso(projetoId, autenticado);

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());
        tarefa.setStatus(request.status());
        tarefa.setPrioridade(request.prioridade());
        tarefa.setPrazo(request.prazo());
        tarefa.setProjeto(projeto);

        if (request.responsavelId() != null) {
            validarResponsavelEhMembro(projeto, request.responsavelId());
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

        tarefa.setTitulo(request.titulo());
        tarefa.setDescricao(request.descricao());
        tarefa.setStatus(request.status());
        tarefa.setPrioridade(request.prioridade());
        tarefa.setPrazo(request.prazo());

        if (request.responsavelId() != null) {
            validarResponsavelEhMembro(projeto, request.responsavelId());
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
    public List<TarefaResponse> listarPorProjeto(Long projetoId, Usuario autenticado) {
        projetoService.buscarProjetoComAcesso(projetoId, autenticado);
        return tarefaRepository.findByProjetoId(projetoId).stream().map(this::toResponse).toList();
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
