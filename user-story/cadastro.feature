Funcionalidade: Cadastro de Jogador
  Como visitante
  Quero me cadastrar com meus dados
  Para poder acessar e jogar

  Cenário: Cadastro com dados válidos
    Dado que informo e-mail, senha, data de nascimento e apelido válidos
    Quando confirmo o cadastro
    Então meu usuário deve ser criado com saldo inicial de R$50,00
    E minha senha deve ser armazenada com hash SHA-256
    E o sistema deve verificar se tenho 18 anos ou mais