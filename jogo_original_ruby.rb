require 'digest'
require 'date'
require 'csv'

# Classe que representa um jogador no sistema do caça-níqueis.
class Jogador
  attr_reader :email, :saldo, :data_de_nascimento, :apelido, :status

  def initialize(email, senha, data_de_nascimento, apelido)
    @senha_hash = Digest::SHA256.hexdigest(senha) # Hash da senha para segurança.
    @saldo = 50.0 # Saldo inicial do jogador.
    @email = email
    @data_de_nascimento = data_de_nascimento
    @apelido = apelido
    @status = maior_de_idade? # Define o status do jogador com base na idade.
  end

  # Autentica o jogador verificando a senha.
  #
  # @param senha [String] A senha digitada pelo usuário.
  # @return [Boolean] Verdadeiro se a senha estiver correta, falso caso contrário.
  def autenticar(senha)
    Digest::SHA256.hexdigest(senha) == @senha_hash
  end

  # Verifica se o jogador é maior de idade (18 anos ou mais).
  #
  # @return [Boolean] Verdadeiro se o jogador for maior de idade, falso caso contrário.
  def maior_de_idade?
    hoje = Date.today
    idade = hoje.year - @data_de_nascimento.year
    # Ajusta a idade se o aniversário ainda não ocorreu no ano atual.
    idade -= 1 if hoje < Date.new(hoje.year, @data_de_nascimento.month, @data_de_nascimento.day)
    idade >= 18
  end

  # Verifica se o jogador pode jogar (é maior de idade).
  #
  # @return [Boolean] Verdadeiro se o jogador puder jogar, falso caso contrário.
  def pode_jogar?
    @status
  end

  # Adiciona um valor ao saldo do jogador.
  #
  # @param valor [Float] O valor a ser depositado.
  def depositar(valor)
    @saldo += valor
  end

  # Retira um valor do saldo do jogador.
  # Requer um saldo mínimo de R$100 para sacar.
  #
  # @param valor [Float] O valor a ser sacado.
  # @raise [RuntimeError] Se o saldo for insuficiente para saque.
  def sacar(valor)
    raise 'Saldo insuficiente para saque. Mínimo de R$100 no saldo.' unless @saldo >= 100 && @saldo >= valor

    @saldo -= valor
  end

  # Realiza uma aposta, diminuindo o saldo do jogador.
  #
  # @param valor [Float] O valor da aposta.
  # @raise [RuntimeError] Se o jogador for inativo ou tiver saldo insuficiente.
  def apostar(valor)
    raise 'Jogador inativo, não pode jogar.' unless pode_jogar?
    raise 'Saldo insuficiente para aposta.' if @saldo < valor

    @saldo -= valor
  end

  # Recompensa o jogador adicionando um valor ao saldo.
  #
  # @param valor [Float] O valor da recompensa.
  def recompensar(valor)
    @saldo += valor
  end
end

# Gerencia o registro e autenticação de usuários.
class SistemaAutenticacao
  attr_reader :usuarios

  def initialize
    @usuarios = {} # Hash para armazenar os jogadores, usando o email como chave.
  end

  # Exibe o menu de login e gerencia o fluxo de autenticação/cadastro.
  #
  # @return [Jogador] O objeto Jogador do jogador logado.
  def menu_login
    tentativas_sem_espera = 5 # Número de tentativas antes de aplicar o atraso.
    segundos_espera = 30 # Tempo de espera entre tentativas em segundos.

    loop do
      puts "--------------------------------------------------"
      puts "Por favor, digite 1 para fazer login ou 2 para se cadastrar."
      puts "--------------------------------------------------"
      escolha = gets.chomp.to_i

      case escolha
      when 1 # Opção de login
        puts "Digite seu email:"
        email = gets.chomp
        jogador = @usuarios[email] # Tenta encontrar o jogador pelo email.

        if jogador.nil?
          puts "Você ainda não tem um cadastro. Por favor, cadastre-se primeiro."
          next # Continua o loop para exibir o menu novamente.
        end

        tentativas = 0
        loop do
          puts "Digite sua senha:"
          senha_digitada = gets.chomp
          if jogador.autenticar(senha_digitada)
            puts "Login bem-sucedido! Bem-vindo(a), #{jogador.apelido}."
            return jogador # Retorna o objeto do jogador logado.
          else
            tentativas += 1
            puts "Senha incorreta. Você tentou #{tentativas} vez(es)."
            if tentativas >= tentativas_sem_espera
              # Calcula o tempo de espera progressivo.
              tempo_dormir = (tentativas - (tentativas_sem_espera - 1)) * segundos_espera
              puts "Muitas tentativas incorretas. Por favor, espere #{tempo_dormir} segundos antes de tentar novamente."
              sleep(tempo_dormir)
            end
          end
        end

      when 2 # Opção de cadastro
        puts "Digite um email válido:"
        email = gets.chomp
        if @usuarios.key?(email)
          puts "Email já cadastrado. Por favor, faça login."
          next # Continua o loop.
        end

        puts "Digite uma senha:"
        senha = gets.chomp
        puts "Digite sua data de nascimento (AAAA-MM-DD):"
        begin
          data_nascimento = Date.parse(gets.chomp)
        rescue ArgumentError
          puts "Formato de data inválido. Por favor, use AAAA-MM-DD."
          next # Continua o loop para pedir a data novamente.
        end

        puts "Digite seu apelido:"
        apelido = gets.chomp

        jogador = cadastrar(email, senha, data_nascimento, apelido)
        puts "Cadastro realizado com sucesso! Você pode fazer login agora."
      else
        puts "Escolha inválida. Por favor, digite 1 ou 2."
      end
    end
  end

  # Cadastra um novo jogador no sistema.
  #
  # @param email [String] O email do jogador.
  # @param senha [String] A senha do jogador.
  # @param data_nasc [Date] A data de nascimento do jogador.
  # @param apelido [String] O apelido do jogador.
  # @return [Jogador] O objeto Jogador recém-criado.
  # @raise [RuntimeError] Se o email já estiver cadastrado.
  def cadastrar(email, senha, data_nasc, apelido)
    raise 'Email já cadastrado.' if @usuarios[email]
    jogador = Jogador.new(email, senha, data_nasc, apelido)
    @usuarios[email] = jogador
    jogador
  end

  # Salva os dados dos jogadores em um arquivo CSV.
  #
  # @param caminho [String] O caminho para o arquivo CSV.
  def salvar_para_csv(caminho)
    CSV.open(caminho, 'w') do |csv|
      # Escreve o cabeçalho do CSV.
      csv << %w[email senha_hash saldo data_de_nascimento apelido]
      @usuarios.each_value do |j|
        # Escreve os dados de cada jogador.
        csv << [j.email, j.instance_variable_get(:@senha_hash),
                j.saldo, j.data_de_nascimento.to_s, j.apelido]
      end
    end
  end

  # Carrega os dados dos jogadores de um arquivo CSV.
  #
  # @param caminho [String] O caminho para o arquivo CSV.
  def carregar_de_csv(caminho)
    return unless File.exist?(caminho) # Não faz nada se o arquivo não existir.

    CSV.foreach(caminho, headers: true) do |linha|
      email = linha['email']
      senha_hash = linha['senha_hash']
      saldo = linha['saldo'].to_f
      data_nascimento = Date.parse(linha['data_de_nascimento'])
      apelido = linha['apelido']

      # Recria o objeto Jogador a partir dos dados do CSV.
      jogador = Jogador.allocate # Aloca memória sem chamar initialize.
      jogador.instance_variable_set(:@senha_hash, senha_hash)
      jogador.instance_variable_set(:@saldo, saldo)
      jogador.instance_variable_set(:@email, email)
      jogador.instance_variable_set(:@data_de_nascimento, data_nascimento)
      jogador.instance_variable_set(:@apelido, apelido)
      jogador.instance_variable_set(:@status, jogador.maior_de_idade?) # Recalcula o status.

      @usuarios[email] = jogador
    end
  rescue StandardError => e
    puts "Erro ao carregar jogadores do CSV: #{e.message}"
  end
end

# Classe base para os diferentes tipos de caça-níqueis.
class CacaNiquel
  SIMBOLOS = %w[A 2 3 4 5 6 7 8 9 T J Q K].freeze # Símbolos padrão.
  CORINGA = '*' # Símbolo curinga.

  def initialize(tamanho, limite_coringa, aposta_min, aposta_max, multiplicador_premio)
    @tamanho = tamanho # Tamanho do tabuleiro (e.g., 3x3, 4x4).
    @limite_coringa = limite_coringa # Limite de curingas por coluna.
    @aposta_min = aposta_min # Aposta mínima permitida.
    @aposta_max = aposta_max # Aposta máxima permitida.
    @multiplicador_premio = multiplicador_premio # Multiplicador de prêmio para vitória.
  end

  # Executa uma rodada do jogo de caça-níqueis.
  #
  # @param jogador [Jogador] O jogador que está apostando.
  # @param valor_aposta [Float] O valor da aposta.
  # @return [Array] Um array contendo o tabuleiro, status de vitória e status de bônus.
  # @raise [RuntimeError] Se a aposta estiver fora dos limites.
  def jogar(jogador, valor_aposta)
    raise "Valor da aposta fora dos limites (#{@aposta_min}-#{@aposta_max})." if valor_aposta < @aposta_min || valor_aposta > @aposta_max

    jogador.apostar(valor_aposta) # Decrementa o saldo do jogador.
    tabuleiro = gerar_tabuleiro # Gera o tabuleiro.

    vitoria = verificar_vitoria?(tabuleiro) # Verifica se houve vitória.
    bonus = verificar_bonus?(tabuleiro) # Verifica se houve bônus.

    if vitoria
      jogador.recompensar(valor_aposta * @multiplicador_premio) # Recompensa por vitória.
      puts "Parabéns! Você ganhou R$#{'%.2f' % (valor_aposta * @multiplicador_premio)}!"
    elsif bonus
      jogador.recompensar(valor_aposta) # Recompensa por bônus (metade da vitória).
      puts "Você ganhou um bônus! Você ganhou R$#{'%.2f' % (valor_aposta)}!"
    else
      puts "Nenhuma vitória desta vez."
    end

    [tabuleiro, vitoria, bonus]
  end

  # Solicita e valida o valor da aposta do jogador.
  #
  # @return [Integer] O valor da aposta válido.
  def solicitar_valor_aposta
    loop do
      puts "Digite o valor da aposta (entre #{@aposta_min} e #{@aposta_max}):"
      valor = gets.strip.to_i
      return valor if valor.between?(@aposta_min, @aposta_max)
      puts "Valor inválido. Digite um número inteiro entre #{@aposta_min} e #{@aposta_max}."
    end
  end

  private

  # Gera o tabuleiro do caça-níqueis.
  #
  # @return [Array<Array<String>>] O tabuleiro gerado.
  def gerar_tabuleiro
    tabuleiro = Array.new(@tamanho) { Array.new(@tamanho) { SIMBOLOS.sample } } # Inicializa com símbolos aleatórios.

    # Lista de todas as posições possíveis no tabuleiro (linha, coluna).
    todas_posicoes = (0...@tamanho).to_a.product((0...@tamanho).to_a)

    # Seleciona posições aleatórias únicas para os curingas.
    # Garante que cada curinga esteja em uma coluna diferente, se possível.
    posicoes_coringas = []
    colunas_com_coringa = Set.new # Usar Set para busca rápida.

    # Tenta adicionar curingas em colunas distintas primeiro.
    @limite_coringa.times do
      break if colunas_com_coringa.size == @tamanho # Já cobriu todas as colunas possíveis.

      disponiveis_para_coringa = todas_posicoes.reject { |r, c| colunas_com_coringa.include?(c) || posicoes_coringas.include?([r, c]) }

      if disponiveis_para_coringa.empty?
        # Se não há mais colunas disponíveis para garantir unicidade,
        # seleciona de todas as posições restantes que ainda não têm curinga.
        disponiveis_para_coringa = todas_posicoes.reject { |pos| posicoes_coringas.include?(pos) }
        break if disponiveis_para_coringa.empty?
      end

      pos_selecionada = disponiveis_para_coringa.sample
      posicoes_coringas << pos_selecionada
      colunas_com_coringa.add(pos_selecionada[1])
    end

    # Se ainda faltam curingas para o total desejado, adiciona em qualquer posição restante.
    while posicoes_coringas.size < @limite_coringa
      disponiveis_para_coringa = todas_posicoes.reject { |pos| posicoes_coringas.include?(pos) }
      break if disponiveis_para_coringa.empty?
      posicoes_coringas << disponiveis_para_coringa.sample
    end

    # Coloca os curingas nas posições selecionadas.
    posicoes_coringas.each do |r, c|
      tabuleiro[r][c] = CORINGA
    end

    tabuleiro
  end

  # Retorna todas as linhas, colunas e diagonais do tabuleiro.
  #
  # @param tabuleiro [Array<Array<String>>] O tabuleiro.
  # @return [Array<Array<String>>] Uma lista de todas as possíveis linhas de vitória.
  def obter_linhas(tabuleiro)
    linhas_horizontais = tabuleiro
    linhas_verticais = tabuleiro.transpose # Transpõe para obter as colunas como linhas.
    diagonal1 = (0...@tamanho).map { |i| tabuleiro[i][i] } # Diagonal principal.
    diagonal2 = (0...@tamanho).map { |i| tabuleiro[i][@tamanho - 1 - i] } # Diagonal secundária.
    linhas_horizontais + linhas_verticais + [diagonal1, diagonal2]
  end

  # Verifica se houve uma vitória em alguma linha, coluna ou diagonal.
  # Uma vitória ocorre quando todos os símbolos em uma linha são iguais (ignorando curingas).
  #
  # @param tabuleiro [Array<Array<String>>] O tabuleiro.
  # @return [Boolean] Verdadeiro se houver vitória, falso caso contrário.
  def verificar_vitoria?(tabuleiro)
    obter_linhas(tabuleiro).any? do |linha|
      # Remove curingas para verificar os símbolos reais.
      simbolos = linha.reject { |s| s == CORINGA }
      next false if simbolos.empty? # Se só tem curingas, não é uma vitória 'pura'.
      # Verifica se todos os símbolos (ou curingas) são iguais ao primeiro símbolo não-curinga.
      linha.all? { |s| s == simbolos[0] || s == CORINGA }
    end
  end

  # Verifica se houve uma sequência bônus em alguma linha, coluna ou diagonal.
  # Uma sequência bônus ocorre quando os símbolos formam uma sequência numérica ou alfabética (sem curingas).
  #
  # @param tabuleiro [Array<Array<String>>] O tabuleiro.
  # @return [Boolean] Verdadeiro se houver bônus, falso caso contrário.
  def verificar_bonus?(tabuleiro)
    obter_linhas(tabuleiro).any? do |linha|
      next false if linha.include?(CORINGA) # Linhas com curingas não contam para bônus.
      indices = linha.map { |s| SIMBOLOS.index(s) } # Converte símbolos para seus índices.
      next false unless indices.all? # Garante que todos os símbolos foram encontrados.

      # Verifica se a linha é uma sequência estrita (crescente ou decrescente).
      sequencia_estrita?(indices)
    end
  end

  # Verifica se uma array de números forma uma sequência numérica estrita
  # (crescente ou decrescente, sem repetições consecutivas).
  #
  # @param numeros [Array<Integer>] Uma array de números (índices de símbolos).
  # @return [Boolean] Verdadeiro se for uma sequência estrita, falso caso contrário.
  def sequencia_estrita?(numeros)
    return false if numeros.length < 2 # Uma sequência precisa de pelo menos dois números.

    # Verifica se é uma sequência crescente estrita
    crescente = true
    (0...numeros.length - 1).each do |i|
      if numeros[i+1] != numeros[i] + 1
        crescente = false
        break
      end
    end
    return true if crescente

    # Verifica se é uma sequência decrescente estrita
    decrescente = true
    (0...numeros.length - 1).each do |i|
      if numeros[i+1] != numeros[i] - 1
        decrescente = false
        break
      end
    end
    return true if decrescente

    false
  end
end

# Slot de dificuldade fácil.
class SlotFacil < CacaNiquel
  def initialize
    super(3, 1, 1, 10, 10) # Tamanho, limite de curinga, aposta min, aposta max, multiplicador de prêmio.
  end
end

# Slot de dificuldade média.
class SlotMedio < CacaNiquel
  def initialize
    super(4, 2, 10, 30, 50) # Tamanho, limite de curinga, aposta min, aposta max, multiplicador de prêmio.
  end
end

# Slot de dificuldade difícil.
class SlotDificil < CacaNiquel
  def initialize
    super(5, 3, 30, 50, 100) # Tamanho, limite de curinga, aposta min, aposta max, multiplicador de prêmio.
  end
end

# Classe principal que orquestra o jogo.
class Principal
  ARQUIVO_DADOS_JOGADORES = 'jogadores.csv'.freeze # Nome do arquivo para salvar/carregar dados dos jogadores.

  def iniciar
    sistema = SistemaAutenticacao.new
    sistema.carregar_de_csv(ARQUIVO_DADOS_JOGADORES) # Carrega os dados dos jogadores ao iniciar.

    jogador_logado = sistema.menu_login # Tenta fazer login ou cadastra um novo jogador.

    loop do
      puts "\n--- Menu do Jogo ---"
      puts "1. Selecionar Dificuldade e Jogar"
      puts "2. Verificar Saldo"
      puts "3. Depositar Dinheiro"
      puts "4. Sacar Dinheiro"
      puts "5. Sair do Jogo"
      puts "--------------------"
      puts "Digite sua escolha:"

      escolha_menu = gets.chomp.to_i

      case escolha_menu
      when 1
        jogo_slot = selecionar_nivel_dificuldade # Permite ao jogador escolher a dificuldade.
        valor_aposta = jogo_slot.solicitar_valor_aposta # Solicita a aposta ao jogador.
        tabuleiro, vitoria, bonus = jogo_slot.jogar(jogador_logado, valor_aposta) # Joga a rodada.

        puts "\n--- Resultado do Slot ---"
        tabuleiro.each { |linha| puts linha.join(' ') } # Exibe o tabuleiro.
        puts "Vitória: #{vitoria ? 'Sim' : 'Não'}"
        puts "Bônus: #{bonus ? 'Sim' : 'Não'}"
        puts "Saldo Atual: R$#{'%.2f' % jogador_logado.saldo}" # Exibe o saldo atual.
        puts "-------------------------"
      when 2
        puts "\nSeu saldo atual é: R$#{'%.2f' % jogador_logado.saldo}"
      when 3
        puts "Digite o valor a depositar:"
        valor = gets.chomp.to_f
        if valor > 0
          jogador_logado.depositar(valor)
          puts "Depósito realizado com sucesso! Novo saldo: R$#{'%.2f' % jogador_logado.saldo}"
        else
          puts "O valor do depósito deve ser positivo."
        end
      when 4
        puts "Digite o valor a sacar (mínimo de R$100 no saldo):"
        valor = gets.chomp.to_f
        begin
          jogador_logado.sacar(valor)
          puts "Saque realizado com sucesso! Novo saldo: R$#{'%.2f' % jogador_logado.saldo}"
        rescue RuntimeError => e
          puts "Falha no saque: #{e.message}"
        end
      when 5
        break # Sai do loop do jogo.
      else
        puts "Opção inválida. Por favor, tente novamente."
      end
      sistema.salvar_para_csv(ARQUIVO_DADOS_JOGADORES) # Salva os dados após cada ação importante.
    end

    sistema.salvar_para_csv(ARQUIVO_DADOS_JOGADORES) # Salva os dados dos jogadores ao sair do jogo.
    puts "Obrigado por jogar!"
  end

  private

  # Permite ao jogador selecionar o nível de dificuldade do caça-níqueis.
  #
  # @return [CacaNiquel] Uma instância da classe CacaNiquel correspondente à dificuldade escolhida.
  def selecionar_nivel_dificuldade
    jogo = nil # Variável para armazenar a instância do jogo.

    loop do
      puts "\n--- Seleção de Nível de Dificuldade ---"
      puts "1. Fácil (Aposta: 1-10)"
      puts "2. Médio (Aposta: 10-30)"
      puts "3. Difícil (Aposta: 30-50)"
      puts "---------------------------------------"
      puts "Digite o número da sua escolha:"

      escolha = gets.chomp.to_i

      case escolha
      when 1
        jogo = SlotFacil.new
        break # Sai do loop após instanciar o jogo.
      when 2
        jogo = SlotMedio.new
        break # Sai do loop.
      when 3
        jogo = SlotDificil.new
        break # Sai do loop.
      else
        puts "Opção inválida. Por favor, digite 1, 2 ou 3."
      end
    end
    jogo # Retorna a instância do jogo selecionado.
  end
end

# Inicia o jogo quando o script é executado.
Principal.new.iniciar
