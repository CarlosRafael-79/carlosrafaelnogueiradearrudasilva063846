CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    perfil VARCHAR(20) NOT NULL
);

INSERT INTO usuarios (id, email, senha, perfil)
VALUES (
    gen_random_uuid(),
    'admin@artistmanager.com',
    '$2a$10$SQiUKghCCS6lIBjMnqMLq.7e55unBZTjv71WTgq3mGATYueMjN68u',
    'ADMIN'
) ON CONFLICT (email) DO NOTHING;

INSERT INTO usuarios (id, email, senha, perfil)
VALUES (
    gen_random_uuid(),
    'user@artistmanager.com',
    '$2a$10$SQiUKghCCS6lIBjMnqMLq.7e55unBZTjv71WTgq3mGATYueMjN68u',
    'USER'
) ON CONFLICT (email) DO NOTHING;