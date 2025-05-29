import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DatabaseManager dbManager = new DatabaseManager(); // Inicializa o gerenciador de banco de dados
        GameRanking gameRanking = new GameRanking(dbManager); // Passa o dbManager para o GameRanking
        Game game = new Game(gameRanking); // Passa o gameRanking para o Game
        Player loggedInPlayer = null;

        while (true) {
            System.out.println("\n--- Menu Principal ---");
            System.out.println("1. Entrar");
            System.out.println("2. Criar novo jogador");
            System.out.println("3. Ver ranking");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Apelido: ");
                    String loginNickname = scanner.nextLine();
                    System.out.print("Senha: ");
                    String loginPassword = scanner.nextLine();
                    loggedInPlayer = gameRanking.authenticatePlayer(loginNickname, loginPassword);
                    if (loggedInPlayer != null) {
                        game.setupNewGame(loggedInPlayer);
                        game.startGameLoop();
                        loggedInPlayer = null; // Reinicia o jogador logado após o fim do jogo
                    }
                    break;
                case "2":
                    System.out.print("Novo Apelido: ");
                    String newNickname = scanner.nextLine();
                    System.out.print("Nova Senha: ");
                    String newPassword = scanner.nextLine();
                    if (newNickname.isEmpty() || newPassword.isEmpty()) {
                        System.out.println("Apelido e senha não podem ser vazios.");
                    } else {
                        gameRanking.registerPlayer(newNickname, newPassword);
                    }
                    break;
                case "3":
                    gameRanking.displayRanking();
                    break;
                case "4":
                    System.out.println("Obrigado por jogar! Até a próxima!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }
}