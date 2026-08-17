# Gerenciador de Tarefas - Introdução

API REST para gerenciamento de tarefas em equipes. Usuários
criam projetos, convidam membros e gerenciam tarefas dentro desses projetos,
com controle de acesso por projeto.


## Ferramentas Utilizadas

- Java 21
- Maven
- Spring Boot 3.5.4
- Spring Security + JWT (jjwt 0.12.6)
- Spring Data JPA / Hibernate 6.6
- PostgreSQL 17
- Flyway
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito, MockMvc
- React


## Pré-requisitos

- Java 21
- PostgreSQL 17 na porta 5432
- Node.js 18+


## Como rodar

   ### 1. Crie o banco de dados
   
   Com o seguinte comando no cmd da pasta bin do postgres 17:
   
   ```bash
    psql -U postgres
    CREATE DATABASE gerenciador_tarefas;
    \q
   ```
   
   Obs: Não é necessário criar nenhuma tabela. O Flyway aplica o sql já commitado
   automaticamente ao rodar o spring boot.

   ### 2. Suba a aplicação
   
   ```bash
    # Windows
    mvnw.cmd spring-boot:run
    
    # Linux / macOS
    ./mvnw spring-boot:run
   ```
   
   Obs: A API sobe em `http://localhost:8080` e a documentação fica em
   `http://localhost:8080/swagger-ui/index.html`.

   ### 3. Para utilizar os testes unitários rode:
   
   ```bash
    mvnw.cmd clean test
   ```

   ### 4. Para subir o frontend rode no cmd do projeto:
   
   ```bash
    cd frontend
    npm install
    npm run dev
   ```
   
   Depois acesse `http://localhost:5173`


## Acesso ao banco

   As senhas vem diretamente das variáveis de ambiente. Nenhuma senha está commitada.
   Se a senha do seu usuário postgres não for "postgres", defina a
   variável antes de rodar:

   ```bash
    # Windows
    set DB_PASSWORD=suasenha

    # Linux/macOS
    export DB_PASSWORD=suasenha
   ```


## Regras de negócio

   ### Transição de status
   Uma tarefa como `Concluído` não pode voltar direto para `A Fazer`. A única volta permitida é
   para `Em Andamento` e depois `A Fazer`.
   
   ### Conclusão de tarefa crítica
   Tarefas com prioridade `Crítica` só podem ser concluídas pelo dono
   do projeto.

   ### Limite de trabalho em andamento
   Um responsável não pode ter mais de cinco tarefas em andamento, considerando todos os projetos.


## Segurança

   ### Senhas com BCrypt
   A senha nunca é gravada em texto e nunca aparece em nenhum DTO de resposta.

   ### Acesso
   O usuário só pode acessar projetos e tarefas a quais ele
   pertence.


## Decisões técnicas

   ### Idioma do código
   Classes, campos e métodos ficaram em português para melhor entendimento das suas funções, deixando
   um nome autoexplicativo e direto. Evitando assim o uso excessivo do javadoc.
   Só usei inglês onde o framework obriga.

   ### Flyway
   Utilizei o Flyway para melhor controle dos scripts usados. Pois ele controla a versão de cada
   script, impedindo que ele seja rodado mais de uma vez. E te dá uma organização maior em relação
   a ordem de scripts criados e rodados. E executa os scripts de forma automática toda vez que o spring
   boot é executado.

   ### PostgreSQL em vez de H2
   O caminho fácil seria usar o H2 e deixar o Hibernate criar as tabelas sozinho.
   Mas preferi escolher o Postgres porque é o banco que eu uso e tenho mais segurança.

   ### Índice GIN com `pg_trgm` na busca textual
   Eu poderia somente usar o `LIKE` e pronto. O problema é que com `%` no começo o índice
   comum não serviria e o banco iria varrer a tabela inteira.
   Então criei a extensão `pg_trgm` e índices GIN no título e na descrição, que é o
   que torna a busca por pedaços de texto indexável.


## Estratégia de testes

   Testei somente o que quebraria em produção:

   ### Testes unitários — `TarefaServiceTest`
   Testei as regras de negócio das tarefas, cada uma com dois casos: o que
   deve funcionar e o que deve ser bloqueado. Só verificar que a exceção é
   lançada não bastaria, um código que recusasse tudo passaria no teste. Por isso
   cada regra tem o par.
   
   Com quatro tarefas em andamento passa, com cinco bloqueia. E tirar uma tarefa de
   "Em Andamento" nunca deve esbarrar no limite, mesmo com o responsável no
   máximo. Se a validação estiver no lugar errado, esse teste pega.
   
   Não testei cadastro nem CRUD simples. São operações que só salvam e leem.
   Preferi cobrir o que quebra em produção.

   ### Teste de integração — `FluxoCriticoTarefasIntegrationTest`
   Registra um usuário, faz login, pega o token, cria um projeto, cria uma tarefa
   já em DONE e tenta voltar pra TODO.
   O token que sai do login é aceito na requisição seguinte, o filtro JWT popula o contexto,
   o guard consegue ler o usuário de lá, e a exceção que o service lança vira um 409.


## O que eu faria com mais tempo

   - Registro de quem alterou o status de cada tarefa. Hoje o dono e os membros
     podem mexer na mesma tarefa e não fica registrado quem foi.
   
   - Teria utilizado o Docker Compose para subir aplicação e banco juntos.
     E o Testcontainers nos testes de integração, pra eliminar a dependência de ter Postgres instalado.
   
   - Uma tela de login mais elaborada com vinculo direto com o gmail e validação real de email válido.
     Eu validei só o formato do email, não se ele existe de verdade.