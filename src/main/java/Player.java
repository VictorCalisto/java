import java.math.BigDecimal;
import java.time.LocalDate;

public class Player {
    private String name;
    private LocalDate birthDate;
    private String email;
    private String passwordHash;
    private BigDecimal balance;


    // Construtor principal (com saldo explícito)
    public Player(String name, LocalDate birthDate, String email, String passwordHash, BigDecimal balance) {
        this.name = name;
        this.birthDate = birthDate;
        this.email = email;
        this.passwordHash = passwordHash;
        this.balance = balance;
    }

    // Construtor com saldo padrão R$50.00
    public Player(String name, LocalDate birthDate, String email, String passwordHash) {
        this(name, birthDate, email, passwordHash, new BigDecimal("50.00"));
    }

    public String getName() { return name; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public BigDecimal getBalance() { return balance; }

    public void setName(String name) { this.name = name; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}