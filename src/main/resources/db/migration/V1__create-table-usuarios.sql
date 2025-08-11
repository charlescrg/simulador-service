-- Criação da tabela
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    login VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    nome_completo VARCHAR(255),
    email VARCHAR(255),
    telefone VARCHAR(50),
    data_nascimento VARCHAR(50),
    perfil VARCHAR(50),
    renda_mensal NUMERIC(12, 2),
    idioma VARCHAR(20),
    tema VARCHAR(20),
    notificacoes_ativas BOOLEAN DEFAULT false,
    ultimo_login VARCHAR(50),
    ativo BOOLEAN DEFAULT true,
    role VARCHAR(50)
);

-- Inserção de usuário de teste
INSERT INTO usuarios (login, senha, ativo, role)
VALUES ('teste', '1234', true, 'USER');




