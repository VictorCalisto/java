import java.sql.*;
import java.time.LocalDate;

import org.mindrot.jbcrypt.BCrypt;


public class Database {
    private static Connection connection;

    private Database() {}
    // ### FASE 1: CONECTAR E CRIAR TABELA
    public static Connection getConnection() {
        if (connection == null) {
            try {
                String host = System.getenv("PG_HOST");
                String port = System.getenv("5432");
                String db = System.getenv("POSTGRES_DB");
                String user = System.getenv("PGPOSTGRES_USER");
                String password = System.getenv("POSTGRES_PASSWORD");

                if (host == null || port == null || db == null || user == null || password == null) {
                    System.err.println("Variáveis de ambiente para DB não configuradas corretamente.");
                    System.exit(1);
                }

                String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Banco conectado com sucesso.");

                checkAndCreateTable();
            } catch (SQLException e) {
                System.err.println("Erro ao conectar ao banco: " + e.getMessage());
                System.exit(1);
            }
        }
        return connection;
    }

    private static void checkAndCreateTable() throws SQLException {
        String checkSQL = """
            SELECT EXISTS (
                SELECT FROM information_schema.tables 
                WHERE table_schema = 'public' AND table_name = 'users'
            );
        """;

        try (PreparedStatement stmt = connection.prepareStatement(checkSQL);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next() && !rs.getBoolean(1)) {
                System.out.println("Tabela 'users' não encontrada. Criando...");

                String createSQL = """
                    CREATE TABLE users (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        birth_date DATE NOT NULL,
                        email VARCHAR(100) UNIQUE NOT NULL,
                        password_hash TEXT NOT NULL,
                        balance DECIMAL(10,2) DEFAULT 50.00
                    );
                """;
                try (Statement createStmt = connection.createStatement()) {
                    createStmt.executeUpdate(createSQL);
                    System.out.println("Tabela 'users' criada com sucesso.");
                }
            } else {
                System.out.println("Tabela 'users' já existe.");
            }
        }
    }
    // ### FASE 2: CRIPT SENHA
    public static String criptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    public static boolean checkPassword(String senhaDigitada, String hashSalvo) {
        return BCrypt.checkpw(senhaDigitada, hashSalvo);
    }
    // #### FAZE 3: CRUD
    public static boolean insertPlayer(Player player) {
        String sql = "INSERT INTO users (name, birth_date, email, password_hash, balance) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, player.getName());
            stmt.setDate(2, Date.valueOf(player.getBirthDate()));
            stmt.setString(3, player.getEmail());
            stmt.setString(4, player.getPasswordHash());
            stmt.setDouble(5, player.getBalance());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir player: " + e.getMessage());
            return false;
        }
    }

    public static Player selectPlayerByEmail(String email) {
        String sql = "SELECT name, birth_date, email, password_hash, balance FROM users WHERE email = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Player(
                        rs.getString("name"),
                        rs.getDate("birth_date").toLocalDate(),
                        rs.getString("email"),
                        rs.getString("password_hash"),
                        rs.getDouble("balance")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar player: " + e.getMessage());
        }
        return null;
    }

    public static boolean updatePlayer(Player player) {
        String sql = "UPDATE users SET name = ?, birth_date = ?, password_hash = ?, balance = ? WHERE email = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, player.getName());
            stmt.setDate(2, Date.valueOf(player.getBirthDate()));
            stmt.setString(3, player.getPasswordHash());
            stmt.setDouble(4, player.getBalance());
            stmt.setString(5, player.getEmail());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar player: " + e.getMessage());
            return false;
        }
    }

    public static boolean deletePlayer(String email) {
        String sql = "DELETE FROM users WHERE email = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar player: " + e.getMessage());
            return false;
        }
    }

    
}
