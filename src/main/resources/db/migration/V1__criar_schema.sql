CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE usuario (
    id     bigserial    NOT NULL,
    nome   varchar(120) NOT NULL,
    email  varchar(180) NOT NULL,
    senha  varchar(100) NOT NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uk_usuario_email UNIQUE (email)
);

CREATE TABLE projeto (
    id         bigserial    NOT NULL,
    nome       varchar(150) NOT NULL,
    descricao  varchar(500),
    dono_id    bigint       NOT NULL,
    CONSTRAINT pk_projeto PRIMARY KEY (id),
    CONSTRAINT fk_projeto_dono FOREIGN KEY (dono_id) REFERENCES usuario (id)
);

CREATE TABLE projeto_membro (
    projeto_id  bigint NOT NULL,
    usuario_id  bigint NOT NULL,
    CONSTRAINT pk_projeto_membro PRIMARY KEY (projeto_id, usuario_id),
    CONSTRAINT fk_projeto_membro_projeto FOREIGN KEY (projeto_id) REFERENCES projeto (id) ON DELETE CASCADE,
    CONSTRAINT fk_projeto_membro_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
);

CREATE TABLE tarefa (
    id                bigserial    NOT NULL,
    titulo            varchar(200) NOT NULL,
    descricao         text,
    status            varchar(20)  NOT NULL,
    prioridade        varchar(20)  NOT NULL,
    projeto_id        bigint       NOT NULL,
    responsavel_id    bigint,
    prazo             timestamp,
    data_criacao      timestamp    NOT NULL,
    data_atualizacao  timestamp,
    version           bigint       NOT NULL DEFAULT 0,
    CONSTRAINT pk_tarefa PRIMARY KEY (id),
    CONSTRAINT fk_tarefa_projeto            FOREIGN KEY (projeto_id) REFERENCES projeto (id) ON DELETE CASCADE,
    CONSTRAINT fk_tarefa_responsavel        FOREIGN KEY (responsavel_id) REFERENCES usuario (id),
    CONSTRAINT ck_tarefa_status             CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE')),
    CONSTRAINT ck_tarefa_prioridade         CHECK (prioridade IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
);

CREATE INDEX ix_tarefa_projeto      ON tarefa (projeto_id);
CREATE INDEX ix_tarefa_status       ON tarefa (status);
CREATE INDEX ix_tarefa_prazo        ON tarefa (prazo);
CREATE INDEX ix_tarefa_resp_status  ON tarefa (responsavel_id, status);

CREATE INDEX ix_tarefa_titulo_trgm    ON tarefa USING gin (titulo gin_trgm_ops);
CREATE INDEX ix_tarefa_descricao_trgm ON tarefa USING gin (descricao gin_trgm_ops);