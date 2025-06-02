# 📜 Regras do Jogo de Caça-Níquel (Slot Machine MVP)

Este documento descreve todas as regras funcionais do sistema de slot machine, divididas em categorias para facilitar a implementação e manutenção do código.

---

## 🎁 Rodada Bônus

O jogador recebe uma **rodada bônus gratuita** se ocorrer uma **sequência ordenada** **sem curinga**:

### Critérios:
- A sequência deve estar em **ordem crescente ou decrescente**  
  Ex: `A, 2, 3` ou `K, Q, J`
- A sequência pode aparecer em:
  - ✅ Linhas (horizontal)
  - ✅ Colunas (vertical)
  - ✅ Diagonal principal
  - ✅ Diagonal secundária
- ❌ **Curingas (`*`) não são permitidos na sequência**
- A sequência deve ter ao menos o número de símbolos equivalente ao nível:
  - EasySlot: 3
  - MediumSlot: 4
  - HardSlot: 5
- **Paralelas às diagonais** não são avaliadas

---

## 🏆 Vitória da Rodada

O jogador vence a rodada se houver uma sequência com todos os símbolos **iguais** ou **iguais com uso de curingas**.

### Critérios:
- A sequência pode estar em:
  - ✅ Linhas (horizontal)
  - ✅ Colunas (vertical)
  - ✅ Diagonal principal
  - ✅ Diagonal secundária
- Curingas (`*`) são permitidos para **completar a sequência**
- ❌ Não é considerado vitória:
  - Sequência composta **apenas por curingas**
  - **Paralelas às diagonais** não são avaliadas

---

## 🃏 Regras do Curinga (`*`)

- O curinga pode substituir qualquer outro símbolo **somente para vencer a rodada**, **não** para rodada bônus
- Limite máximo de curingas por nível:
  - EasySlot: **1**
  - MediumSlot: **2**
  - HardSlot: **3**
- Cada vetor gerado (coluna) pode conter **no máximo 1 curinga**
- Os curingas devem ser distribuídos **sem estarem na mesma coluna**

---

## 🎰 Geração da Roleta

- A matriz gerada representa a roleta e possui:
  - Número de **colunas** variável:
    - EasySlot: 3
    - MediumSlot: 4
    - HardSlot: 5
  - Número de **linhas** variável para formar uma matrz quadrada. Portanto igaul ao numero de colunas.
- Cada coluna é preenchida com **valores únicos** (sem repetição na coluna)
- São sorteados apenas **13 símbolos**:  
  `A, 2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K`
- Curingas são inseridos segundo regras acima

---

## 💰 Regras de Saldo e Saque

- O jogador inicia com **R$50 de saldo inicial (banca)**
- O jogador **só pode sacar valores** quando o **saldo for ≥ R$100**
- Métodos de **depósito** e **saque** existem, mas **não são usados no MVP**

---

## 💰 Apostas Mínimas e Máximas por Nível

Define os limites de aposta conforme a dificuldade da máquina selecionada:

| Nível       | Aposta Mínima  | Aposta Máxima  |
|-------------|----------------|----------------|
| EasySlot    | R$1            | R$10           |
| MediumSlot  | R$10           | R$30           |
| HardSlot    | R$30           | R$50           |

**Observação:** O valor apostado será debitado do saldo do jogador antes de cada rodada.

---

## Login

- Senha do jogador é armazenada com **hash Bcrypt**
- Cadastro bloqueia menores de idade
- Cadastro não permite **cadastrar email ja existente**
- Apenas jogadores logados podem jogar


## Dependencias

- JUnit
- Bcryp