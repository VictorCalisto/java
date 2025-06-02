import java.util.*;

public abstract class SlotMachine {
    protected String[] symbols = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K"};
    private final List<String> RANK_ORDER = Arrays.asList(
        "A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K"
    );// T de Ten, porque se eu colocar 10 sai da formatacao
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

    public boolean checkWin(String[][] matrix) {
        // Verifica linhas
        for (int row = 0; row < size; row++) {
            if (allEqualWithJoker(matrix[row])) return true;
        }

        // Verifica colunas
        for (int col = 0; col < size; col++) {
            String[] column = new String[size];
            for (int row = 0; row < size; row++) {
                column[row] = matrix[row][col];
            }
            if (allEqualWithJoker(column)) return true;
        }

        // Verifica diagonal principal
        String[] diag1 = new String[size];
        for (int i = 0; i < size; i++) diag1[i] = matrix[i][i];
        if (allEqualWithJoker(diag1)) return true;

        // Verifica diagonal secundária
        String[] diag2 = new String[size];
        for (int i = 0; i < size; i++) diag2[i] = matrix[i][size - 1 - i];
        if (allEqualWithJoker(diag2)) return true;

        return false;
    }

    // Verifica se todos os elementos são iguais ou substituíveis por '*'
    private boolean allEqualWithJoker(String[] arr) {
        String base = null;

        // Define o primeiro símbolo real (não '*') como base
        for (String s : arr) {
            if (!s.equals("*")) {
                base = s;
                break;
            }
        }
        if (base == null) return false; // todos são '*', não é uma vitória válida

        // Verifica se todos são iguais ao base ou '*'
        for (String s : arr) {
            if (!s.equals("*") && !s.equals(base)) return false;
        }
        return true;
    }

    public boolean hasBonusSequence(String[][] matrix) {
        // Verifica linhas
        for (int row = 0; row < size; row++) {
            if (isOrderedSequence(matrix[row])) return true;
        }

        // Verifica colunas
        for (int col = 0; col < size; col++) {
            String[] column = new String[size];
            for (int row = 0; row < size; row++) {
                column[row] = matrix[row][col];
            }
            if (isOrderedSequence(column)) return true;
        }

        // Verifica diagonal principal
        String[] diag1 = new String[size];
        for (int i = 0; i < size; i++) diag1[i] = matrix[i][i];
        if (isOrderedSequence(diag1)) return true;

        // Verifica diagonal secundária
        String[] diag2 = new String[size];
        for (int i = 0; i < size; i++) diag2[i] = matrix[i][size - 1 - i];
        if (isOrderedSequence(diag2)) return true;

        return false;
    }

    // Verifica se o array está em ordem crescente ou decrescente (sem '*')
    private boolean isOrderedSequence(String[] arr) {
        List<Integer> positions = new ArrayList<>();

        for (String s : arr) {
            if (s.equals("*")) return false; // não pode ter curinga
            int index = RANK_ORDER.indexOf(s);
            if (index == -1) return false; // símbolo inválido
            positions.add(index);
        }

        boolean ascending = true;
        boolean descending = true;

        for (int i = 1; i < positions.size(); i++) {
            if (positions.get(i) != positions.get(i - 1) + 1) ascending = false;
            if (positions.get(i) != positions.get(i - 1) - 1) descending = false;
        }

        return ascending || descending;
    }

}
