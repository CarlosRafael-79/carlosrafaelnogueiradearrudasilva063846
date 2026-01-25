# Desafio Técnico - Processo Seletivo SEPLAG 2026

## Dados de Inscrição
* **Nome Completo:** Carlos Rafael Nogueira de Arruda Silva
* **Vaga:** Analista de Tecnologia da Informação - Perfil Engenheiro da Computação (Sênior)
* **Projeto Escolhido:** Implementação  Back End Java Sênior

# Artist Manager API


## Decisões Arquiteturais e Tecnologias

Abaixo estão as justificativas para as principais escolhas técnicas adotadas neste projeto:

* **Docker & Docker Compose:** A aplicação foi conteinerizada utilizando *Multistage Building*. Isso separa a fase de compilação (com Maven e JDK) da fase de execução (apenas JRE), resultando em uma imagem final leve (~180MB) e segura (sem código fonte em produção).
* **MinIO (Object Storage):** Utilizado para cumprir o requisito de armazenamento S3. Foi adicionado um container efêmero (`createbuckets`) que configura automaticamente o bucket `capas-albuns` na inicialização, eliminando configuração manual.
* **PostgreSQL:** Banco de dados relacional robusto, configurado com *Healthchecks* para garantir que a API só inicie quando o banco estiver pronto para receber conexões.
* **Portas Customizadas:** A porta do banco foi mapeada externamente para `5433` para evitar conflitos com instalações locais de PostgreSQL na máquina do avaliador.

## Como Executar o Projeto

### Pré-requisitos
* Docker e Docker Compose instalados.
* Portas `8080` (API), `5433` (Banco) e `9000/9001` (MinIO) livres.

### 1. Primeira Execução (Build e Start)
Para baixar as dependências, compilar o projeto, criar as imagens Docker e subir o ambiente completo (Banco, MinIO e API), execute na raiz do projeto:
```bash
docker-compose up -d --build
```


