Funcionalidade: Validação de E-mail Único
  Como sistema
  Quero impedir e-mails duplicados no cadastro
  Para garantir unicidade de conta

  Cenário: Tentativa de cadastro com e-mail duplicado
    Dado que tento cadastrar um novo jogador
    Quando uso um e-mail já existente
    Então o sistema deve rejeitar o cadastro com uma mensagem de erro