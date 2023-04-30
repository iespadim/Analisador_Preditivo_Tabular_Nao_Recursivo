package app;

import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

import negocio.Grammar;

public class App {
    private static Grammar grammar;
    static String nameGrammar = "";

    public static void main(String[] args) {
        desenhaApp();
        montaGramatica();
        validateGrammar();
    }

    private static void validateGrammar() {
        if (grammar.validateGrammar()) {
            System.out.println("Gramatica é LL");
        }
    }

    private static void montaGramatica() {
        grammar = new Grammar(nameGrammar);
    }

    public static void desenhaApp() {
        CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> new GrammarInputUI((String s) -> {
            nameGrammar = s;
            latch.countDown();
        }));

        try {
            latch.await(); // Wait for the result to be available.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
