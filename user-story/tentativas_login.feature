Funcionalidade: Tentativas Inválidas de Senha
  Como sistema
  Quero impor atrasos após múltiplas tentativas de login com senha errada
  Para proteger contra força bruta

  Cenário: Múltiplas tentativas de login inválidas
    Dado que erro a senha 5 vezes consecutivas
    Quando tento novamente
    Então o sistema deve impor um atraso de 30 segundos
    E aumentar esse atraso progressivamente a cada nova tentativa falha