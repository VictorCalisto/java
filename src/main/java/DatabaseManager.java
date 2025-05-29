import org.jasypt.util.password.StrongPasswordEncryptor; // Para criptografia de senhas
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages database connection and player data operations.
 */
class DatabaseManager {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/dealornodeald_db";
    private static final String DB_USER = "gameuser";
    private static final String DB_PASSWORD = "strongpassword"; // Altere para sua senha real

    // Jasypt para criptografia de senhas
    private final StrongPasswordEncryptor passwordEncryptor;

    public DatabaseManager() {
        this.passwordEncryptor = new StrongPasswordEncryptor();
        // Carrega o driver JDBC do PostgreSQL
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver JDBC do PostgreSQL não encontrado. " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Establishes a connection to the PostgreSQL database.
     * @return A database connection.
     * @throws SQLException If a database access error occurs.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Registers a new player in the database.
     * @param nickname The player's nickname.
     * @param password The player's plaintext password.
     * @return The created Player object, or null if registration fails (e.g., nickname already exists).
     */
    public Player registerPlayer(String nickname, String password) {
        String encryptedPassword = passwordEncryptor.encryptPassword(password);
        String sql = "INSERT INTO players (nickname, encrypted_password, highest_score) VALUES (?, ?, ?) ON CONFLICT (nickname) DO NOTHING";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname);
            pstmt.setString(2, encryptedPassword);
            pstmt.setDouble(3, 0.00); // Novo jogador começa com score 0

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Jogador '" + nickname + "' criado com sucesso no banco de dados!");
                return new Player(nickname, 0.0);
            } else {
                System.out.println("Erro: Apelido '" + nickname + "' já existe.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao registrar jogador: " + e.getMessage());
            return null;
        }
    }

    /**
     * Authenticates a player against the database.
     * @param nickname The player's nickname.
     * @param plaintextPassword The player's plaintext password to verify.
     * @return The authenticated Player object, or null if authentication fails.
     */
    public Player authenticatePlayer(String nickname, String plaintextPassword) {
        String sql = "SELECT nickname, encrypted_password, highest_score FROM players WHERE nickname = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String encryptedPassword = rs.getString("encrypted_password");
                if (passwordEncryptor.checkPassword(plaintextPassword, encryptedPassword)) {
                    System.out.println("Bem-vindo de volta, " + nickname + "!");
                    return new Player(rs.getString("nickname"), rs.getDouble("highest_score"));
                }
            }
            System.out.println("Apelido ou senha inválidos.");
            return null;
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar jogador: " + e.getMessage());
            return null;
        }
    }

    /**
     * Updates a player's highest score in the database if the new score is higher.
     * @param player The player whose score is to be updated.
     * @param newScore The new score to consider.
     */
    public void updatePlayerScore(Player player, double newScore) {
        if (newScore > player.getHighestScore()) {
            String sql = "UPDATE players SET highest_score = ? WHERE nickname = ?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setDouble(1, newScore);
                pstmt.setString(2, player.getNickname());
                pstmt.executeUpdate();
                player.setHighestScore(newScore); // Atualiza o objeto Player em memória
                System.out.println("Pontuação de '" + player.getNickname() + "' atualizada para R$" + String.format("%.2f", newScore));
            } catch (SQLException e) {
                System.err.println("Erro ao atualizar pontuação do jogador: " + e.getMessage());
            }
        }
    }

    /**
     * Retrieves all players from the database, sorted by highest score.
     * @return A map of players, keyed by nickname.
     */
    public Map<String, Player> getAllPlayers() {
        Map<String, Player> players = new HashMap<>();
        String sql = "SELECT nickname, highest_score FROM players ORDER BY highest_score DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String nickname = rs.getString("nickname");
                double highestScore = rs.getDouble("highest_score");
                players.put(nickname, new Player(nickname, highestScore));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar jogadores: " + e.getMessage());
        }
        return players;
    }
}