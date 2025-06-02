import java.math.BigDecimal;
import java.time.LocalDate;

public class Player {
    private String name;
    private LocalDate birthDate;
    private String email;
    private String passwordHash;
    private double balance;

    public Player(String name, LocalDate birthDate, String email, String passwordHash) {
        this.name = name;
        this.birthDate = birthDate;
        this.email = email;
        this.passwordHash = passwordHash;
        this.balance = new BigDecimal("50.00");
    }

    public String getName() { return name; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public double getBalance() { return balance; }

    public void setName(String name) { this.name = name; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setBalance(double balance) { this.balance = balance; }
}