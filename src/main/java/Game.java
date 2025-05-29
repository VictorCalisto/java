import java.util.*;
import java.util.stream.Collectors;

/**
 * Main game logic for "Deal or No Deal".
 */
class Game {
    private List<Suitcase> allSuitcases;
    private Suitcase playerSuitcase;
    private List<Double> initialValues; // Valores iniciais das maletas
    private Scanner scanner;
    private Random random;
    private GameRanking ranking; // Recebe GameRanking no construtor
    private Player currentPlayer;

    public Game(GameRanking ranking) {
        this.ranking = ranking;
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        initializeGameValues();
    }

    /**
     * Define os valores iniciais das maletas.
     */
    private void initializeGameValues() {
        initialValues = Arrays.asList(
            0.01, 1.00, 5.00, 10.00, 25.00, 50.00, 75.00, 100.00, 200.00, 300.00,
            400.00, 500.00, 750.00, 1000.00, 5000.00, 10000.00, 25000.00, 50000.00,
            75000.00, 100000.00, 250000.00, 500000.00, 750000.00, 1000000.00
        );
    }

    /**
     * Prepara um novo jogo.
     */
    public void setupNewGame(Player player) {
        this.currentPlayer = player;
        allSuitcases = new ArrayList<>();
        List<Double> shuffledValues = new ArrayList<>(initialValues);
        Collections.shuffle(shuffledValues); // Embaralha os valores

        for (int i = 0; i < initialValues.size(); i++) {
            allSuitcases.add(new Suitcase(i + 1, shuffledValues.get(i)));
        }
        playerSuitcase = null;
        System.out.println("\nBem-vindo ao Topa ou Não Topa, " + currentPlayer.getNickname() + "!");
        System.out.println("Vamos começar!");
    }

    /**
     * Permite o jogador escolher sua maleta pessoal.
     */
    public void choosePlayerSuitcase() {
        displayAvailableSuitcases();
        while (true) {
            System.out.print("Escolha a maleta que será sua (número da maleta): ");
            String input = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                continue;
            }
            final int finalChoice = choice; // variável final para lambda

            Suitcase chosen = allSuitcases.stream()
                                    .filter(s -> s.getId() == finalChoice && !s.isOpened())
                                    .findFirst()
                                    .orElse(null);
            if (chosen != null) {
                playerSuitcase = chosen;
                System.out.println("Maleta #" + choice + " é a sua maleta! Boa sorte!");
                break;
            } else {
                System.out.println("Número de maleta inválido ou maleta já escolhida/aberta. Tente novamente.");
            }
        }
    }

    /**
     * Mostra as maletas disponíveis, indicando abertas, sua maleta e fechadas.
     */
    private void displayAvailableSuitcases() {
        System.out.println("\n--- Maletas ---");
        for (Suitcase s : allSuitcases) {
            if (s.isOpened()) {
                System.out.print("Maleta #" + s.getId() + " [ABERTA: R$" + String.format("%.2f", s.getValue()) + "] ");
            } else if (s.equals(playerSuitcase)) {
                System.out.print("Maleta #" + s.getId() + " [SUA MALETA] ");
            } else {
                System.out.print("Maleta #" + s.getId() + " [FECHADA] ");
            }
            if (s.getId() % 5 == 0) System.out.println();
        }
        System.out.println("\n---------------\n");

        System.out.println("Valores restantes no jogo:");
        getRemainingValues().stream()
            .sorted()
            .map(val -> "R$" + String.format("%.2f", val))
            .forEach(val -> System.out.print(val + " "));
        System.out.println("\n");
    }

    /**
     * Retorna os valores das maletas que ainda não foram abertas e que não são a do jogador.
     */
    private List<Double> getRemainingValues() {
        return allSuitcases.stream()
                .filter(s -> !s.isOpened() && !s.equals(playerSuitcase))
                .map(Suitcase::getValue)
                .collect(Collectors.toList());
    }

    /**
     * Permite o jogador escolher uma maleta para abrir.
     */
    public void chooseSuitcaseToOpen() {
        while (true) {
            displayAvailableSuitcases();
            System.out.print("Escolha uma maleta para abrir (número da maleta): ");
            String input = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                continue;
            }
            final int finalChoice = choice; // variável final para lambda

            Suitcase chosen = allSuitcases.stream()
                                    .filter(s -> s.getId() == finalChoice && !s.isOpened() && !s.equals(playerSuitcase))
                                    .findFirst()
                                    .orElse(null);
            if (chosen != null) {
                chosen.open();
                System.out.println("Você abriu a Maleta #" + choice + ", que revelou: R$" + String.format("%.2f", chosen.getValue()) + "!");
                break;
            } else {
                System.out.println("Número de maleta inválido, já aberta ou é sua maleta. Tente novamente.");
            }
        }
    }

    /**
     * Calcula a oferta do banqueiro baseada nos valores restantes.
     * A oferta é 30% do valor esperado (média dos valores restantes).
     */
    public double calculateBankerOffer() {
        List<Double> remainingValues = getRemainingValues();
        if (remainingValues.isEmpty()) {
            return 0.0;
        }

        double sumOfValues = remainingValues.stream().mapToDouble(Double::doubleValue).sum();
        double expectedValue = sumOfValues / remainingValues.size();

        return expectedValue * 0.30;
    }

    /**
     * Pergunta ao jogador se aceita a oferta.
     */
    public boolean askDealOrNoDeal(double offer) {
        System.out.println("\n--- Proposta do Banqueiro ---");
        System.out.println("O Banqueiro oferece: R$" + String.format("%.2f", offer));
        System.out.print("Topa ou Não Topa? (T/N): ");
        String choice = scanner.nextLine().trim().toUpperCase();
        return choice.equals("T");
    }

    /**
     * Loop principal do jogo.
     * Mantém a assinatura sem argumentos, para compatibilidade.
     */
    public void startGameLoop() {
        if (currentPlayer == null) {
            System.out.println("Erro: jogador atual não definido. Use setupNewGame(Player) antes de iniciar o jogo.");
            return;
        }

        setupNewGame(currentPlayer);
        choosePlayerSuitcase();

        int round = 1;

        // Define quantas maletas abrir em cada rodada
        List<Integer> suitcasesPerRound = Arrays.asList(6, 5, 4, 3, 2, 1, 1, 1, 1);

        while (getRemainingValues().size() > 0) {
            System.out.println("\n--- Rodada " + round + " ---");
            int suitcasesToOpenInRound = suitcasesPerRound.get(Math.min(round - 1, suitcasesPerRound.size() - 1));

            int currentSuitcasesToOpen = Math.min(suitcasesToOpenInRound, getRemainingValues().size());

            for (int i = 0; i < currentSuitcasesToOpen; i++) {
                chooseSuitcaseToOpen();
                if (getRemainingValues().isEmpty()) break;
            }

            if (getRemainingValues().isEmpty()) {
                // Acabaram as maletas para abrir, mostra o prêmio do jogador
                System.out.println("\nTodas as outras maletas foram abertas!");
                System.out.println("Sua maleta #" + playerSuitcase.getId() + " contém: R$" + String.format("%.2f", playerSuitcase.getValue()));
                System.out.println("Parabéns, você ganhou o valor da sua maleta!");
                ranking.updatePlayerScore(currentPlayer, playerSuitcase.getValue());
                break;
            }

            double offer = calculateBankerOffer();
            if (offer > 0 && askDealOrNoDeal(offer)) {
                System.out.println("Você aceitou a oferta de R$" + String.format("%.2f", offer) + " e finalizou o jogo!");
                System.out.println("Sua maleta #" + playerSuitcase.getId() + " continha: R$" + String.format("%.2f", playerSuitcase.getValue()));
                ranking.updatePlayerScore(currentPlayer, offer);
                break;
            } else {
                System.out.println("Você recusou a oferta! O jogo continua!");
            }
            round++;
        }
        System.out.println("Fim do jogo!");
    }
}
