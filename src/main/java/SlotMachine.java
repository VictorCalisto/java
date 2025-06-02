import java.util.*;

public abstract class SlotMachine {
    protected String[] symbols = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    protected int size;       // N linhas e N colunas
    protected int maxJokers;  // max curingas na matriz
    protected Random random = new Random();

    public SlotMachine(int size, int maxJokers) {
        this.size = size;
        this.maxJokers = maxJokers;
    }

    // Gera matriz NxN com regras dadas
    public String[][] spin() {
        String[][] matrix = new String[size][size];
        int totalJokers = 0;

        for (int col = 0; col < size; col++) {
            boolean canPlaceJoker = totalJokers < maxJokers;
            boolean placeJoker = canPlaceJoker && random.nextBoolean();

            // Posicao do curinga na coluna (se houver)
            int jokerPos = placeJoker ? random.nextInt(size) : -1;
            if (placeJoker) totalJokers++;

            Set<String> usedSymbolsInCol = new HashSet<>();

            for (int row = 0; row < size; row++) {
                if (row == jokerPos) {
                    matrix[row][col] = "*"; // curinga
                } else {
                    // sorteia símbolo não repetido na coluna
                    String symbol;
                    do {
                        symbol = symbols[random.nextInt(symbols.length)];
                    } while (usedSymbolsInCol.contains(symbol));
                    usedSymbolsInCol.add(symbol);
                    matrix[row][col] = symbol;
                }
            }
        }

        return matrix;
    }

    // Imprime matriz
    public void printMatrix(String[][] matrix) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                System.out.print(matrix[row][col]);
                if (col < size - 1) System.out.print(" | ");
            }
            System.out.println();
        }
    }

    // Verifica se o player ganhou na matriz: linhas, colunas e diagonais
    // O curinga '*' substitui qualquer símbolo para formar sequência
    public boolean checkWin(String[][] matrix) {
        // Checa linhas
        for (int row = 0; row < size; row++) {
            if (allEqualWithJoker(matrix[row])) return true;
        }

        // Checa colunas
        for (int col = 0; col < size; col++) {
            String[] column = new String[size];
            for (int row = 0; row < size; row++) {
                column[row] = matrix[row][col];
            }
            if (allEqualWithJoker(column)) return true;
        }

        // Checa diagonal principal
        String[] diag1 = new String[size];
        for (int i = 0; i < size; i++) diag1[i] = matrix[i][i];
        if (allEqualWithJoker(diag1)) return true;

        // Checa diagonal secundária
        String[] diag2 = new String[size];
        for (int i = 0; i < size; i++) diag2[i] = matrix[i][size - 1 - i];
        if (allEqualWithJoker(diag2)) return true;

        return false;
    }

    // Retorna true se todos os símbolos são iguais ou curinga pode substituir
    private boolean allEqualWithJoker(String[] arr) {
        String base = null;
        for (String s : arr) {
            if (!s.equals("*")) {
                base = s;
                break;
            }
        }
        if (base == null) return false; // só curingas? não conta vitória

        for (String s : arr) {
            if (!s.equals(base) && !s.equals("*")) return false;
        }
        return true;
    }
}
