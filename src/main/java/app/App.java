package app;

import negocio.Gramatica;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

public class App {
    private static Gramatica gramatica;
    static String stringGramatica = "";

    public static void main(String[] args) {
        desenhaApp();
        montaGramatica();
        verificaGramaticaLL();
        criaConjuntoFirstEFollow();

    }

    private static void criaConjuntoFirstEFollow() {
        gramatica.gerarFirst();
//        //gramatica.calcularConjuntoFollow();
        gramatica.exibirFirstFollow();
    }

    private static void verificaGramaticaLL() {
        if(gramatica.verificaGramaticaLL()){
            System.out.println("Gramatica é LL");
        }else {
            System.out.println("Gramatica não é LL");
            System.exit(0);
        }
    }

    private static void montaGramatica() {
        gramatica = new Gramatica(stringGramatica);
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
