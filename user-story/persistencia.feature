Funcionalidade: Persistência em CSV
  Como sistema
  Quero salvar e carregar os dados dos jogadores automaticamente
  Para manter o progresso

  Cenário: Salvamento e carregamento de dados
    Dado que ações como cadastro, depósito, saque ou jogo acontecem
    Quando o sistema detecta mudanças
    Então os dados devem ser salvos em jogadores.csv
    E carregados automaticamente na inicialização