import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

public class TesteRegrasSlot {

    private final EasySlot slot = new EasySlot(); // Instancia real do jogo

    @Test
    public void testarVitoriaPorLinhaComCuringa() {
        String[][] matriz = {
            {"*", "7", "7"},
            {"A", "2", "3"},
            {"K", "Q", "J"}
        };
        assertEquals(1, slot.countWins(matriz));
    }

    @Test
    public void testarDerrotaQuandoApenasCoringas() {
        String[][] matriz = {
            {"*", "*", "*"},
            {"A", "2", "3"},
            {"K", "Q", "J"}
        };
        assertEquals(0, slot.countWins(matriz));
    }

    @Test
    public void testarVitoriaPorColuna() {
        String[][] matriz = {
            {"A", "B", "C"},
            {"A", "*", "D"},
            {"A", "E", "F"}
        };
        assertEquals(1, slot.countWins(matriz));
    }

    @Test
    public void testarBonusComSequenciaAscendente() {
        String[][] matriz = {
            {"5", "6", "7"},
            {"A", "2", "3"},
            {"K", "Q", "J"}
        };
        assertEquals(3, slot.countBonusSequences(matriz));
    }

    @Test
    public void testarBonusFalhaComCuringa() {
        String[][] matriz = {
            {"5", "6", "*"},
            {"A", "2", "3"},
            {"K", "Q", "J"}
        };
        assertEquals(2, slot.countBonusSequences(matriz));
    }

    @Test
    public void testarMultiplasVitorias() {
        String[][] matriz = {
            {"A", "A", "A"},
            {"A", "A", "A"},
            {"A", "A", "A"}
        };
        assertEquals(8, slot.countWins(matriz)); // 3 linhas + 3 colunas + 2 diagonais
    }

    @Test
    public void testarMultiplosBonus() {
        String[][] matriz = {
            {"7", "8", "9"},
            {"4", "5", "6"},
            {"A", "2", "3"}
        };
        assertEquals(3, slot.countWins(matriz));
        assertEquals(6, slot.countBonusSequences(matriz));
    }
}
