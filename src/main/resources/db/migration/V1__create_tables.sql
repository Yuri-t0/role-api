CREATE TABLE IF NOT EXISTS usuario (
    id         BIGSERIAL PRIMARY KEY,
    nome       VARCHAR(150) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    telefone   VARCHAR(20)  NOT NULL,
    senha      VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS local_evento (
    id        BIGSERIAL PRIMARY KEY,
    nome      VARCHAR(150) NOT NULL,
    endereco  VARCHAR(255) NOT NULL,
    latitude  DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS evento (
    id                  BIGSERIAL PRIMARY KEY,
    titulo              VARCHAR(200) NOT NULL,
    descricao           VARCHAR(500) NOT NULL,
    inicio              TIMESTAMP    NOT NULL,
    fim                 TIMESTAMP    NOT NULL,
    vagas_totais        INT          NOT NULL,
    vagas_disponiveis   INT          NOT NULL,
    organizador_id      BIGINT       NOT NULL,
    local_id            BIGINT       NOT NULL,
    FOREIGN KEY (organizador_id) REFERENCES usuario(id),
    FOREIGN KEY (local_id)       REFERENCES local_evento(id)
);

CREATE TABLE IF NOT EXISTS presenca (
    id            BIGSERIAL PRIMARY KEY,
    usuario_id    BIGINT    NOT NULL,
    evento_id     BIGINT    NOT NULL,
    confirmado_em TIMESTAMP NOT NULL,
    UNIQUE (usuario_id, evento_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    FOREIGN KEY (evento_id)  REFERENCES evento(id)
);

CREATE TABLE IF NOT EXISTS notificacao_processamento (
    id              BIGSERIAL PRIMARY KEY,
    evento_id       BIGINT       NOT NULL,
    usuario_email   VARCHAR(150) NOT NULL,
    evento_titulo   VARCHAR(200) NOT NULL,
    local_nome      VARCHAR(150) NOT NULL,
    vagas_restantes INT          NOT NULL,
    processado_em   TIMESTAMP    NOT NULL
);
