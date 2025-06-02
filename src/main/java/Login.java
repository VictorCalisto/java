import java.util.Scanner;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

public class Login {

    private static Scanner scanner = new Scanner(System.in);

    public static Player autenticarOuCadastrar() {
        System.out.print("Digite seu email: ");
        String email = scanner.nextLine().trim().toLowerCase();

        Player existente = Database.selectPlayerByEmail(email);

        System.out.print("Digite sua senha: ");
        String senha = scanner.nextLine();

        if (existente != null) {
            if (Database.checkPassword(senha, existente.getPasswordHash())) {
                System.out.println("Login bem-sucedido!");
                return existente;
            } else {
                System.out.println("Senha incorreta. Encerrando.");
                System.exit(1);
            }
        } else {
            System.out.println("Novo cadastro...");

            System.out.print("Nome completo: ");
            String nome = scanner.nextLine().trim();

            LocalDate nascimento = null;
            while (nascimento == null) {
                System.out.print("Data de nascimento (AAAA-MM-DD): ");
                String dataStr = scanner.nextLine();
                try {
                    nascimento = LocalDate.parse(dataStr);
                } catch (DateTimeParseException e) {
                    System.out.println("Formato inválido. Tente novamente.");
                }
            }

            int idade = Period.between(nascimento, LocalDate.now()).getYears();
            if (idade < 18) {
                System.out.println("Cadastro bloqueado. Menores de idade não podem jogar.");
                System.exit(1);
            }

            String senhaCriptografada = Database.criptPassword(senha);
            Player novo = new Player(nome, nascimento, email, senhaCriptografada);
            if (Database.insertPlayer(novo)) {
                System.out.println("Cadastro realizado com sucesso!");
                return novo;
            } else {
                System.out.println("Erro ao cadastrar usuário.");
                System.exit(1);
                return null;
            }

        }

        return null;
    }
}

