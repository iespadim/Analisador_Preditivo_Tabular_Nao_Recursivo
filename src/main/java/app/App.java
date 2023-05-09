package app;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import negocio.Gramatica;

public class App {
    private static Gramatica gramatica;
    static String stringGramatica = "";

    public static void main(String[] args) {
        desenhaApp();
        montaGramatica();
        verificaGramaticaLL();
        criaConjuntoFirstEFollow();
        montarTabelaPreditivaTabular();
        analisarEntrada();
    }

    private static void montarTabelaPreditivaTabular() {
        gramatica.montarTabelaPreditivaTabular();
    }

    private static void criaConjuntoFirstEFollow() {
        gramatica.gerarFirst();
        gramatica.gerarFollow();
        gramatica.exibirFirstFollow();

    }

    private static void verificaGramaticaLL() {
        if (gramatica.verificaGramaticaLL()) {
            System.out.println("Gramatica é LL");
        } else {
            System.out.println("Gramatica não é LL");
            System.exit(0);
        }
    }

    private static void montaGramatica() {
        gramatica = new Gramatica(stringGramatica);
    }

    private static void analisarEntrada() {
        Scanner ler = new Scanner(System.in);

        String entrada = "";
        while (entrada != "sair") {
            System.out.printf("\n(Exemplo: i+i*i )\nInforme uma entrada: ");
            entrada = ler.nextLine();

            System.out.println("Analisando entrada: " + entrada);
            if (gramatica.analisarEntrada(entrada)) {
                System.out.println("\nEntrada aceita");
            } else {
                System.out.println("\nEntrada rejeitada");
            }
        }

        ler.close();
    }

    public static void desenhaApp() {
        CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> new GrammarInputUI((String s) -> {
            stringGramatica = s;
            latch.countDown();
        }));

        try {
            latch.await(); // Wait for the result to be available.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
