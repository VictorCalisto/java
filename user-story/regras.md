# Regras de Negócio do Jogo Caça-Níqueis (Java)

Este documento descreve as regras de negócio que governam o funcionamento do jogo de caça-níqueis, incluindo o sistema de autenticação de jogadores, a mecânica de jogo e a gestão de saldos.

---

## 1. Sistema de Autenticação e Jogador

### Cadastro de Jogadores
* **Dados Necessários**: Para o cadastro, são obrigatórios: **e-mail**, **senha**, **data de nascimento** e **apelido**.
* **E-mail Único**: Cada e-mail cadastrado deve ser único no sistema. Não é permitido o cadastro de múltiplos jogadores com o mesmo e-mail.
* **Armazenamento de Senha**: As senhas são armazenadas como um **hash SHA-256** para segurança, não em texto puro.
* **Saldo Inicial**: Todo novo jogador recebe um **saldo inicial de R$ 50,00**.
* **Verificação de Idade**: No momento do cadastro, é verificada a maioridade do jogador (18 anos ou mais) com base na data de nascimento.

### Autenticação de Jogadores
* **Login**: O login é feito por e-mail e senha.
* **Tentativas de Senha**: Após **5 tentativas incorretas** de senha, o sistema impõe um atraso de **30 segundos** antes de permitir novas tentativas, com um tempo de espera progressivo a cada tentativa adicional.
* **Status de Jogo**: Apenas jogadores maiores de idade (com `statusMaiorDeIdade` verdadeiro) podem realizar apostas ou jogar.

### Gestão de Saldo
* **Depósito**: Jogadores podem depositar qualquer valor **positivo** em suas contas.
* **Saque**:
    * O jogador só pode sacar se tiver um **saldo total mínimo de R$ 100,00** em sua conta.
    * O valor do saque solicitado não pode exceder o saldo disponível.

---

## 2. Mecânica do Jogo Caça-Níqueis

### Símbolos e Curinga
* **Símbolos Padrão**: Os símbolos são definidos como "A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K".
* **Símbolo Curinga**: O símbolo `*` (asterisco) atua como um curinga.

### Níveis de Dificuldade (Slots)
O jogo possui três níveis de dificuldade, cada um com suas próprias características de tabuleiro, limite de curingas, aposta e multiplicador de prêmio:

| Característica            | Slot Fácil | Slot Médio | Slot Difícil |
| :------------------------ | :--------- | :--------- | :----------- |
| **Tamanho do Tabuleiro** | 3x3        | 4x4        | 5x5          |
| **Limite de Curingas** | 1          | 2          | 3            |
| **Aposta Mínima** | R$ 1,00    | R$ 10,00   | R$ 30,00     |
| **Aposta Máxima** | R$ 10,00   | R$ 30,00   | R$ 50,00     |
| **Multiplicador de Prêmio** | 10x        | 50x        | 100x         |

### Geração do Tabuleiro
* O tabuleiro é preenchido aleatoriamente com os símbolos padrão.
* Curingas são inseridos em posições aleatórias, respeitando o limite de curingas por slot. Preferencialmente, cada curinga é colocado em uma coluna diferente para maior dispersão, se o tamanho do tabuleiro permitir.

### Condições de Vitória
A verificação de vitória e bônus ocorre em todas as **linhas horizontais**, **linhas verticais (colunas)**, **diagonal principal** e **diagonal secundária**.

* **Vitória Principal**:
    * Ocorre quando todos os símbolos em uma linha (horizontal, vertical ou diagonal) são **iguais**, independentemente da presença de curingas.
    * Curingas (`*`) contam como o símbolo correspondente para formar a combinação vencedora.
    * **Recompensa**: O jogador recebe o **valor da aposta multiplicado** pelo `multiplicadorPremio` do slot.

* **Bônus**:
    * Ocorre quando os símbolos em uma linha (horizontal, vertical ou diagonal) formam uma **sequência numérica ou alfabética estrita** (crescente ou decrescente).
    * **Não pode haver curingas (`*`)** na linha para que ela seja considerada para bônus.
    * **Exemplos de Sequências**: "2", "3", "4" (crescente); "Q", "J", "T" (decrescente).
    * **Recompensa**: O jogador recebe o **valor da aposta**.

---

## 3. Persistência de Dados

* **Arquivo de Dados**: Os dados dos jogadores são salvos e carregados de um arquivo CSV chamado `jogadores.csv`.
* **Formato CSV**: O arquivo CSV armazena informações como `email`, `senha_hash`, `saldo`, `data_de_nascimento` e `apelido`.
* **Salvamento**: Os dados são salvos no CSV:
    * Ao sair do jogo.
    * Após cada ação importante que altere o saldo do jogador (aposta, depósito, saque, vitória, bônus).
* **Carregamento**: Os dados são carregados do CSV na inicialização do jogo. Se o arquivo não existir, o sistema inicia sem jogadores pré-existentes.
