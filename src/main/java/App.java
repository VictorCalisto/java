// src/main/java/App.java

public class App {

    public static void main(String[] args) {
        System.out.println("Iniciando o Easy Slot...");

        try {
            // Cria uma instância do EasySlot
            EasySlot easySlotGame = new EasySlot();
            
            // Assume que a classe SlotMachine (ou EasySlot) tem um método
            // como 'jogar()' ou 'iniciarJogo()' que executa a lógica do slot.
            // VOCÊ PRECISARÁ AJUSTAR O NOME DO MÉTODO SE FOR DIFERENTE.
            easySlotGame.iniciarJogo(); // Exemplo: Chamando um método para iniciar o jogo.

            System.out.println("Jogo Easy Slot finalizado.");

        } catch (Exception e) {
            // Captura qualquer erro que possa ocorrer durante a execução do jogo
            System.err.println("Ocorreu um erro ao rodar o Easy Slot: " + e.getMessage());
            e.printStackTrace(); // Imprime o rastreamento completo do erro para depuração
        }
    }
}