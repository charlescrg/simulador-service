-- Criação do banco
CREATE DATABASE hack;
GO

-- Usar o banco
USE hack;
GO

-- Criar tabela
CREATE TABLE clientes (
    id INT PRIMARY KEY IDENTITY(1,1),
    nome NVARCHAR(100),
    email NVARCHAR(100),
    saldo DECIMAL(10,2)
);
GO

-- Inserir dados
INSERT INTO clientes (nome, email, saldo) VALUES
('João Silva', 'joao@exemplo.com', 1500.00),
('Maria Souza', 'maria@exemplo.com', 2300.50),
('Carlos Lima', 'carlos@exemplo.com', 500.75);
GO
