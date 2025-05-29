import java.io.Serializable;

/**
 * Represents a player in the game.
 * Note: Password handling moved to DatabaseManager and GameRanking.
 */
class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nickname;
    private double highestScore;

    public Player(String nickname, double highestScore) {
        this.nickname = nickname;
        this.highestScore = highestScore;
    }

    public Player(String nickname) { // Construtor para novos jogadores
        this(nickname, 0.0);
    }

    // Getters e Setters
    public String getNickname() {
        return nickname;
    }

    public double getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(double highestScore) {
        this.highestScore = highestScore;
    }

    @Override
    public String toString() {
        return "Apelido: " + nickname + ", Maior Pontuação: R$" + String.format("%.2f", highestScore);
    }
}