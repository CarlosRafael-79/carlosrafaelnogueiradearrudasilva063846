CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE genero_musical (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    slug VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE regional (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    id_externo INTEGER NOT NULL,
    nome VARCHAR(200) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_sincronizacao TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE TABLE artista (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    pais_origem VARCHAR(3),
    ano_formacao INTEGER,
    descricao TEXT,
    data_criacao TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    data_atualizacao TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL
);

CREATE TABLE album (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    ano_lancamento INTEGER,
    duracao_segundos INTEGER,
    data_criacao TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    data_atualizacao TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL
);

CREATE TABLE imagem_album (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    album_id UUID NOT NULL,
    url VARCHAR(500) NOT NULL,
    nome_arquivo VARCHAR(255),
    data_criacao TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    
    CONSTRAINT fk_ia_album FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE
);

CREATE TABLE album_artista (
    album_id UUID NOT NULL,
    artista_id UUID NOT NULL,
    PRIMARY KEY (album_id, artista_id),
    CONSTRAINT fk_aa_album FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE,
    CONSTRAINT fk_aa_artista FOREIGN KEY (artista_id) REFERENCES artista(id) ON DELETE CASCADE
);

CREATE TABLE album_genero (
    album_id UUID NOT NULL,
    genero_id UUID NOT NULL,
    PRIMARY KEY (album_id, genero_id),
    CONSTRAINT fk_ag_album FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE,
    CONSTRAINT fk_ag_genero FOREIGN KEY (genero_id) REFERENCES genero_musical(id) ON DELETE RESTRICT
);

CREATE INDEX idx_artista_nome ON artista(nome);
CREATE INDEX idx_album_titulo ON album(titulo);
CREATE INDEX idx_regional_externo ON regional(id_externo);