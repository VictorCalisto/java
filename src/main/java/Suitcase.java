import java.util.Objects; // Necessário para equals e hashCode

/**
 * Represents a suitcase with a monetary value.
 */
class Suitcase {
    private int id;
    private double value;
    private boolean opened;

    public Suitcase(int id, double value) {
        this.id = id;
        this.value = value;
        this.opened = false;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    public boolean isOpened() {
        return opened;
    }

    public void open() {
        this.opened = true;
    }

    @Override
    public String toString() {
        return "Maleta #" + id + (opened ? " (Aberta: R$" + String.format("%.2f", value) + ")" : " (Fechada)");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Suitcase suitcase = (Suitcase) o;
        return id == suitcase.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}