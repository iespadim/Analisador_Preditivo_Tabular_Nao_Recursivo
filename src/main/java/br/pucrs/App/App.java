package br.pucrs.App;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

public class App {
    static String stringGramatica = "";

    public static void main(String[] args) {
        desenhaApp();
        System.out.println(stringGramatica.trim());
        montaGramatica();
    }

    private static void montaGramatica() {

    }

    public static void desenhaApp() {
        CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            new GrammarInputUI((String s) -> {
                stringGramatica = s;
                latch.countDown();
            });
        });

        try {
            latch.await(); // Wait for the result to be available.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
