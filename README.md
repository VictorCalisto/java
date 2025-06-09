# Caça-Níqueil

Este é um projeto de jogo de caça-níqueis desenvolvido com propósitos educacionais, focado em demonstrar conceitos de programação orientada a objeto (POO).
## O que é o Jogo?

É uma simulação simples de um jogo de caça-níqueis (slot machine) que permite aos usuários:

* **Cadastrar-se e Fazer Login**: Gerenciamento básico de contas de jogador.
* **Apostar**: Os jogadores usam um saldo virtual para fazer suas apostas.
* **Jogar em Diferentes Dificuldades**: Escolha entre níveis Fácil, Médio e Difícil, que alteram o tamanho do tabuleiro e a quantidade de símbolos curinga.
* **Ganhos e Bônus**: O jogo recompensa vitórias por combinações de símbolos e e oferece bônus por sequências estritas (crescentes ou decrescentes) sem curingas.
* **Gerenciar Saldo**: Opções para depositar e sacar dinheiro virtual.
* **Persistência de Dados**: Os dados dos jogadores são salvos em um arquivo CSV para manter o progresso entre as sessões.

## Projeto Educacional

Este projeto serve como uma ferramenta de aprendizado para:

* **Programação Orientada a Objetos (POO)**: Demonstração de classes, objetos, herança e encapsulamento.
* **Lógica de Negócio**: Como traduzir regras de um jogo para código.
* **Refatoração**: O código foi aprimorado e traduzido para diferentes idiomas como um exercício de manutenção e adaptação de código.
* **Colaboração com IAs**: Este projeto foi inicialmente desenvolvido em Ruby e, posteriormente, convertido para Java com a assistência da IA Gemini 2.5, destacando o potencial das ferramentas de IA no desenvolvimento de software.

## Regras de Negócio (User Stories)

As regras de negócio detalhadas e os critérios de aceitação do jogo estão documentados na pasta `user-story`. Lá, você encontrará a descrição das funcionalidades do sistema em um formato de Comportamento Orientado ao Desenvolvimento (BDD - Behaviour-Driven Development).

## Tecnologia

* **Linguagem Original**: Ruby
* **Conversão**: Java (realizada pela IA Gemini 2.5)

## Código Aberto

Este projeto é de código aberto e está disponível para estudo, modificação e contribuição. Sinta-se à vontade para explorar o código, adaptá-lo às suas necessidades ou sugerir melhorias.
## Pré-requisitos

- [Git](https://git-scm.com/)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## Clonando o projeto

```bash
git clone https://github.com/VictorCalisto/slots.git
cd slots
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
---

## Estrutura do Projeto

```plaintext
.
├── app
│   ├── CacaNiquel.class
│   ├── ExibidorDeMensagens.class
│   ├── Jogador.class
│   ├── jogadores.csv
│   ├── Principal.class
│   ├── Principal.java
│   ├── SistemaAutenticacao.class
│   ├── SlotDificil.class
│   ├── SlotFacil.class
│   └── SlotMedio.class
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
    ├── usecase_diagrama.png
    └── vitoria.feature

2 directories, 28 files
