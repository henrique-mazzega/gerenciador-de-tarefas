package com.henrique.gerenciador_de_tarefas.service;

import com.henrique.gerenciador_de_tarefas.domain.Projeto;
import com.henrique.gerenciador_de_tarefas.domain.Usuario;
import com.henrique.gerenciador_de_tarefas.dto.AdicionarMembroRequest;
import com.henrique.gerenciador_de_tarefas.dto.ProjetoRequest;
import com.henrique.gerenciador_de_tarefas.dto.ProjetoResponse;
import com.henrique.gerenciador_de_tarefas.dto.UsuarioResumoResponse;
import com.henrique.gerenciador_de_tarefas.exception.AcessoNegadoException;
import com.henrique.gerenciador_de_tarefas.exception.RecursoNaoEncontradoException;
import com.henrique.gerenciador_de_tarefas.exception.RegraDeNegocioException;
import com.henrique.gerenciador_de_tarefas.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final UsuarioService usuarioService;

    private void validarAcesso(Projeto projeto, Usuario usuario) {
        boolean ehDono = projeto.getDono().getId().equals(usuario.getId());
        boolean ehMembro = projeto.getMembros().stream().anyMatch(m -> m.getId().equals(usuario.getId()));
        if (!ehDono && !ehMembro) {
            throw new AcessoNegadoException("Você não tem acesso a este projeto");
        }
    }

    private void validarEhDono(Projeto projeto, Usuario usuario) {
        if (!projeto.getDono().getId().equals(usuario.getId())) {
            throw new AcessoNegadoException("Apenas o dono do projeto pode realizar esta operação");
        }
    }

    @Transactional(readOnly = true)
    public Projeto buscarProjetoComAcesso(Long projetoId, Usuario usuario) {
        Projeto projeto = buscarOuLancarErro(projetoId);
        validarAcesso(projeto, usuario);
        return projeto;
    }

    @Transactional
    public ProjetoResponse criar(ProjetoRequest request, Usuario autenticado) {
        Projeto projeto = new Projeto();
        projeto.setNome(request.nome());
        projeto.setDescricao(request.descricao());
        projeto.setDono(autenticado);
        projeto.getMembros().add(autenticado);

        return toResponse(projetoRepository.save(projeto));
    }

    @Transactional(readOnly = true)
    public List<ProjetoResponse> listarDoUsuario(Usuario autenticado) {
        return projetoRepository.findProjetosDoUsuario(autenticado.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjetoResponse buscarPorId(Long id, Usuario autenticado) {
        Projeto projeto = buscarOuLancarErro(id);
        validarAcesso(projeto, autenticado);
        return toResponse(projeto);
    }

    @Transactional
    public ProjetoResponse atualizar(Long id, ProjetoRequest request, Usuario autenticado) {
        Projeto projeto = buscarOuLancarErro(id);
        validarEhDono(projeto, autenticado);

        projeto.setNome(request.nome());
        projeto.setDescricao(request.descricao());

        return toResponse(projetoRepository.save(projeto));
    }

    @Transactional
    public void excluir(Long id, Usuario autenticado) {
        Projeto projeto = buscarOuLancarErro(id);
        validarEhDono(projeto, autenticado);
        projetoRepository.delete(projeto);
    }

    @Transactional
    public ProjetoResponse adicionarMembro(Long projetoId, AdicionarMembroRequest request, Usuario autenticado) {
        Projeto projeto = buscarOuLancarErro(projetoId);
        validarEhDono(projeto, autenticado);

        Usuario novoMembro = usuarioService.buscarPorEmail(request.email());

        boolean jaEhMembro = projeto.getMembros().stream()
                .anyMatch(m -> m.getId().equals(novoMembro.getId()));
        if (jaEhMembro) {
            throw new RegraDeNegocioException("Usuário já é membro deste projeto");
        }

        projeto.getMembros().add(novoMembro);

        return toResponse(projetoRepository.save(projeto));
    }

    @Transactional
    public void removerMembro(Long projetoId, Long usuarioId, Usuario autenticado) {
        Projeto projeto = buscarOuLancarErro(projetoId);
        validarEhDono(projeto, autenticado);

        if (projeto.getDono().getId().equals(usuarioId)) {
            throw new RegraDeNegocioException("O dono do projeto não pode ser removido dos membros");
        }

        projeto.getMembros().removeIf(m -> m.getId().equals(usuarioId));
        projetoRepository.save(projeto);
    }

    private Projeto buscarOuLancarErro(Long id) {
        return projetoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Projeto não encontrado"));
    }

    private UsuarioResumoResponse toResumo(Usuario usuario) {
        return new UsuarioResumoResponse(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }

    private ProjetoResponse toResponse(Projeto projeto) {
        List<UsuarioResumoResponse> membrosDto =
                projeto.getMembros().stream()
                                    .map(this::toResumo)
                                    .toList();
        return new ProjetoResponse(
                projeto.getId(),
                projeto.getNome(),
                projeto.getDescricao(),
                toResumo(projeto.getDono()),
                membrosDto
        );
    }
}
