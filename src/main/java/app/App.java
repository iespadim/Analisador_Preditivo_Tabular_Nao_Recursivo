package app;

import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import negocio.Gramatica;

public class App {
    private static Gramatica gramatica;
    static String stringGramatica = "";
    public static final String ENTRADA = "i+i*i";

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
        System.out.println("Analisando entrada: " + ENTRADA);
        if (gramatica.analisarEntrada(ENTRADA)) {
            System.out.println("Entrada aceita");
        } else {
            System.out.println("Entrada rejeitada");
        }
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
