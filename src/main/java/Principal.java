import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

/**
 * Classe que representa um jogador no sistema do caça-níqueis.
 */
class Jogador {
    private String email;
    private String senhaHash; // Hash da senha para segurança.
    private double saldo; // Saldo inicial do jogador.
    private LocalDate dataDeNascimento;
    private String apelido;
    private boolean statusMaiorDeIdade; // Define o status do jogador com base na idade.

    /**
     * Construtor da classe Jogador.
     *
     * @param email            O email do jogador.
     * @param senha            A senha do jogador.
     * @param dataDeNascimento A data de nascimento do jogador.
     * @param apelido          O apelido do jogador.
     */
    public Jogador(String email, String senha, LocalDate dataDeNascimento, String apelido) {
        this.senhaHash = gerarHashSHA256(senha); // Hash da senha para segurança.
        this.saldo = 50.0; // Saldo inicial do jogador.
        this.email = email;
        this.dataDeNascimento = dataDeNascimento;
        this.apelido = apelido;
        this.statusMaiorDeIdade = verificarMaiorDeIdade(); // Define o status do jogador com base na idade.
    }

    // Getters para os atributos.
    public String getEmail() {
        return email;
    }

    public double getSaldo() {
        return saldo;
    }

    public LocalDate getDataDeNascimento() {
        return dataDeNascimento;
    }

    public String getApelido() {
        return apelido;
    }

    public boolean getStatusMaiorDeIdade() {
        return statusMaiorDeIdade;
    }

    // Adicionado getter para o hash da senha, necessário para salvar no CSV
    public String getSenhaHash() {
        return senhaHash;
    }

    /**
     * Gera o hash SHA-256 de uma string.
     *
     * @param texto O texto a ser hasheado.
     * @return O hash SHA-256 em formato hexadecimal.
     */
    private String gerarHashSHA256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash SHA-256", e);
        }
    }

    /**
     * Autentica o jogador verificando a senha.
     *
     * @param senha A senha digitada pelo usuário.
     * @return Verdadeiro se a senha estiver correta, falso caso contrário.
     */
    public boolean autenticar(String senha) {
        return gerarHashSHA256(senha).equals(this.senhaHash);
    }

    /**
     * Verifica se o jogador é maior de idade (18 anos ou mais).
     *
     * @return Verdadeiro se o jogador for maior de idade, falso caso contrário.
     */
    public boolean verificarMaiorDeIdade() {
        LocalDate hoje = LocalDate.now();
        Period periodo = Period.between(dataDeNascimento, hoje);
        return periodo.getYears() >= 18;
    }

    /**
     * Verifica se o jogador pode jogar (é maior de idade).
     *
     * @return Verdadeiro se o jogador puder jogar, falso caso contrário.
     */
    public boolean podeJogar() {
        return this.statusMaiorDeIdade;
    }

    /**
     * Adiciona um valor ao saldo do jogador.
     *
     * @param valor O valor a ser depositado.
     */
    public void depositar(double valor) {
        this.saldo += valor;
    }

    /**
     * Retira um valor do saldo do jogador.
     * Requer um saldo mínimo de R$100 para sacar.
     *
     * @param valor O valor a ser sacado.
     * @throws RuntimeException Se o saldo for insuficiente para saque.
     */
    public void sacar(double valor) {
        if (this.saldo < 100 || this.saldo < valor) {
            throw new RuntimeException("Saldo insuficiente para saque. Mínimo de R$100 no saldo.");
        }
        this.saldo -= valor;
    }

    /**
     * Realiza uma aposta, diminuindo o saldo do jogador.
     *
     * @param valor O valor da aposta.
     * @throws RuntimeException Se o jogador for inativo ou tiver saldo
     *                          insuficiente.
     */
    public void apostar(double valor) {
        if (!podeJogar()) {
            throw new RuntimeException("Jogador inativo, não pode jogar.");
        }
        if (this.saldo < valor) {
            throw new RuntimeException("Saldo insuficiente para aposta.");
        }
        this.saldo -= valor;
    }

    /**
     * Recompensa o jogador adicionando um valor ao saldo.
     *
     * @param valor O valor da recompensa.
     */
    public void recompensar(double valor) {
        this.saldo += valor;
    }

    // Setters necessários para carregar dados do CSV.
    // O setSenhaHash é importante para reconstruir o objeto do CSV sem re-hashear.
    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDataDeNascimento(LocalDate dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public void setStatusMaiorDeIdade(boolean statusMaiorDeIdade) {
        this.statusMaiorDeIdade = statusMaiorDeIdade;
    }
}

/**
 * Gerencia o registro e autenticação de usuários.
 */
class SistemaAutenticacao {
    private Map<String, Jogador> usuarios; // Mapa para armazenar os jogadores, usando o email como chave.
    private Scanner scanner; // Objeto Scanner para leitura de entrada do usuário.

    /**
     * Construtor da classe SistemaAutenticacao.
     */
    public SistemaAutenticacao() {
        this.usuarios = new HashMap<>();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Exibe o menu de login e gerencia o fluxo de autenticação/cadastro.
     *
     * @return O objeto Jogador do jogador logado.
     */
    public Jogador menuLogin() {
        int tentativasSemEspera = 5; // Número de tentativas antes de aplicar o atraso.
        long segundosEspera = 30; // Tempo de espera entre tentativas em segundos.

        while (true) {
            System.out.println("--------------------------------------------------");
            System.out.println("Por favor, digite 1 para fazer login ou 2 para se cadastrar.");
            System.out.println("--------------------------------------------------");
            int escolha = scanner.nextInt();
            scanner.nextLine(); // Consome a nova linha.

            switch (escolha) {
                case 1: // Opção de login
                    System.out.println("Digite seu email:");
                    String emailLogin = scanner.nextLine();
                    Jogador jogador = usuarios.get(emailLogin); // Tenta encontrar o jogador pelo email.

                    if (jogador == null) {
                        System.out.println("Você ainda não tem um cadastro. Por favor, cadastre-se primeiro.");
                        break; // Volta para o menu principal.
                    }

                    int tentativas = 0;
                    while (true) {
                        System.out.println("Digite sua senha:");
                        String senhaDigitada = scanner.nextLine();
                        if (jogador.autenticar(senhaDigitada)) {
                            System.out.printf("Login bem-sucedido! Bem-vindo(a), %s.%n", jogador.getApelido());
                            return jogador; // Retorna o objeto do jogador logado.
                        } else {
                            tentativas++;
                            System.out.printf("Senha incorreta. Você tentou %d vez(es).%n", tentativas);
                            if (tentativas >= tentativasSemEspera) {
                                // Calcula o tempo de espera progressivo.
                                long tempoDormir = (tentativas - (tentativasSemEspera - 1)) * segundosEspera;
                                System.out.printf(
                                        "Muitas tentativas incorretas. Por favor, espere %d segundos antes de tentar novamente.%n",
                                        tempoDormir);
                                try {
                                    Thread.sleep(tempoDormir * 1000); // Dorme por tempoDormir segundos.
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    System.out.println("O atraso foi interrompido.");
                                }
                            }
                        }
                    }

                case 2: // Opção de cadastro
                    System.out.println("Digite um email válido:");
                    String emailCadastro = scanner.nextLine();
                    if (usuarios.containsKey(emailCadastro)) {
                        System.out.println("Email já cadastrado. Por favor, faça login.");
                        break; // Volta para o menu principal.
                    }

                    System.out.println("Digite uma senha:");
                    String senhaCadastro = scanner.nextLine();
                    System.out.println("Digite sua data de nascimento (AAAA-MM-DD):");
                    LocalDate dataNascimento;
                    try {
                        dataNascimento = LocalDate.parse(scanner.nextLine());
                    } catch (java.time.format.DateTimeParseException e) {
                        System.out.println("Formato de data inválido. Por favor, use AAAA-MM-DD.");
                        break; // Volta para o menu principal.
                    }

                    System.out.println("Digite seu apelido:");
                    String apelidoCadastro = scanner.nextLine();

                    Jogador novoJogador = cadastrar(emailCadastro, senhaCadastro, dataNascimento, apelidoCadastro);
                    System.out.println("Cadastro realizado com sucesso! Você pode fazer login agora.");
                    break; // Volta para o menu principal.
                default:
                    System.out.println("Escolha inválida. Por favor, digite 1 ou 2.");
                    break;
            }
        }
    }

    /**
     * Cadastra um novo jogador no sistema.
     *
     * @param email    O email do jogador.
     * @param senha    A senha do jogador.
     * @param dataNasc A data de nascimento do jogador.
     * @param apelido  O apelido do jogador.
     * @return O objeto Jogador recém-criado.
     * @throws RuntimeException Se o email já estiver cadastrado.
     */
    public Jogador cadastrar(String email, String senha, LocalDate dataNasc, String apelido) {
        if (usuarios.containsKey(email)) {
            throw new RuntimeException("Email já cadastrado.");
        }
        Jogador jogador = new Jogador(email, senha, dataNasc, apelido);
        usuarios.put(email, jogador);
        return jogador;
    }

    /**
     * Salva os dados dos jogadores em um arquivo CSV.
     *
     * @param caminho O caminho para o arquivo CSV.
     */
    public void salvarParaCsv(String caminho) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminho))) {
            // Escreve o cabeçalho do CSV.
            writer.write("email,senha_hash,saldo,data_de_nascimento,apelido\n");
            for (Jogador j : usuarios.values()) {
                // Escreve os dados de cada jogador.
                writer.write(String.format("%s,%s,%.2f,%s,%s%n",
                        j.getEmail(),
                        j.getSenhaHash(), // Agora usamos o getter para acessar o hash da senha
                        j.getSaldo(),
                        j.getDataDeNascimento().toString(),
                        j.getApelido()));
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar jogadores para CSV: " + e.getMessage());
        }
    }

    /**
     * Carrega os dados dos jogadores de um arquivo CSV.
     *
     * @param caminho O caminho para o arquivo CSV.
     */
    public void carregarDeCsv(String caminho) {
        File arquivo = new File(caminho);
        if (!arquivo.exists()) {
            return; // Não faz nada se o arquivo não existir.
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(caminho))) {
            String linha;
            // Lê o cabeçalho.
            reader.readLine();
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(",");
                if (partes.length == 5) {
                    String email = partes[0];
                    String senhaHash = partes[1];
                    double saldo = Double.parseDouble(partes[2]);
                    LocalDate dataNascimento = LocalDate.parse(partes[3]);
                    String apelido = partes[4];

                    // Recria o objeto Jogador a partir dos dados do CSV.
                    // Passamos uma senha dummy para o construtor, pois o hash real será definido a
                    // seguir.
                    Jogador jogador = new Jogador(email, "senha_dummy_para_carga", dataNascimento, apelido);
                    jogador.setSenhaHash(senhaHash); // Define o hash da senha diretamente do CSV
                    jogador.setSaldo(saldo);
                    jogador.setStatusMaiorDeIdade(jogador.verificarMaiorDeIdade()); // Recalcula o status.

                    usuarios.put(email, jogador);
                }
            }
        } catch (IOException | NumberFormatException | java.time.format.DateTimeParseException e) {
            System.err.println("Erro ao carregar jogadores do CSV: " + e.getMessage());
        }
    }
}

/**
 * Classe base para os diferentes tipos de caça-níqueis.
 */
abstract class CacaNiquel {
    protected static final List<String> SIMBOLOS = Arrays.asList("A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J",
            "Q", "K"); // Símbolos padrão.
    protected static final String CORINGA = "*"; // Símbolo curinga.
    protected Random random; // Objeto Random para geração de números aleatórios.
    protected Scanner scanner; // Objeto Scanner para leitura de entrada do usuário.

    protected int tamanho; // Tamanho do tabuleiro (e.g., 3x3, 4x4).
    protected int limiteCoringa; // Limite de curingas por coluna.
    protected int apostaMin; // Aposta mínima permitida.
    protected int apostaMax; // Aposta máxima permitida.
    protected int multiplicadorPremio; // Multiplicador de prêmio para vitória.

    /**
     * Construtor da classe CacaNiquel.
     *
     * @param tamanho             Tamanho do tabuleiro.
     * @param limiteCoringa       Limite de curingas.
     * @param apostaMin           Aposta mínima.
     * @param apostaMax           Aposta máxima.
     * @param multiplicadorPremio Multiplicador de prêmio.
     */
    public CacaNiquel(int tamanho, int limiteCoringa, int apostaMin, int apostaMax, int multiplicadorPremio) {
        this.tamanho = tamanho;
        this.limiteCoringa = limiteCoringa;
        this.apostaMin = apostaMin;
        this.apostaMax = apostaMax;
        this.multiplicadorPremio = multiplicadorPremio;
        this.random = new Random();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Executa uma rodada do jogo de caça-níqueis.
     *
     * @param jogador     O jogador que está apostando.
     * @param valorAposta O valor da aposta.
     * @return Um array contendo o tabuleiro, status de vitória e status de bônus.
     * @throws RuntimeException Se a aposta estiver fora dos limites.
     */
    public Object[] jogar(Jogador jogador, double valorAposta) {
        if (valorAposta < apostaMin || valorAposta > apostaMax) {
            throw new RuntimeException(
                    String.format("Valor da aposta fora dos limites (%d-%d).", apostaMin, apostaMax));
        }

        jogador.apostar(valorAposta); // Decrementa o saldo do jogador.
        String[][] tabuleiro = gerarTabuleiro(); // Gera o tabuleiro.

        boolean vitoria = verificarVitoria(tabuleiro); // Verifica se houve vitória.
        boolean bonus = verificarBonus(tabuleiro); // Verifica se houve bônus.

        if (vitoria) {
            jogador.recompensar(valorAposta * multiplicadorPremio); // Recompensa por vitória.
            System.out.printf("Parabéns! Você ganhou R$%.2f!%n", (valorAposta * multiplicadorPremio));
        } else if (bonus) {
            jogador.recompensar(valorAposta); // Recompensa por bônus (metade da vitória).
            System.out.printf("Você ganhou um bônus! Você ganhou R$%.2f!%n", (valorAposta));
        } else {
            System.out.println("Nenhuma vitória desta vez.");
        }

        return new Object[] { tabuleiro, vitoria, bonus };
    }

    /**
     * Solicita e valida o valor da aposta do jogador.
     *
     * @return O valor da aposta válido.
     */
    public int solicitarValorAposta() {
        while (true) {
            System.out.printf("Digite o valor da aposta (entre %d e %d):%n", apostaMin, apostaMax);
            String entrada = scanner.nextLine();
            try {
                int valor = Integer.parseInt(entrada);
                if (valor >= apostaMin && valor <= apostaMax) {
                    return valor;
                }
                System.out.printf("Valor inválido. Digite um número inteiro entre %d e %d.%n", apostaMin, apostaMax);
            } catch (NumberFormatException e) {
                System.out.printf("Entrada inválida. Digite um número inteiro entre %d e %d.%n", apostaMin, apostaMax);
            }
        }
    }

    /**
     * Gera o tabuleiro do caça-níqueis.
     *
     * @return O tabuleiro gerado.
     */
    private String[][] gerarTabuleiro() {
        String[][] tabuleiro = new String[tamanho][tamanho];
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                tabuleiro[i][j] = SIMBOLOS.get(random.nextInt(SIMBOLOS.size())); // Inicializa com símbolos aleatórios.
            }
        }

        // Lista de todas as posições possíveis no tabuleiro (linha, coluna).
        List<int[]> todasPosicoes = new ArrayList<>();
        for (int r = 0; r < tamanho; r++) {
            for (int c = 0; c < tamanho; c++) {
                todasPosicoes.add(new int[] { r, c });
            }
        }

        // Seleciona posições aleatórias únicas para os curingas.
        // Garante que cada curinga esteja em uma coluna diferente, se possível.
        List<int[]> posicoesCoringas = new ArrayList<>();
        Set<Integer> colunasComCoringa = new HashSet<>(); // Usar Set para busca rápida.

        // Tenta adicionar curingas em colunas distintas primeiro.
        for (int i = 0; i < limiteCoringa; i++) {
            if (colunasComCoringa.size() == tamanho) {
                break; // Já cobriu todas as colunas possíveis.
            }

            List<int[]> disponiveisParaCoringa = new ArrayList<>();
            for (int[] pos : todasPosicoes) {
                if (!colunasComCoringa.contains(pos[1]) && !contemPosicao(posicoesCoringas, pos)) {
                    disponiveisParaCoringa.add(pos);
                }
            }

            if (disponiveisParaCoringa.isEmpty()) {
                // Se não há mais colunas disponíveis para garantir unicidade,
                // seleciona de todas as posições restantes que ainda não têm curinga.
                disponiveisParaCoringa.clear();
                for (int[] pos : todasPosicoes) {
                    if (!contemPosicao(posicoesCoringas, pos)) {
                        disponiveisParaCoringa.add(pos);
                    }
                }
                if (disponiveisParaCoringa.isEmpty()) {
                    break;
                }
            }
            // CORRECTION: Corrected variable name from disponiveisParaCoringas to
            // disponiveisParaCoringa
            int[] posSelecionada = disponiveisParaCoringa.get(random.nextInt(disponiveisParaCoringa.size()));
            posicoesCoringas.add(posSelecionada);
            colunasComCoringa.add(posSelecionada[1]);
        }

        // Se ainda faltam curingas para o total desejado, adiciona em qualquer posição
        // restante.
        while (posicoesCoringas.size() < limiteCoringa) {
            List<int[]> disponiveisParaCoringa = new ArrayList<>();
            for (int[] pos : todasPosicoes) {
                if (!contemPosicao(posicoesCoringas, pos)) {
                    disponiveisParaCoringa.add(pos);
                }
            }
            if (disponiveisParaCoringa.isEmpty()) {
                break;
            }
            posicoesCoringas.add(disponiveisParaCoringa.get(random.nextInt(disponiveisParaCoringa.size())));
        }

        // Coloca os curingas nas posições selecionadas.
        for (int[] pos : posicoesCoringas) {
            tabuleiro[pos[0]][pos[1]] = CORINGA;
        }

        return tabuleiro;
    }

    /**
     * Método auxiliar para verificar se uma lista de arrays de int contém um array
     * específico.
     *
     * @param lista    A lista de arrays de int.
     * @param elemento O array de int a ser verificado.
     * @return Verdadeiro se a lista contiver o elemento, falso caso contrário.
     */
    private boolean contemPosicao(List<int[]> lista, int[] elemento) {
        for (int[] item : lista) {
            if (Arrays.equals(item, elemento)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retorna todas as linhas, colunas e diagonais do tabuleiro.
     *
     * @param tabuleiro O tabuleiro.
     * @return Uma lista de todas as possíveis linhas de vitória.
     */
    protected List<List<String>> obterLinhas(String[][] tabuleiro) {
        List<List<String>> linhasTotais = new ArrayList<>();

        // Linhas horizontais
        for (int i = 0; i < tamanho; i++) {
            linhasTotais.add(Arrays.asList(tabuleiro[i]));
        }

        // Linhas verticais (colunas)
        for (int j = 0; j < tamanho; j++) {
            List<String> coluna = new ArrayList<>();
            for (int i = 0; i < tamanho; i++) {
                coluna.add(tabuleiro[i][j]);
            }
            linhasTotais.add(coluna);
        }

        // Diagonal principal
        List<String> diagonal1 = new ArrayList<>();
        for (int i = 0; i < tamanho; i++) {
            diagonal1.add(tabuleiro[i][i]);
        }
        linhasTotais.add(diagonal1);

        // Diagonal secundária
        List<String> diagonal2 = new ArrayList<>();
        for (int i = 0; i < tamanho; i++) {
            diagonal2.add(tabuleiro[i][tamanho - 1 - i]);
        }
        linhasTotais.add(diagonal2);

        return linhasTotais;
    }

    /**
     * Verifica se houve uma vitória em alguma linha, coluna ou diagonal.
     * Uma vitória ocorre quando todos os símbolos em uma linha são iguais
     * (ignorando curingas).
     *
     * @param tabuleiro O tabuleiro.
     * @return Verdadeiro se houver vitória, falso caso contrário.
     */
    protected boolean verificarVitoria(String[][] tabuleiro) {
        for (List<String> linha : obterLinhas(tabuleiro)) {
            List<String> simbolosNaoCoringas = new ArrayList<>();
            for (String s : linha) {
                if (!s.equals(CORINGA)) {
                    simbolosNaoCoringas.add(s);
                }
            }

            if (simbolosNaoCoringas.isEmpty()) {
                continue; // Se só tem curingas, não é uma vitória 'pura'.
            }

            // CORRECTION: Declared and initialized firstSimbolo
            String firstSimbolo = simbolosNaoCoringas.get(0);
            boolean vitoriaNaLinha = true;
            for (String s : linha) {
                if (!s.equals(firstSimbolo) && !s.equals(CORINGA)) {
                    vitoriaNaLinha = false;
                    break;
                }
            }
            if (vitoriaNaLinha) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se houve uma sequência bônus em alguma linha, coluna ou diagonal.
     * Uma sequência bônus ocorre quando os símbolos formam uma sequência numérica
     * ou alfabética (sem curingas).
     *
     * @param tabuleiro O tabuleiro.
     * @return Verdadeiro se houver bônus, falso caso contrário.
     */
    protected boolean verificarBonus(String[][] tabuleiro) {
        for (List<String> linha : obterLinhas(tabuleiro)) {
            if (linha.contains(CORINGA)) {
                continue; // Linhas com curingas não contam para bônus.
            }

            List<Integer> indices = new ArrayList<>();
            for (String s : linha) {
                int index = SIMBOLOS.indexOf(s);
                if (index == -1) {
                    indices.clear(); // Se o símbolo não for encontrado, a linha não é válida para bônus.
                    break;
                }
                indices.add(index);
            }

            if (!indices.isEmpty() && sequenciaEstrita(indices)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se uma lista de números forma uma sequência numérica estrita
     * (crescente ou decrescente, sem repetições consecutivas).
     *
     * @param numeros Uma lista de números (índices de símbolos).
     * @return Verdadeiro se for uma sequência estrita, falso caso contrário.
     */
    protected boolean sequenciaEstrita(List<Integer> numeros) {
        if (numeros.size() < 2) {
            return false; // Uma sequência precisa de pelo menos dois números.
        }

        // Verifica se é uma sequência crescente estrita
        boolean crescente = true;
        for (int i = 0; i < numeros.size() - 1; i++) {
            if (numeros.get(i + 1) != numeros.get(i) + 1) {
                crescente = false;
                break;
            }
        }
        if (crescente) {
            return true;
        }

        // Verifica se é uma sequência decrescente estrita
        boolean decrescente = true;
        for (int i = 0; i < numeros.size() - 1; i++) {
            if (numeros.get(i + 1) != numeros.get(i) - 1) {
                decrescente = false;
                break;
            }
        }
        return decrescente;
    }
}

/**
 * Slot de dificuldade fácil.
 */
class SlotFacil extends CacaNiquel {
    /**
     * Construtor da classe SlotFacil.
     */
    public SlotFacil() {
        super(3, 1, 1, 10, 10); // Tamanho, limite de curinga, aposta min, aposta max, multiplicador de prêmio.
    }
}

/**
 * Slot de dificuldade média.
 */
class SlotMedio extends CacaNiquel {
    /**
     * Construtor da classe SlotMedio.
     */
    public SlotMedio() {
        super(4, 2, 10, 30, 50); // Tamanho, limite de curinga, aposta min, aposta max, multiplicador de prêmio.
    }
}

/**
 * Slot de dificuldade difícil.
 */
class SlotDificil extends CacaNiquel {
    /**
     * Construtor da classe SlotDificil.
     */
    public SlotDificil() {
        super(5, 3, 30, 50, 100); // Tamanho, limite de curinga, aposta min, aposta max, multiplicador de prêmio.
    }
}

/**
 * Classe principal que orquestra o jogo.
 */
public class Principal {
    private static final String ARQUIVO_DADOS_JOGADORES = "jogadores.csv"; // Nome do arquivo para salvar/carregar dados
                                                                           // dos jogadores.
    private Scanner scanner; // Objeto Scanner para leitura de entrada do usuário.

    /**
     * Construtor da classe Principal.
     */
    public Principal() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Inicia o fluxo principal do jogo.
     */
    public void iniciar() {
        SistemaAutenticacao sistema = new SistemaAutenticacao();
        sistema.carregarDeCsv(ARQUIVO_DADOS_JOGADORES); // Carrega os dados dos jogadores ao iniciar.

        Jogador jogadorLogado = sistema.menuLogin(); // Tenta fazer login ou cadastra um novo jogador.

        while (true) {
            System.out.println("\n--- Menu do Jogo ---");
            System.out.println("1. Selecionar Dificuldade e Jogar");
            System.out.println("2. Verificar Saldo");
            System.out.println("3. Depositar Dinheiro");
            System.out.println("4. Sacar Dinheiro");
            System.out.println("5. Sair do Jogo");
            System.out.println("--------------------");
            System.out.println("Digite sua escolha:");

            int escolhaMenu = scanner.nextInt();
            scanner.nextLine(); // Consome a nova linha.

            switch (escolhaMenu) {
                case 1:
                    CacaNiquel jogoSlot = selecionarNivelDificuldade(); // Permite ao jogador escolher a dificuldade.
                    int valorAposta = jogoSlot.solicitarValorAposta(); // Solicita a aposta ao jogador.
                    try {
                        Object[] resultado = jogoSlot.jogar(jogadorLogado, valorAposta); // Joga a rodada.
                        String[][] tabuleiro = (String[][]) resultado[0];
                        boolean vitoria = (boolean) resultado[1];
                        boolean bonus = (boolean) resultado[2];

                        System.out.println("\n--- Resultado do Slot ---");
                        for (String[] linha : tabuleiro) {
                            for (String simbolo : linha) {
                                System.out.print(simbolo + " ");
                            }
                            System.out.println();
                        }
                        System.out.printf("Vitória: %s%n", vitoria ? "Sim" : "Não");
                        System.out.printf("Bônus: %s%n", bonus ? "Sim" : "Não");
                        System.out.printf("Saldo Atual: R$%.2f%n", jogadorLogado.getSaldo()); // Exibe o saldo atual.
                        System.out.println("-------------------------");
                    } catch (RuntimeException e) {
                        System.out.println("Erro ao jogar: " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.printf("\nSeu saldo atual é: R$%.2f%n", jogadorLogado.getSaldo());
                    break;
                case 3:
                    System.out.println("Digite o valor a depositar:");
                    double valorDeposito = scanner.nextDouble();
                    scanner.nextLine(); // Consome a nova linha.
                    if (valorDeposito > 0) {
                        jogadorLogado.depositar(valorDeposito);
                        System.out.printf("Depósito realizado com sucesso! Novo saldo: R$%.2f%n",
                                jogadorLogado.getSaldo());
                    } else {
                        System.out.println("O valor do depósito deve ser positivo.");
                    }
                    break;
                case 4:
                    System.out.println("Digite o valor a sacar (mínimo de R$100 no saldo):");
                    double valorSaque = scanner.nextDouble();
                    scanner.nextLine(); // Consome a nova linha.
                    try {
                        jogadorLogado.sacar(valorSaque);
                        System.out.printf("Saque realizado com sucesso! Novo saldo: R$%.2f%n",
                                jogadorLogado.getSaldo());
                    } catch (RuntimeException e) {
                        System.out.println("Falha no saque: " + e.getMessage());
                    }
                    break;
                case 5:
                    sistema.salvarParaCsv(ARQUIVO_DADOS_JOGADORES); // Salva os dados dos jogadores ao sair do jogo.
                    System.out.println("Obrigado por jogar!");
                    scanner.close(); // Fecha o scanner ao sair.
                    return; // Sai do método iniciar e encerra o programa.
                default:
                    System.out.println("Opção inválida. Por favor, tente novamente.");
                    break;
            }
            sistema.salvarParaCsv(ARQUIVO_DADOS_JOGADORES); // Salva os dados após cada ação importante.
        }
    }

    /**
     * Permite ao jogador selecionar o nível de dificuldade do caça-níqueis.
     *
     * @return Uma instância da classe CacaNiquel correspondente à dificuldade
     *         escolhida.
     */
    private CacaNiquel selecionarNivelDificuldade() {
        CacaNiquel jogo = null; // Variável para armazenar a instância do jogo.

        while (true) {
            System.out.println("\n--- Seleção de Nível de Dificuldade ---");
            System.out.println("1. Fácil (Aposta: 1-10)");
            System.out.println("2. Médio (Aposta: 10-30)");
            System.out.println("3. Difícil (Aposta: 30-50)");
            System.out.println("---------------------------------------");
            System.out.println("Digite o número da sua escolha:");

            int escolha = scanner.nextInt();
            scanner.nextLine(); // Consome a nova linha.

            switch (escolha) {
                case 1:
                    jogo = new SlotFacil();
                    break; // Sai do loop após instanciar o jogo.
                case 2:
                    jogo = new SlotMedio();
                    break; // Sai do loop.
                case 3:
                    jogo = new SlotDificil();
                    break; // Sai do loop.
                default:
                    System.out.println("Opção inválida. Por favor, digite 1, 2 ou 3.");
                    continue; // Continua o loop para pedir a escolha novamente.
            }
            break; // Sai do loop principal de seleção de dificuldade.
        }
        return jogo; // Retorna a instância do jogo selecionado.
    }

    /**
     * Método principal para iniciar a aplicação.
     *
     * @param args Argumentos da linha de comando (não utilizados neste caso).
     */
    public static void main(String[] args) {
        new Principal().iniciar();
    }
}
