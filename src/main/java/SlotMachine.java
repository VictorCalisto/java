import java.util.*;
import java.util.stream.Collectors;

public abstract class SlotMachine {
    private final List<String> RANK_ORDER = Arrays.asList(
        "A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K"
    );
    protected int size;        // N linhas e N colunas
    protected int maxJokers;   // máximo de curingas permitidos na matriz
    protected Random random = new Random();

    public SlotMachine(int size, int maxJokers) {
        this.size = size;
        this.maxJokers = maxJokers;
    }

    public String[][] spin() {
        String[][] matrix = new String[size][size];
        int totalJokers = 0;

        for (int col = 0; col < size; col++) {
            boolean canPlaceJoker = totalJokers < maxJokers;
            boolean placeJoker = canPlaceJoker && random.nextBoolean();

            int jokerRow = placeJoker ? random.nextInt(size) : -1;
            if (placeJoker) totalJokers++;

            Set<String> usedRANK_ORDER = new HashSet<>();

            for (int row = 0; row < size; row++) {
                if (row == jokerRow) {
                    matrix[row][col] = "*";
                } else {
                    String symbol;
                    do {
                        symbol = RANK_ORDER.get(random.nextInt(RANK_ORDER.size()));
                    } while (usedRANK_ORDER.contains(symbol));
                    usedRANK_ORDER.add(symbol);
                    matrix[row][col] = symbol;
                }
            }
        }

        return matrix;
    }

    public void printMatrix(String[][] matrix) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                System.out.print(matrix[row][col]);
                if (col < size - 1) System.out.print(" | ");
            }
            System.out.println();
        }
    }

    // Retorna a quantidade de vitórias na matriz
    public int countWins(String[][] matrix) {
        int wins = 0;

        // Linhas
        for (int row = 0; row < size; row++) {
            if (allEqualWithJoker(matrix[row])) wins++;
        }

        // Colunas
        for (int col = 0; col < size; col++) {
            String[] column = new String[size];
            for (int row = 0; row < size; row++) {
                column[row] = matrix[row][col];
            }
            if (allEqualWithJoker(column)) wins++;
        }

        // Diagonal principal
        String[] diag1 = new String[size];
        for (int i = 0; i < size; i++) diag1[i] = matrix[i][i];
        if (allEqualWithJoker(diag1)) wins++;

        // Diagonal secundária
        String[] diag2 = new String[size];
        for (int i = 0; i < size; i++) diag2[i] = matrix[i][size - 1 - i];
        if (allEqualWithJoker(diag2)) wins++;

        return wins;
    }

    // Retorna a quantidade de sequências ordenadas válidas para bônus
    public int countBonusSequences(String[][] matrix) {
        int count = 0;

        // Linhas
        for (int row = 0; row < size; row++) {
            if (isOrderedSequence(matrix[row])) count++;
        }

        // Colunas
        for (int col = 0; col < size; col++) {
            String[] column = new String[size];
            for (int row = 0; row < size; row++) {
                column[row] = matrix[row][col];
            }
            if (isOrderedSequence(column)) count++;
        }

        // Diagonal principal
        String[] diag1 = new String[size];
        for (int i = 0; i < size; i++) diag1[i] = matrix[i][i];
        if (isOrderedSequence(diag1)) count++;

        // Diagonal secundária
        String[] diag2 = new String[size];
        for (int i = 0; i < size; i++) diag2[i] = matrix[i][size - 1 - i];
        if (isOrderedSequence(diag2)) count++;

        return count;
    }

    // Verifica se todos os símbolos são iguais, com curingas aceitos
    private boolean allEqualWithJoker(String[] arr) {
        String base = null;
        for (String s : arr) {
            if (!s.equals("*")) {
                base = s;
                break;
            }
        }
        if (base == null) return false; // só * não vale

        for (String s : arr) {
            if (!s.equals("*") && !s.equals(base)) return false;
        }
        return true;
    }

    // Verifica se array está em ordem crescente ou decrescente sem curinga
    private boolean isOrderedSequence(String[] arr) {
        List<String> clean = Arrays.stream(arr)
            .filter(s -> !s.equals("*"))
            .collect(Collectors.toList());

        if (clean.size() < 3) return false;

        List<Integer> indexes = new ArrayList<>();
        for (String s : clean) {
            int idx = RANK_ORDER.indexOf(s);
            if (idx == -1) return false;
            indexes.add(idx);
        }

        boolean ascending = true;
        boolean descending = true;
        for (int i = 1; i < indexes.size(); i++) {
            if (indexes.get(i) != indexes.get(i - 1) + 1) ascending = false;
            if (indexes.get(i) != indexes.get(i - 1) - 1) descending = false;
        }

        return ascending || descending;
    }
}
