package com.henrique.gerenciador_de_tarefas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.henrique.gerenciador_de_tarefas.dto.LoginRequest;
import com.henrique.gerenciador_de_tarefas.dto.ProjetoRequest;
import com.henrique.gerenciador_de_tarefas.dto.ProjetoResponse;
import com.henrique.gerenciador_de_tarefas.dto.RegistrarRequest;
import com.henrique.gerenciador_de_tarefas.dto.TarefaRequest;
import com.henrique.gerenciador_de_tarefas.dto.TarefaResponse;
import com.henrique.gerenciador_de_tarefas.dto.TokenResponse;
import com.henrique.gerenciador_de_tarefas.enums.PrioridadeTarefa;
import com.henrique.gerenciador_de_tarefas.enums.StatusTarefa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FluxoCriticoTarefasIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRegistrarLogarCriarProjetoCriarTarefaEBloquearTransicaoDeDoneParaTodo() throws Exception {
        RegistrarRequest registrarRequest = new RegistrarRequest(
                "Usuario Fluxo", "usuario.fluxo@teste.com", "senha12345");

        mockMvc.perform(post("/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrarRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest("usuario.fluxo@teste.com", "senha12345");
        String loginResponseJson = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readValue(loginResponseJson, TokenResponse.class).token();

        ProjetoRequest projetoRequest = new ProjetoRequest("Projeto Fluxo", "Projeto do teste de integração");
        String projetoResponseJson = mockMvc.perform(post("/projetos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projetoRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long projetoId = objectMapper.readValue(projetoResponseJson, ProjetoResponse.class).id();

        TarefaRequest criarTarefaRequest = new TarefaRequest(
                "Tarefa Fluxo", "Descrição da tarefa", StatusTarefa.DONE, PrioridadeTarefa.MEDIUM, null, null);
        String tarefaResponseJson = mockMvc.perform(post("/projetos/{projetoId}/tarefas", projetoId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(criarTarefaRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long tarefaId = objectMapper.readValue(tarefaResponseJson, TarefaResponse.class).id();

        TarefaRequest voltarParaTodoRequest = new TarefaRequest(
                "Tarefa Fluxo", "Descrição da tarefa", StatusTarefa.TODO, PrioridadeTarefa.MEDIUM, null, null);
        mockMvc.perform(put("/tarefas/{id}", tarefaId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voltarParaTodoRequest)))
                .andExpect(status().isConflict());
    }
}
