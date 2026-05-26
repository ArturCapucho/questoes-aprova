CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    categoria_pai_id BIGINT REFERENCES categorias(id),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_categorias_slug ON categorias(slug);
CREATE INDEX idx_categorias_pai ON categorias(categoria_pai_id);

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4() UNIQUE,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'ALUNO' CHECK (role IN ('ALUNO','PROFESSOR','ADMIN')),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_uuid ON usuarios(uuid);

CREATE TABLE questoes (
    id BIGSERIAL,
    uuid UUID NOT NULL DEFAULT uuid_generate_v4(),
    enunciado TEXT NOT NULL,
    banca VARCHAR(100),
    orgao VARCHAR(150),
    ano INT NOT NULL,
    categoria_id BIGINT NOT NULL REFERENCES categorias(id),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id, ano)
) PARTITION BY RANGE (ano);

CREATE TABLE questoes_2020 PARTITION OF questoes FOR VALUES FROM (2020) TO (2021);
CREATE TABLE questoes_2021 PARTITION OF questoes FOR VALUES FROM (2021) TO (2022);
CREATE TABLE questoes_2022 PARTITION OF questoes FOR VALUES FROM (2022) TO (2023);
CREATE TABLE questoes_2023 PARTITION OF questoes FOR VALUES FROM (2023) TO (2024);
CREATE TABLE questoes_2024 PARTITION OF questoes FOR VALUES FROM (2024) TO (2025);
CREATE TABLE questoes_2025 PARTITION OF questoes FOR VALUES FROM (2025) TO (2026);
CREATE TABLE questoes_2026 PARTITION OF questoes FOR VALUES FROM (2026) TO (2027);

CREATE INDEX idx_questoes_uuid ON questoes(uuid);
CREATE INDEX idx_questoes_categoria ON questoes(categoria_id);
CREATE INDEX idx_questoes_banca_ano ON questoes(banca, ano);
CREATE INDEX idx_questoes_fts ON questoes USING GIN (to_tsvector('portuguese', enunciado));

CREATE TABLE alternativas (
    id BIGSERIAL PRIMARY KEY,
    questao_id BIGINT NOT NULL,
    questao_ano INT NOT NULL,
    letra CHAR(1) NOT NULL CHECK (letra IN ('A','B','C','D','E')),
    texto TEXT NOT NULL,
    correta BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (questao_id, questao_ano) REFERENCES questoes(id, ano) ON DELETE CASCADE,
    UNIQUE (questao_id, questao_ano, letra)
);

CREATE INDEX idx_alternativas_questao ON alternativas(questao_id, questao_ano);
CREATE UNIQUE INDEX idx_alternativa_correta ON alternativas(questao_id, questao_ano) WHERE correta = TRUE;

CREATE TABLE tentativas (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    questao_id BIGINT NOT NULL,
    questao_ano INT NOT NULL,
    alternativa_escolhida_id BIGINT NOT NULL REFERENCES alternativas(id),
    correta BOOLEAN NOT NULL,
    explicacao_ia TEXT,
    respondido_em TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    FOREIGN KEY (questao_id, questao_ano) REFERENCES questoes(id, ano)
);

CREATE INDEX idx_tentativas_usuario ON tentativas(usuario_id);
CREATE INDEX idx_tentativas_questao ON tentativas(questao_id, questao_ano);
CREATE INDEX idx_tentativas_usuario_data ON tentativas(usuario_id, respondido_em DESC);

CREATE MATERIALIZED VIEW mv_stats_usuario_categoria AS
SELECT
    t.usuario_id,
    q.categoria_id,
    c.nome AS categoria_nome,
    COUNT(*) AS total_tentativas,
    SUM(CASE WHEN t.correta THEN 1 ELSE 0 END) AS total_acertos,
    ROUND(100.0 * SUM(CASE WHEN t.correta THEN 1 ELSE 0 END) / COUNT(*), 2) AS percentual_acerto,
    MAX(t.respondido_em) AS ultima_tentativa
FROM tentativas t
JOIN questoes q ON q.id = t.questao_id AND q.ano = t.questao_ano
JOIN categorias c ON c.id = q.categoria_id
GROUP BY t.usuario_id, q.categoria_id, c.nome
WITH NO DATA;

CREATE UNIQUE INDEX idx_mv_stats_usuario_cat ON mv_stats_usuario_categoria(usuario_id, categoria_id);

CREATE OR REPLACE FUNCTION refresh_stats()
RETURNS void LANGUAGE plpgsql AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_stats_usuario_categoria;
END;
$$;
