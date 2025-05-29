import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manages the game ranking, now interacting with the DatabaseManager.
 */
class GameRanking {
    private DatabaseManager dbManager; // Dependência do gerenciador de banco de dados

    public GameRanking(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Registers a new player by delegating to the DatabaseManager.
     * @param nickname Player's nickname.
     * @param password Player's password.
     * @return The newly created Player object, or null if registration fails.
     */
    public Player registerPlayer(String nickname, String password) {
        return dbManager.registerPlayer(nickname, password);
    }

    /**
     * Authenticates a player by delegating to the DatabaseManager.
     * @param nickname Player's nickname.
     * @param password Player's password.
     * @return The authenticated Player object, or null if authentication fails.
     */
    public Player authenticatePlayer(String nickname, String password) {
        return dbManager.authenticatePlayer(nickname, password);
    }

    /**
     * Displays the top players by highest score, retrieving data from the database.
     */
    public void displayRanking() {
        Map<String, Player> players = dbManager.getAllPlayers(); // Obtém todos os jogadores do banco

        if (players.isEmpty()) {
            System.out.println("Nenhum jogador no ranking ainda. Crie um novo jogador para começar!");
            return;
        }
        System.out.println("\n--- Ranking dos Jogadores ---");
        // Filtra para exibir apenas os 10 melhores
        players.values().stream()
                .sorted(Comparator.comparingDouble(Player::getHighestScore).reversed())
                .limit(10)
                .forEach(System.out::println);
        System.out.println("-----------------------------\n");
    }

    /**
     * Updates a player's highest score, delegating to the DatabaseManager.
     * @param player The player to update.
     * @param newScore The new score to consider.
     */
    public void updatePlayerScore(Player player, double newScore) {
        dbManager.updatePlayerScore(player, newScore);
    }
}