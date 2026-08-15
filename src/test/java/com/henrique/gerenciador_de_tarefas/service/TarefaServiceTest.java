package com.henrique.gerenciador_de_tarefas.service;

import com.henrique.gerenciador_de_tarefas.domain.Projeto;
import com.henrique.gerenciador_de_tarefas.domain.Tarefa;
import com.henrique.gerenciador_de_tarefas.domain.Usuario;
import com.henrique.gerenciador_de_tarefas.dto.TarefaRequest;
import com.henrique.gerenciador_de_tarefas.enums.PrioridadeTarefa;
import com.henrique.gerenciador_de_tarefas.enums.StatusTarefa;
import com.henrique.gerenciador_de_tarefas.exception.AcessoNegadoException;
import com.henrique.gerenciador_de_tarefas.exception.RegraDeNegocioException;
import com.henrique.gerenciador_de_tarefas.repository.TarefaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private ProjetoService projetoService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private TarefaService tarefaService;

    @Test
    void devePermitirMoverTarefaDoneParaInProgress() {
        Usuario dono = criarUsuario(1L);
        Projeto projeto = criarProjeto(dono, Set.of(dono));
        Tarefa tarefa = criarTarefa(1L, StatusTarefa.DONE, PrioridadeTarefa.MEDIUM, null, projeto);

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(projetoService.buscarProjetoComAcesso(anyLong(), any())).thenReturn(projeto);
        when(tarefaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() ->
                tarefaService.atualizar(1L, criarRequest(StatusTarefa.IN_PROGRESS, PrioridadeTarefa.MEDIUM, null), dono));
    }

    @Test
    void deveLancarExcecaoAoMoverTarefaDoneParaTodo() {
        Usuario dono = criarUsuario(1L);
        Projeto projeto = criarProjeto(dono, Set.of(dono));
        Tarefa tarefa = criarTarefa(1L, StatusTarefa.DONE, PrioridadeTarefa.MEDIUM, null, projeto);

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(projetoService.buscarProjetoComAcesso(anyLong(), any())).thenReturn(projeto);

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class, () ->
                tarefaService.atualizar(1L, criarRequest(StatusTarefa.TODO, PrioridadeTarefa.MEDIUM, null), dono));

        assertTrue(ex.getMessage().contains("TODO"),
                "Mensagem deve mencionar o status TODO proibido na transição");
    }

    @Test
    void devePermitirMoverTarefaTodoParaInProgress() {
        Usuario dono = criarUsuario(1L);
        Projeto projeto = criarProjeto(dono, Set.of(dono));
        Tarefa tarefa = criarTarefa(1L, StatusTarefa.TODO, PrioridadeTarefa.MEDIUM, null, projeto);

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(projetoService.buscarProjetoComAcesso(anyLong(), any())).thenReturn(projeto);
        when(tarefaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() ->
                tarefaService.atualizar(1L, criarRequest(StatusTarefa.IN_PROGRESS, PrioridadeTarefa.MEDIUM, null), dono));
    }

    @Test
    void devePermitirQueDonoDoProjetoConcluaTarefaCritical() {
        Usuario dono = criarUsuario(1L);
        Projeto projeto = criarProjeto(dono, Set.of(dono));
        Tarefa tarefa = criarTarefa(1L, StatusTarefa.IN_PROGRESS, PrioridadeTarefa.CRITICAL, null, projeto);

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(projetoService.buscarProjetoComAcesso(anyLong(), any())).thenReturn(projeto);
        when(tarefaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() ->
                tarefaService.atualizar(1L, criarRequest(StatusTarefa.DONE, PrioridadeTarefa.CRITICAL, null), dono));
    }

    @Test
    void deveLancarExcecaoQuandoMembroComumTentaConcluirTarefaCritical() {
        Usuario dono = criarUsuario(1L);
        Usuario membro = criarUsuario(2L);
        Projeto projeto = criarProjeto(dono, Set.of(dono, membro));
        Tarefa tarefa = criarTarefa(1L, StatusTarefa.IN_PROGRESS, PrioridadeTarefa.CRITICAL, null, projeto);

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(projetoService.buscarProjetoComAcesso(anyLong(), any())).thenReturn(projeto);

        assertThrows(AcessoNegadoException.class, () ->
                tarefaService.atualizar(1L, criarRequest(StatusTarefa.DONE, PrioridadeTarefa.CRITICAL, null), membro));
    }

    @Test
    void devePermitirQueMembroComumConcluaTarefaComPrioridadeHigh() {
        Usuario dono = criarUsuario(1L);
        Usuario membro = criarUsuario(2L);
        Projeto projeto = criarProjeto(dono, Set.of(dono, membro));
        Tarefa tarefa = criarTarefa(1L, StatusTarefa.IN_PROGRESS, PrioridadeTarefa.HIGH, null, projeto);

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(projetoService.buscarProjetoComAcesso(anyLong(), any())).thenReturn(projeto);
        when(tarefaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> tarefaService.atualizar(1L, criarRequest(StatusTarefa.DONE, PrioridadeTarefa.HIGH, null), membro));
    }

    @Test
    void devePermitirAtribuirTarefaQuandoResponsavelTemQuatroTarefasEmAndamento() {
        Usuario dono = criarUsuario(1L);
        Usuario responsavel = criarUsuario(2L);
        Projeto projeto = criarProjeto(dono, Set.of(dono, responsavel));
        Tarefa tarefa = criarTarefa(1L, StatusTarefa.TODO, PrioridadeTarefa.MEDIUM, null, projeto);

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(projetoService.buscarProjetoComAcesso(anyLong(), any())).thenReturn(projeto);
        when(tarefaRepository.countByResponsavelIdAndStatus(2L, StatusTarefa.IN_PROGRESS)).thenReturn(4L);
        when(usuarioService.buscarPorId(2L)).thenReturn(responsavel);
        when(tarefaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() ->
                tarefaService.atualizar(1L, criarRequest(StatusTarefa.IN_PROGRESS, PrioridadeTarefa.MEDIUM, 2L), dono));
    }

    @Test
    void deveLancarExcecaoQuandoResponsavelAtingiuLimiteDeTarefasEmAndamento() {
        Usuario dono = criarUsuario(1L);
        Usuario responsavel = criarUsuario(2L);
        Projeto projeto = criarProjeto(dono, Set.of(dono, responsavel));
        Tarefa tarefa = criarTarefa(1L, StatusTarefa.TODO, PrioridadeTarefa.MEDIUM, null, projeto);

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(projetoService.buscarProjetoComAcesso(anyLong(), any())).thenReturn(projeto);
        when(tarefaRepository.countByResponsavelIdAndStatus(2L, StatusTarefa.IN_PROGRESS)).thenReturn(5L);

        RegraDeNegocioException ex = assertThrows(RegraDeNegocioException.class, () ->
                tarefaService.atualizar(1L, criarRequest(StatusTarefa.IN_PROGRESS, PrioridadeTarefa.MEDIUM, 2L), dono));

        assertTrue(ex.getMessage().contains("5"),
                "Mensagem deve informar o limite de 5 tarefas");
    }

    @Test
    void devePermitirMoverParaDoneMesmoComResponsavelNoLimite() {
        Usuario dono = criarUsuario(1L);
        Usuario responsavel = criarUsuario(2L);
        Projeto projeto = criarProjeto(dono, Set.of(dono, responsavel));
        Tarefa tarefa = criarTarefa(1L, StatusTarefa.IN_PROGRESS, PrioridadeTarefa.MEDIUM, responsavel, projeto);

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(projetoService.buscarProjetoComAcesso(anyLong(), any())).thenReturn(projeto);
        when(usuarioService.buscarPorId(2L)).thenReturn(responsavel);
        when(tarefaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() ->
                tarefaService.atualizar(1L, criarRequest(StatusTarefa.DONE, PrioridadeTarefa.MEDIUM, 2L), dono));
    }

    private Usuario criarUsuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Usuario " + id);
        usuario.setEmail("usuario" + id + "@teste.com");
        usuario.setSenha("senha-hash");
        return usuario;
    }

    private Projeto criarProjeto(Usuario dono, Set<Usuario> membros) {
        Projeto projeto = new Projeto();
        projeto.setId(1L);
        projeto.setNome("Projeto Teste");
        projeto.setDono(dono);
        membros.forEach(projeto.getMembros()::add);
        return projeto;
    }

    private Tarefa criarTarefa(Long id, StatusTarefa status, PrioridadeTarefa prioridade,
                                Usuario responsavel, Projeto projeto) {
        Tarefa tarefa = new Tarefa();
        tarefa.setId(id);
        tarefa.setTitulo("Tarefa Teste");
        tarefa.setStatus(status);
        tarefa.setPrioridade(prioridade);
        tarefa.setResponsavel(responsavel);
        tarefa.setProjeto(projeto);
        return tarefa;
    }

    private TarefaRequest criarRequest(StatusTarefa status, PrioridadeTarefa prioridade, Long responsavelId) {
        return new TarefaRequest("Titulo Atualizado", null, status, prioridade, null, responsavelId);
    }
}
