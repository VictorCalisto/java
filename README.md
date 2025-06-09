# Projeto Java com Docker

Este projeto executa uma aplicação Java simples em ambiente Docker, com acesso interativo ao terminal do container para compilar e rodar manualmente os arquivos `.java`.

## Pré-requisitos

- [Git](https://git-scm.com/)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## Clonando o projeto

```bash
git clone https://seu-repo.git
cd seu-repo
```

## Subindo o container

```bash
docker compose up -d
```

## Acessando o terminal do container
```bash
docker exec -it java bash
```
## Compilando o programa Java
```bash
javac Principal.java
```
## Executando o programa Java
```bash
java Principal
```
## Parando o container
```bash
docker compose down
```
## Estrutura do projeto

.
├── app
│ ├── CacaNiquel.class
│ ├── ExibidorDeMensagens.class
│ ├── Jogador.class
│ ├── jogadores.csv
│ ├── Principal.class
│ ├── Principal.java
│ ├── SistemaAutenticacao.class
│ ├── SlotDificil.class
│ ├── SlotFacil.class
│ └── SlotMedio.class
├── docker-compose.yml
├── Dockerfile
├── jogo_original_ruby.rb
├── README.md
└── user-story
├── bonus.feature
├── cadastro.feature
├── deposito.feature
├── email_unico.feature
├── geracao_tabuleiro.feature
├── login.feature
├── persistencia.feature
├── regras.md
├── restricao_idade.feature
├── saque.feature
├── slot_aposta.feature
├── tentativas_login.feature
└── vitoria.feature

Total: **2 diretórios, 27 arquivos**
